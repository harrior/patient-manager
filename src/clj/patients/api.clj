(ns patients.api
  (:require [next.jdbc.sql :as sql]
            [patients.responses :as responses]
            [patients.serializers :as serializers]
            [patients.validate :as validate]))

;;
;; Helpers
;;

(defn validate-patient-identifier
  "Throws an exception with message ':incorrect-patient-identifier'
   if the given patient-identifier is not a valid UUID."
  [patient-identifier]
  (when-not (uuid? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier"))))

(defn get-patient-by-id
   "Returns the patient record with the given identifier,
    or throws an exception with message :patient-not-found if the patient is not found."
   [db patient-identifier]
   (let [patient (-> (sql/get-by-id db :patients patient-identifier)
                     :patients/patient)]
     (if (nil? patient)
       (throw (Exception. ":patient-not-found"))
       patient)))

(defn generate-validate-error-response
  "Generates a response with a validation error status and error paths from the given patient data."
  [patient-data]
  (let [error-paths (validate/get-patient-validation-error-paths patient-data)]
    (responses/generate-response {:status :validate-error
                                  :data {:error-paths error-paths}})))

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
  (validate-patient-identifier patient-identifier)
  (let [patient (get-patient-by-id db patient-identifier)]
    (responses/generate-response {:status :ok
                                  :data {:patient (assoc patient :identifier patient-identifier)}})))

(defn create-patient
  "Creates a new patient.
   :db - the JDBC database connection
   :patient-data - the patient data (EDN) to insert into the database
   Returns UUID of created patient."
  [{:keys [db patient-data]}]
  (if-not (validate/patient-is-valid? patient-data)
    (generate-validate-error-response patient-data)

    (let [new-patient-uuid (java.util.UUID/randomUUID)
          data {:id new-patient-uuid
                :patient (assoc patient-data :identifier new-patient-uuid)}]
      (sql/insert! db :patients data)
      (responses/generate-response {:status :ok
                                    :data {:patient-identifier new-patient-uuid}}))))

(defn delete-patient
  "Deletes a patient from the database.
   :db - the JDBC database connection
   :patient-identifier - the UUID of the patient to delete
   Returns only :ok status in case of succefull deleting."
  [{:keys [db patient-identifier]}]
  (validate-patient-identifier patient-identifier)
  (sql/delete! db :patients {:id patient-identifier})
  (responses/generate-response {:status :ok}))

(defn update-patient
  "Updates a patient's data in the database.
   :db - the JDBC database connection
   :patient-identifier - the string of the patient's UUID to update
   :patient-data - the map with all patient data to insert into the database"
  [{:keys [db patient-identifier patient-data]}]
  (validate-patient-identifier patient-identifier)
  (get-patient-by-id db patient-identifier)

  (if-not (validate/patient-is-valid? patient-data)
    (generate-validate-error-response patient-data)

    (do (sql/update! db :patients {:patient patient-data} {:id patient-identifier})
        (responses/generate-response {:status :ok
                                      :data {:patients patient-data}}))))
