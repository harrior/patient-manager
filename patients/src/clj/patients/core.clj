(ns patients.core
  (:require
   [ring.adapter.jetty :as jetty]
   [patients.routes :as routes]

   [patients.config :as config]
   [patients.db])
  (:gen-class))

(defn -main
  [_]
  (println (str "Start web-server localhost:" (str config/PORT)))
  (jetty/run-jetty #'routes/app
                     {:port config/PORT
                      :join? false})
  (println (str "Web-server started.")))
