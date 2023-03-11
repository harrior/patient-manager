(ns patients.routes
  "Namespace for defining the routes and request handler."
  (:require [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.edn :as edn]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.util.response :refer [redirect]]
            [patients.rpc :as rpc]))

(defroutes handler
  "Defines the routes and request handlers"
  (GET "/" [] (redirect "/index.html"))
  (POST "/rpc" [method params] (rpc/rpc-handler method params))
  (resources "/")
  (not-found "Page not found"))

(def app
  "Defines the top-level middleware stack."
  (-> handler
      (wrap-stacktrace)
      edn/wrap-edn-params))
