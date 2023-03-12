(ns patients.core
  "Namespace for running an application."
  (:require [clojure.tools.logging :as log]
            [ring.adapter.jetty :as jetty]
            [patients.db :as db]
            [patients.config :as config]
            [patients.routes :as routes])
  (:gen-class))

(defn -main
  "Starts a web server on the specified port."
  [& _]
  (db/init-db config/db-spec)

  (db/make-migrations config/migratus-config)

  (jetty/run-jetty #'routes/app
                   {:port config/PORT
                    :join? false})

  (log/info (str "System started.")))
