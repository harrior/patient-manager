(ns patients.api-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [migratus.core :as migratus]
            [patients.api :as api]
            [patients.config :as config]
            [patients.responses :as responses]
            [patients.validate :as validate]))

;;
;; Data
;;

(def valid-patient-data {:name
                         [{:use "usual"
                           :text "Петров Иван Иванович" :family "Петров"
                           :given ["Иван" "Иванович"]}]
                         :address [{:use "home"
                                    :city "г. Москва"
                                    :type "physical"
                                    :state "Московская область"
                                    :line "ул. Ленина, д. 12"
                                    :postalCode "123456"
                                    :country "RU"
                                    :district "Басманный"
                                    :text "123456, Московская область, г. Москва, ул. Ленина, д. 12"}]
                         :insurance-number "9876543210123456"
                         :gender "male"
                         :birth-date "1979-01-01"})

(def invalid-patient-data {:name
                           [{:use "usual"
                             :text "Петров Иван Иванович" :family "Петров"
                             :given ["Иван" "Иванович"]}]
                           :address [{:use "home"
                                      :city "г. Москва"
                                      :type "physical"
                                      :state "Московская область"
                                      :line "ул. Ленина, д. 12"
                                      :postalCode "123456"
                                      :country "RU"
                                      :district "Басманный"
                                      :text "123456, Московская область, г. Москва, ул. Ленина, д. 12"}]
                           :insurance-number "9876543"
                           :birth-date "1979-01-01"})

(def mock-db-conn
  (jdbc/get-datasource config/db-spec-test))

(defn fix-db-server [t]
  (migratus/init config/migratus-config-test)
  (migratus/migrate config/migratus-config-test)
  (t))

(use-fixtures :each fix-db-server)


(defn parse-response
  [response]
  (let [{:keys [status headers body]} response
        body-dict (read-string body)
        api-status (:status body-dict)
        data (:data body-dict)
        patient-identifier (:patient-identifier data)]
    {:status status
     :headers headers
     :body body
     :api-status api-status
     :data data
     :patient-identifier patient-identifier}))

;; status

(deftest test-status
  (is (= (-> (api/status)
             (update :body read-string))
         {:status 200,
          :headers {"Content-Type" "application/edn"},
          :body {:status :ok, :data {:message "Backend is up and running"}}})))

;; create-patient

(deftest test-create-patient-with-valid-data
  (let [db mock-db-conn
        response (api/create-patient {:db db :patient-data valid-patient-data})
        {:keys [status headers api-status patient-identifier]}(parse-response response)
        patient-from-db (-> (sql/get-by-id db :patients patient-identifier)
                            :patients/patient)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :ok))
    (is (uuid? patient-identifier))
    (is (= (dissoc patient-from-db :identifier) valid-patient-data))))

(deftest test-create-patient-with-invalid-data
  (let [db mock-db-conn
        response (api/create-patient {:db db :patient-data invalid-patient-data})
        {:keys [status headers api-status data]} (parse-response response)
        error-paths (:error-paths data)]
    (is (= status 400))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :validate-error))
    (is (= error-paths
           '([:gender] [:insurance-number])))))

(deftest test-create-patient-with-null-db
  (let [db nil]
    (is (thrown? Exception
                 (api/create-patient {:db db :patient-data valid-patient-data})))))

;; list-patients

(deftest test-list-patients-empty-db
  (let [db mock-db-conn
        patients-list (api/list-patients {:db db})
        {:keys [status headers body]} (parse-response patients-list)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= body "{:status :ok, :data {:patients ()}}"))))

(deftest test-list-patients-db-with-patients
  (let [db mock-db-conn

        records (for [_ (range 10)] (let [uuid (random-uuid)]
                                      [uuid (assoc valid-patient-data :identifier uuid)]))

        _ (sql/insert-multi! mock-db-conn :patients [:id :patient] records)

        patients-list (api/list-patients {:db db})
        {:keys [status headers api-status data]} (parse-response patients-list)
        count-of-records (-> data
                             :patients
                             count)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :ok))
    (is (= count-of-records 10))))

(deftest test-list-patients-null-db)
  (let [db nil]
    (is (thrown? Exception
                 (api/list-patients {:db db}))))

;; get-patient

(deftest test-get-patient-return-correct-patient-on-correct-uid
  (let [db mock-db-conn
        patient-uuid (random-uuid)
        _ (sql/insert! db :patients {:id patient-uuid
                                     :patient valid-patient-data})
        get-response (api/get-patient {:db db
                                          :patient-identifier patient-uuid})

        {:keys [status headers api-status data]} (parse-response get-response)
        patient-from-db (:patient data)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :ok))
    (is (= (dissoc patient-from-db :identifier) valid-patient-data))))

(deftest test-get-patient-throw-exception-on-incorrect-patient-uid
  (let [db mock-db-conn]

    (is (thrown? Exception (api/get-patient {:db db
                                             :patient-identifier "just-string"})))
    (is (thrown? Exception (api/get-patient {:db db
                                             :patient-identifier 12312313})))
    (is (thrown? Exception (api/get-patient {:db db
                                             :patient-identifier nil})))))

(deftest test-get-patient-throw-exception-on-patient-not-found
  (let [db mock-db-conn]
    (is (thrown-with-msg? Exception #":patient-not-found"
                          (api/get-patient {:db db
                                            :patient-identifier (random-uuid)})))))

(deftest test-get-patient-null-db
  (let [db nil
        patient-uuid (random-uuid)]
    (is (thrown? Exception
                 (api/get-patient {:db db
                                   :patient-identifier patient-uuid})))))

;; delete-patient

(deftest test-delete-patient-successful-deletion
  (let [db mock-db-conn
        patient-uuid (random-uuid)
        _ (sql/insert! db :patients {:id patient-uuid
                                     :patient valid-patient-data})
        delete-response (api/delete-patient {:db db
                                             :patient-identifier patient-uuid})
        {:keys [status headers api-status data]} (parse-response delete-response)
        patient-in-db (sql/get-by-id mock-db-conn :patients (random-uuid))]

    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :ok))
    (is (nil? patient-in-db))))

(deftest test-delete-patient-throw-exception-on-incorrect-patient-uid
  (let [db mock-db-conn]
    (is (thrown? Exception (api/delete-patient {:db db
                                                :patient-identifier "just-string"})))
    (is (thrown? Exception (api/delete-patient {:db db
                                                :patient-identifier 12312313})))
    (is (thrown? Exception (api/delete-patient {:db db
                                                :patient-identifier nil})))))

(deftest test-delete-patient-null-db
  (let [db nil
        patient-uuid (random-uuid)]
    (is (thrown? Exception
                 (api/delete-patient {:db db
                                      :patient-identifier patient-uuid})))))

;; update-patient

(deftest test-update-patient-incorrect-uuid
  (let [db mock-db-conn]

    (is (thrown? Exception (api/update-patient {:db db
                                                :patient-identifier "just-string"})))
    (is (thrown? Exception (api/update-patient {:db db
                                                :patient-identifier 12312313})))
    (is (thrown? Exception (api/update-patient {:db db
                                                :patient-identifier nil})))))

(deftest test-update-patient-nonexistent-patient
  (let [db mock-db-conn]
    (is (thrown-with-msg? Exception #":patient-not-found"
                          (api/update-patient {:db db
                                               :patient-identifier (random-uuid)})))))

(deftest test-update-patient-with-invalid-data
  (let [db mock-db-conn
        patient-uuid (random-uuid)

        _ (sql/insert! db :patients {:id patient-uuid
                                     :patient valid-patient-data})

        response (api/update-patient {:db db
                                      :patient-identifier patient-uuid
                                      :patient-data invalid-patient-data})
        {:keys [status headers api-status data]} (parse-response response)
        error-paths (:error-paths data)]
    (is (= status 400))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :validate-error))
    (is (= error-paths
           '([:gender] [:insurance-number])))))

(deftest test-update-patient-with-valid-data
  (let [db mock-db-conn
        patient-uuid (random-uuid)

        _ (sql/insert! db :patients {:id patient-uuid
                                     :patient valid-patient-data})

        changed-valid-patient-data (assoc valid-patient-data
                                          :birth-date
                                          "1920-01-01")

        response (api/update-patient {:db db
                                      :patient-identifier patient-uuid
                                      :patient-data changed-valid-patient-data})
        {:keys [status headers api-status]} (parse-response response)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :ok))))

(deftest test-update-patient-with-null-db
  (let [db nil
        patient-uuid (random-uuid)]
    (is (thrown? Exception
                 (api/update-patient {:db db :patient-identifier patient-uuid
                                      :patient-data valid-patient-data})))))

;;
;; Helpers
;;

;; validate-patient-identifier

(deftest test-validate-patient-identifier-correct-uuid
    (let [uuid (random-uuid)]
      (is (nil? (api/validate-patient-identifier uuid)))))

(deftest test-validate-patient-identifier-incorrect-uuid
    (let [uuid "not-a-uuid"]
      (is (thrown? Exception (api/validate-patient-identifier uuid)))))

;; get-patient-by-id

(deftest test-get-patient-by-id-success
    (let [db mock-db-conn
          patient-uuid (random-uuid)
          _ (sql/insert! db :patients {:id patient-uuid
                                       :patient valid-patient-data})]
      (is (= valid-patient-data (dissoc (api/get-patient-by-id db patient-uuid) :identifier)))))

(deftest test-get-patient-by-incorrect-uuid
    (let [db mock-db-conn]
      (is (thrown? Exception (api/get-patient-by-id db "invalid-identifier")))
      (is (thrown? Exception (api/get-patient-by-id db "")))
      (is (thrown? Exception (api/get-patient-by-id db 123)))
      (is (thrown? Exception (api/get-patient-by-id db nil)))))

;; generate-validate-error-response

(deftest test-generate-validate-error-response-valid
      (is (= (responses/generate-response {:status :validate-error
                                           :data {:error-paths (validate/get-patient-validation-error-paths valid-patient-data)}})
             (api/generate-validate-error-response valid-patient-data))))

(deftest test-generate-validate-error-response-invalid
    (is (= (responses/generate-response {:status :validate-error
                                         :data {:error-paths (validate/get-patient-validation-error-paths invalid-patient-data)}})
           (api/generate-validate-error-response invalid-patient-data))))
