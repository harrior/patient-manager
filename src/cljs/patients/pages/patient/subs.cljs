(ns patients.pages.patient.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::patient-uid
 (fn [db _]
   (first (get-in db [:app :page-params]))))

(rf/reg-sub
 ::new-patient?
 (fn []
   (rf/subscribe [::patient-uid]))
 (fn [patient-uid]
   (nil? patient-uid)))