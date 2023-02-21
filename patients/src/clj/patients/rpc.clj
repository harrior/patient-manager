(ns patients.rpc
  (:require [next.jdbc.sql :as sql]
            [patients.db :as patients.db]))

(defn generate-response [{:keys [status data]}]
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str (merge {:status status}
                        (when-not (nil? data)
                          {:data data})))})

(defn list-patients
  [{:keys [db limit offset]}]
  (let [patients
        (->> (sql/find-by-keys db :patients :all {:limit limit
                                                  :offset offset})
             (map :patients/patient))]
    (generate-response {:status :ok
                        :data {:patients patients}})))

(defn get-patient
  [{:keys [db patient-identifier]}]
  (when-not (string? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))

  (let [patient-uuid (parse-uuid patient-identifier)

        patient (-> (sql/get-by-id db :patients patient-uuid)
                    :patients/patient)]
    (when (nil? patient)
      (throw (Exception. ":user-not-found")))

    (generate-response {:status :ok
                        :data {:patients patient}})))

(defn create-patient
  [{:keys [db patient-data]}]
  ;; TODO: Add data validation
  (let [new-patient-uuid (java.util.UUID/randomUUID)
        data {:id new-patient-uuid
              :patient (assoc patient-data :identifier new-patient-uuid)}]
    (sql/insert! db :patients data)
    (generate-response {:status :ok
                        :data {:patient-identifier (str new-patient-uuid)}})))

(defn delete-patient
  [{:keys [db patient-identifier]}]
  (when-not (string? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))
  (let [patient-uuid (parse-uuid patient-identifier)
        deleted-count (-> (sql/delete! db :patients {:id patient-uuid})
                          :next.jdbc/update-count)]
    (when (= deleted-count 0)
      (throw (Exception. ":user-not-found")))

    (generate-response {:status :ok})))

(defn update-patient
  [{:keys [db patient-identifier patient-data]}]
  (when-not (string? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))
  (let [patient-uuid (parse-uuid patient-identifier)

        patient (-> (sql/get-by-id db :patients patient-uuid)
                    :patients/patient)]
    (when (nil? patient)
      (throw (Exception. ":user-not-found")))

    (sql/update! db :patients {:patient patient-data} {:id patient-uuid})
    (generate-response {:status :ok
                        :data {:patients patient-data}})))

(defn rpc
  [method params]
  (try
    (let [ext-params (merge params
                            {:db patients.db/db-conn})]
      (case method
        :list-patients (list-patients ext-params)
        :get-patient (get-patient ext-params)
        :create-patient (create-patient ext-params)
        :delete-patient (delete-patient ext-params)
        :update-patient (update-patient ext-params)
        (throw (Exception. (str "Method " method " not implemented.")))))
    (catch Exception e (generate-response {:status :error
                                           :data {:text (.getMessage e)}}))))