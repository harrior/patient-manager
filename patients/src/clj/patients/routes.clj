(ns patients.routes
  (:require [patients.rpc :as rpc]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.edn :as edn]
            [ring.util.response :refer [redirect]]))

(defroutes handler
  (GET "/" [] (redirect "/index.html"))
  (POST "/rpc" [method params] (rpc/rpc method params))
  (resources "/")
  (not-found "Page not found"))

;; TODO: Add wrapper for errors
(def app
  (-> handler
      edn/wrap-edn-params))
