(ns patients.core
  (:require
   [ring.adapter.jetty :as jetty]
   [patients.routes :as routes])
  (:gen-class))


(def PORT 8000)


(defn -main 
  []
  (jetty/run-jetty routes/app
                   {:port PORT
                    :join? true}))
