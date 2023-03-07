(ns patients.rpc
  "Namespace for handling RPC requests related to the Patients database."
  (:require [clojure.tools.logging :as log]
            [patients.api :as api]
            [patients.db :as patients.db]))

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

;; RPC Router

(defn rpc-handler
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
        :list-patients (api/list-patients ext-params)
        :get-patient (api/get-patient ext-params)
        :create-patient (api/create-patient ext-params)
        :delete-patient (api/delete-patient ext-params)
        :update-patient (api/update-patient ext-params)
        (throw (Exception. (str "Method " method " not implemented.")))))
    (catch Exception e (do (log/debug (format "Wrong rpc request. Method %s. Params %s Error: %s"
                                              method params (.getMessage e)))
                           (generate-response {:status :error
                                               :data {:text (.getMessage e)}})))))