(ns patients.rpc
  "Namespace for handling RPC requests related to the Patients database."
  (:require [clojure.tools.logging :as log]
            [next.jdbc.sql :as sql]
            [patients.db :as patients.db]
            [patients.validate :as validate]))

;; Responce

(defn generate-response
  "Returns a response map containing the status, headers, and body.
   :status - status of response (:ok or :error)
   :headers - map of HTTP response headers
   :body - EDN-encoded response body. The body is a string representation of a
   Clojure data structure that contains the :status key with the value of the
   response status and, if present, the :data key with the value of the response
   data. This string can be directly sent as the response body of an HTTP
   request, and can be parsed back into a Clojure data structure using the EDN
   reader.

   Args:
   - `status` (required): The status of the response, which can be one of :ok or
   :error.
   - `data` (optional): The data to include in the response body."
  [{:keys [status data]}]

  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str (merge {:status status}
                        (when-not (nil? data)
                          {:data data})))})

;; Serialisers

(defn patient-serialiser
  "Serialises patient data.
   Accepts a `patient` record, and returns a serialised flat map of patient data"
  [patient]
  (let [patient* (:patients/patient patient)
        insurance-number (:insurance-number patient*)
        fullname (-> patient*
                     :name
                     first
                     :text)
        address (-> patient*
                    :address
                    first
                    :text)
        gender (:gender patient*)
        birth-date (:birth-date patient*)
        identifier (:patients/id patient)]
    {:insurance-number insurance-number
     :fullname fullname
     :address address
     :gender gender
     :birth-date birth-date
     :identifier identifier}))

;; API Methods

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
             (map patient-serialiser))]
    (generate-response {:status :ok
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

    (generate-response {:status :ok
                        :data {:patient patient}})))

(defn create-patient
  "Creates a new patient.
   :db - the JDBC database connection
   :patient-data - the patient data (EDN) to insert into the database
   Returns UUID of created patient."
  [{:keys [db patient-data]}]
  (when-not (validate/validate-patient patient-data)
    (throw  (Exception. ":not-valid-patient-date")))
  (let [new-patient-uuid (java.util.UUID/randomUUID)
        data {:id new-patient-uuid
              :patient (assoc patient-data :identifier new-patient-uuid)}]
    (sql/insert! db :patients data)
    (generate-response {:status :ok
                        :data {:patient-identifier new-patient-uuid}})))

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
    (generate-response {:status :ok})))

(defn update-patient
  "Updates a patient's data in the database.
   :db - the JDBC database connection
   :patient-identifier - the string of the patient's UUID to update
   :patient-data - the map with all patient data to insert into the database"
  [{:keys [db patient-identifier patient-data]}]
  (when-not (uuid? patient-identifier)
    (throw (Exception. ":incorrect-patient-identifier")))
  (when-not (validate/validate-patient patient-data)
    (throw  (Exception. ":not-valid-patient-date")))
  (let [patient (-> (sql/get-by-id db :patients patient-identifier)
                    :patients/patient)]
    (when (nil? patient)
      (throw (Exception. ":user-not-found")))

    (sql/update! db :patients {:patient patient-data} {:id patient-identifier})
    (generate-response {:status :ok
                        :data {:patients patient-data}})))

;; RPC Router

(defn rpc
  "Executes an RPC method specified by the `method` keyword with the given parameters `params`.
   Add additional keys (as a database connection).

  Args:
    method (keyword): The RPC method to call.
    params (map): A map of parameters to pass to the RPC method.

  Returns:
    A map containing the response data for the RPC method. The map has the following keys:
      - :status: The HTTP status code for the response.
      - :headers: A map of response headers (application/edn)
      - :body: A string representation of the response data.

  Raises:
    Exception: If the `method` is not recognized or if an exception is thrown while executing the RPC method."
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
    (catch Exception e (do (log/debug (format "Wrong rpc request. Method %s. Params %s Error: %s"
                                              method params (.getMessage e)))
                           (generate-response {:status :error
                                               :data {:text (.getMessage e)}})))))