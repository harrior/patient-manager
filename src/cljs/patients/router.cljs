(ns patients.router
  "Provides a router for app pages."
  (:require [re-frame.core :as rf]
            [patients.pages.patient.core :as patient-single]
            [patients.pages.patient-grid :as patient-grid]))

;;
;; Subs
;;

(rf/reg-sub
 :active-page
 (fn [db _]
   (get-in db [:app :active-page])))

(rf/reg-sub
 :page-params
 (fn [db _]
   (get-in db [:app :page-params])))

;;
;; Router
;;

(defn router
  "Returns a component for the currently active page."
  []
  (let [active-page @(rf/subscribe [:active-page])]
    (case active-page
      :patients [patient-grid/main]
      :patient [patient-single/main])))