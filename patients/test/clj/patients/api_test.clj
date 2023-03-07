(ns patients.api-test
  (:require [clojure.test :refer :all]
            [migratus.core :as migratus]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [patients.api :as api]
            [patients.config :as config]))

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

;;
;; Fixtures
;;

(defn mock-db-conn []
  (jdbc/get-datasource config/db-spec-test))

(defn fix-db-data [t]
  (migratus/init config/migratus-config-test)
  (migratus/migrate config/migratus-config-test)
  (t))

(defn fix-clear-test-db [t]
  (t)
  (migratus/down config/migratus-config-test))

(use-fixtures :once fix-db-data fix-clear-test-db)


;;
;; create-patient
;;

(deftest test-create-patient-with-valid-data
  (let [db (mock-db-conn)
        result (patients.api/create-patient {:db db :patient-data valid-patient-data})
        {:keys [status headers body]} result
        body-dict (read-string body)
        api-status (:status body-dict)
        data (:data body-dict)
        patient-identifier (:patient-identifier data)
        patient-from-db (-> (sql/get-by-id db :patients patient-identifier)
                            :patients/patient)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :ok))
    (is (uuid? patient-identifier))
    (is (= (dissoc patient-from-db :identifier) valid-patient-data))))

(deftest test-create-patient-with-invalid-data
  (let [db (mock-db-conn)
        result (patients.api/create-patient {:db db :patient-data invalid-patient-data})
        {:keys [status headers body]} result
        body-dict (read-string body)
        api-status (:status body-dict)
        data (:data body-dict)
        error-paths (:error-paths data)]
    (is (= status 200))
    (is (= headers {"Content-Type" "application/edn"}))
    (is (= api-status :validate-error))
    (is (= error-paths
           '([:gender] [:insurance-number])))))

(deftest test-create-patient-with-invalid-db-connection
  (let [db nil]
    (is (thrown? Exception
                 (patients.api/create-patient {:db db :patient-data valid-patient-data})))))


;;
;; update-patient
;;
