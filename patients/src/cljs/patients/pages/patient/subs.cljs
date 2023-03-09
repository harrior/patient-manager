(ns patients.pages.patient.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 ::get-patient-uid
 (fn [db _]
   (first (get-in db [:app :page-params]))))

(rf/reg-sub
 ::create-patient?
 (fn []
   (rf/subscribe [::get-patient-uid]))
 (fn [patient-uid]
   (nil? patient-uid)))