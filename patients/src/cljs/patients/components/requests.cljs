(ns patients.components.requests
  (:require [ajax.edn :as edn]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [patients.components.ui-elements :as ui]))

(def RPC-ENDPOINT "http://localhost:8000/rpc")

;;
;; Events
;;

(rf/reg-event-fx
 ::show-error-popup
 (fn [_ [_ message]]
   (ui/show-error-popup message)
   {}))

(rf/reg-event-fx
 ::show-success-popup
 (fn [_ [_ message]]
   (ui/show-success-popup message)
   {}))

;;
;;
;;

(rf/reg-event-fx
 ::invoke
 (fn [_ [_ request on-success on-failure]]
   {:http-xhrio {:method          :post
                 :uri             RPC-ENDPOINT
                 :params          request
                 :timeout         5000
                 :format          (edn/edn-request-format)
                 :response-format (edn/edn-response-format)
                 :on-success      on-success
                 :on-failure      on-failure}}))