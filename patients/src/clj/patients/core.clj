(ns patients.core
  "Namespace for handling patient data and serving it through a web application."
  (:require
   [ring.adapter.jetty :as jetty]
   [patients.routes :as routes]

   [patients.config :as config]
   [patients.db])
  (:gen-class))

(defn -main
  "Starts a web server on the specified port."
  [& _]
  (println (str "Start web-server localhost:" (str config/PORT)))
  (jetty/run-jetty #'routes/app
                     {:port config/PORT
                      :join? false})
  (println (str "Web-server started.")))
