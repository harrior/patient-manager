(ns patients.responses)

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
