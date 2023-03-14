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
  []
  (let [active-page @(rf/subscribe [:active-page])]
    (case active-page
      :patients (do
                  (patient-grid/init)
                  [patient-grid/main])
      :patient (do
                 (patient-single/init)
                 [patient-single/main]))))