(ns patients.responses
  "Namespace for generating responses for HTTP requests.")

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
                      :validate-error 500
                      (throw (Exception. (str "Invalid status value: " status))))]
    {:status status-code
     :headers {"Content-Type" "application/edn"}
     :body (pr-str (merge {:status status}
                          (when data
                            {:data data})))}))