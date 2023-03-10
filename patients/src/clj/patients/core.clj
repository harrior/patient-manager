(ns patients.core
  "Namespace for handling patient data and serving it through a web application."
  (:require [clojure.tools.logging :as log]
            [ring.adapter.jetty :as jetty]
            [patients.db :as db]
            [patients.config :as config]
            [patients.routes :as routes])
  (:gen-class))

(defn -main
  "Starts a web server on the specified port."
  [& _]
  (jetty/run-jetty #'routes/app
                   {:port config/PORT
                    :join? false})
  (db/make-migrations config/migratus-config)
  (log/info (str "System started.")))
