(ns patients.routes
  (:require [compojure.core :refer [GET POST defroutes]]
            [clojure.java.io :as io]
            [compojure.route :refer [not-found resources]] 
            [ring.util.response :refer [redirect]]
            [patients.handlers :as handlers]))

(defroutes app
  (GET "/" [] (redirect "/index.html"))
  (resources "/")
  (not-found "Page not found"))
