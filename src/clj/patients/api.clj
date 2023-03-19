(ns patients.api
  "This module contains API functions."
  (:require [next.jdbc.sql :as sql]
            [patients.responses :as responses]
            [patients.serializers :as serializers]
            [patients.validate :as validate]))

;;
;; Helpers
;;

(defn get-patient-by-id
  "Returns the patient record with the given identifier, or nil if the patient is not found."
  [db patient-identifier]
  (try
    (-> (sql/get-by-id db :patients patient-identifier)
        :patients/patient)
    (catch Exception _ (identity nil))))

;;
;; Status
;;

(defn status
  "Returns a response indicating that the backend is up and running."
  []
  (responses/generate-response {:status :ok
                                :data {:message "Backend is up and running"}}))

;;
;; CRUD
;;

(defn list-patients
  "Returns a response containing a list of patients.
   :db - the JDBC database connection
   Returns serialised list of patients' maps."
  [{:keys [db]}]
  (let [patients (->> (sql/find-by-keys db :patients :all)
                      (map serializers/patient-serialiser))]
    (responses/generate-response {:status :ok
                                  :data {:patients patients}})))

(defn get-patient
  "Returns a response containing a single patient.
   :db - the JDBC database connection
   :patient-identifier - UUID of the patient to retrieve
   Returns patient's map."
  [{:keys [db patient-identifier]}]
  (cond (not (validate/patient-identifier-valid? patient-identifier))
        (responses/generate-incorrect-patient-id-error-response)

        (nil? (get-patient-by-id db patient-identifier))
        (responses/generate-not-found-error-response)

        :else
        (let [patient (get-patient-by-id db patient-identifier)]
          (responses/generate-response {:status :ok
                                        :data {:patient patient}}))))

(defn create-patient
  "Creates a new patient.
   :db - the JDBC database connection
   :patient-data - the patient data (EDN) to insert into the database
   Returns UUID of created patient."
  [{:keys [db patient-data]}]
  (if-not (validate/patient-is-valid? patient-data)
    (responses/generate-validate-error-response patient-data)

    (let [new-patient-uuid (java.util.UUID/randomUUID)
          data {:id new-patient-uuid
                :patient patient-data}]
      (sql/insert! db :patients data)
      (responses/generate-response {:status :ok
                                    :data {:patient-identifier new-patient-uuid}}))))

(defn delete-patient
  "Deletes a patient from the database.
   :db - the JDBC database connection
   :patient-identifier - the UUID of the patient to delete
   Returns only :ok status in case of succefull deleting."
  [{:keys [db patient-identifier]}]
  (if-not (validate/patient-identifier-valid? patient-identifier)
    (responses/generate-incorrect-patient-id-error-response)
    (do
      (sql/delete! db :patients {:id patient-identifier})
      (responses/generate-response {:status :ok}))))

(defn update-patient
  "Updates a patient's data in the database.
   :db - the JDBC database connection
   :patient-identifier - the string of the patient's UUID to update
   :patient-data - the map with all patient data to insert into the database"
  [{:keys [db patient-identifier patient-data]}]

  (cond (not (validate/patient-identifier-valid? patient-identifier))
        (responses/generate-incorrect-patient-id-error-response)

        (nil? (get-patient-by-id db patient-identifier))
        (responses/generate-not-found-error-response)

        (not (validate/patient-is-valid? patient-data))
        (responses/generate-validate-error-response patient-data)

        :else
        (do (sql/update! db :patients
                         {:patient patient-data}
                         {:id patient-identifier})
            (responses/generate-response {:status :ok
                                          :data {:patients patient-data}}))))