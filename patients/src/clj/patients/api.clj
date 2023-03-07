(ns patients.api
  (:require [next.jdbc.sql :as sql]
            [patients.rpc :as rpc]
            [patients.serializers :as serializers]
            [patients.validate :as validate]))

(defn list-patients
  "Returns a response containing a list of patients.
   :db - the JDBC database connection
   :limit - the maximum number of patients to return
   :offset - the index of the first patient to return
   Returns serialised list of patients' maps."
  [{:keys [db limit offset]}]
  (let [patients
        (->> (sql/find-by-keys db :patients :all {:limit limit
                                                  :offset offset})
             (map serializers/patient-serialiser))]
    (rpc/generate-response {:status :ok
                            :data {:patients patients}})))

(defn get-patient
  "Returns a response containing a single patient.
   :db - the JDBC database connection
   :patient-identifier - the string of the patient's UUID to retrieve
   Returns patient's map."
  [{:keys [db patient-identifier]}]
  (when-not (uuid? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))

  (let [patient (-> (sql/get-by-id db :patients patient-identifier)
                    :patients/patient
                    (assoc :identifier patient-identifier))]
    (when (nil? patient)
      (throw (Exception. ":patient-not-found")))

    (rpc/generate-response {:status :ok
                            :data {:patient patient}})))

(defn create-patient
  "Creates a new patient.
   :db - the JDBC database connection
   :patient-data - the patient data (EDN) to insert into the database
   Returns UUID of created patient."
  [{:keys [db patient-data]}]
  (if-not (validate/patient-is-valid? patient-data)
    (let [error-paths (validate/get-patient-validation-error-paths patient-data)]
      (rpc/generate-response {:status :validate-error
                              :data {:error-paths error-paths}}))

    (let [new-patient-uuid (java.util.UUID/randomUUID)
          data {:id new-patient-uuid
                :patient (assoc patient-data :identifier new-patient-uuid)}]
      (sql/insert! db :patients data)
      (rpc/generate-response {:status :ok
                              :data {:patient-identifier new-patient-uuid}}))))

(defn delete-patient
  "Deletes a patient from the database.
   :db - the JDBC database connection
   :patient-identifier - the UUID of the patient to delete
   Returns only :ok status in case of succefull deleting."
  [{:keys [db patient-identifier]}]
  (when-not (uuid? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))
  (let [deleted-count (-> (sql/delete! db :patients {:id patient-identifier})
                          :next.jdbc/update-count)]
    (when (= deleted-count 0)
      (throw (Exception. ":user-not-found")))
    (rpc/generate-response {:status :ok})))

(defn update-patient
  "Updates a patient's data in the database.
   :db - the JDBC database connection
   :patient-identifier - the string of the patient's UUID to update
   :patient-data - the map with all patient data to insert into the database"
  [{:keys [db patient-identifier patient-data]}]
  (when-not (uuid? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))
  (let [patient (-> (sql/get-by-id db :patients patient-identifier)
                    :patients/patient)]
    (when (nil? patient)
      (throw (Exception. ":user-not-found")))

    (if-not (validate/patient-is-valid? patient-data)
      (let [error-paths (validate/get-patient-validation-error-paths patient-data)]
        (rpc/generate-response {:status :validate-error
                                :data {:error-paths error-paths}}))

      (do (sql/update! db :patients {:patient patient-data} {:id patient-identifier})
          (rpc/generate-response {:status :ok
                                  :data {:patients patient-data}})))))