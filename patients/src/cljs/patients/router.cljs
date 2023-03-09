(ns patients.router
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

;;
;; Router
;;

(defn router
  []
  (let [current-page @(rf/subscribe [:active-page])]
    (case current-page
      :patients [patient-grid/main]
      :patient [patient-single/main]
      [patient-grid/main])))