(ns patients.responses
  "Namespace for generating responses for HTTP requests."
  (:require [patients.validate :as validate]))

(defn generate-response
  "Returns a response map containing the status, headers, and body.

  Args:
    - `status` (required): The status of the response, which can be one of :ok or :error.
    - `data` (optional): The data to include in the response body.

  Returns:
    A map containing the response data with the following keys:
      - :status: The HTTP status code for the response.
      - :headers: A map of response headers (application/edn)
      - :body: A string representation of the response data.

  Raises:
    Exception: If `status` parameter is invalid."
  [{:keys [status data]}]
  (let [status-code (case status
                      :ok 200
                      :error 500
                      :validate-error 400
                      (throw (Exception. (str "Invalid status value: " status))))]
    {:status status-code
     :headers {"Content-Type" "application/edn"}
     :body (pr-str (merge {:status status}
                          (when data
                            {:data data})))}))

(defn generate-validate-error-response
  "Generates a response with a validation error status and error paths from the given patient data."
  [patient-data]
  (let [error-paths (validate/get-patient-validation-error-paths patient-data)]
    (generate-response {:status :validate-error
                                  :data {:error-paths error-paths}})))

(defn generate-incorrect-patient-id-error-response
  "Generates an error response indicating an incorrect patient identifier"
  []
  (generate-response {:status :error :data {:message :incorrect-patient-identifier}}))

(defn generate-not-found-error-response
  "Generates an error response indicating the requested patient isn't found"
  []
  (generate-response {:status :error :data {:message "Patient not found"}}))