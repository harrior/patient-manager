(ns patients.routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [patients.handlers :as handlers]))

(defroutes app
  (GET "/" [] handlers/index)
  (GET "/parients" [] handlers/patients)
  #_(resources "/")
  (not-found "Page not found"))
