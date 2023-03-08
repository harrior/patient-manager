(ns patients.rpc
  "Namespace for handling RPC requests related to the Patients database."
  (:require [clojure.tools.logging :as log]
            [patients.api :as api]
            [patients.db :as patients.db]
            [patients.responses :as responses]))

;; RPC Router

(defn handle-rpc-errors
  "Helper function for handling errors in the `rpc-handler` function.
   Returns a map with an error message."
  [method params e]
  (log/debug (format "Wrong rpc request. Method %s. Params %s Error: %s"
                     method params (.getMessage e)))
  (responses/generate-response {:status :error
                                :data {:text (.getMessage e)}}))

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
    (catch Exception e (handle-rpc-errors method params e))))