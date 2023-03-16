(ns patients.events
  "Provides HTTP event handlers for EDN RPC calls."
  (:require [ajax.edn :as edn]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]))

(def RPC-ENDPOINT "/rpc")

(defn- get-current-api-url
  {:doc "Returns the current API URL for the patients app."}
  []
  (let [protocol js/window.location.protocol
        hostname js/window.location.hostname
        port js/window.location.port]
    (if port
      (str protocol "//" hostname ":" port RPC-ENDPOINT)
      (str protocol "//" hostname RPC-ENDPOINT))))


(rf/reg-event-fx
 ::invoke
 ^{:doc
   "Invoking an HTTP request with EDN format for request and response payloads.
  - `request`: a map with the request payload
  - `on-success`: the success callback event to be dispatched upon receiving a successful response
  - `on-failure`: the failure callback event to be dispatched upon receiving a failed response"}
 (fn [_ [_ {:keys [request on-success on-failure]}]]
   (let [uri (get-current-api-url)]
     {:http-xhrio {:method          :post
                   :uri             uri
                   :params          request
                   :timeout         5000
                   :format          (edn/edn-request-format)
                   :response-format (edn/edn-response-format)
                   :on-success      on-success
                   :on-failure      on-failure}})))