(ns patients.core
  (:require
   [ring.adapter.jetty :as jetty]
   [patients.routes :as routes])
  (:gen-class))


(defn -main
  [& args]
  (jetty/run-jetty routes/app
                   {:port 8080
                    :join? true}))
