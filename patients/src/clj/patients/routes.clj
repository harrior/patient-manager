(ns patients.routes
  "Namespace for defining the routes and request handler."
  (:require [patients.rpc :as rpc]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.edn :as edn]
            [ring.util.response :refer [redirect]]))

(defroutes handler
  "Defines the routes and request handlers"
  (GET "/" [] (redirect "/index.html"))
  (POST "/rpc" [method params] (rpc/rpc method params))
  (resources "/")
  (not-found "Page not found"))

;; TODO: Add wrapper for errors
(def app
  "Defines the top-level middleware stack"
  (-> handler
      edn/wrap-edn-params))