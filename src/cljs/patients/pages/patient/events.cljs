(ns patients.pages.patient.events
  (:require [re-frame.core :as rf]
            [patients.components.requests :as rpc]
            [patients.pages.patient.converters :as conv]
            [patients.components.popup :as popup]
            [patients.nav :as nav]))

;;
;; Events
;;

(rf/reg-event-db
 ::clear-form
 (fn [db]
   (merge db
          {:patient-address {}
           :patient-data {}
           :patient-name {}})))

(rf/reg-event-db
 ::clean-form-errors
 (fn [db [_]]
   (assoc db :errors {})))

(rf/reg-event-db
 ::show-form-validation-errors
 (fn [db [_ {:keys [error-paths]} transform-fn]]
   (let [errors-map (transform-fn error-paths)]
     (assoc db :errors errors-map))))

;; Check CRUD response

(rf/reg-event-fx
 ::successful-update
 (fn [_ _]
   {:dispatch [::popup/show-success-popup :app/success-updated]}))

(rf/reg-event-fx
 ::successful-create
 (fn [_ [_ {:keys [data]}]]
   {:dispatch-n [[::popup/show-success-popup :app/success-created]
                 [::nav/set-active-page :patient (:patient-identifier data)]]}))

(rf/reg-event-fx
 ::successful-delete
 (fn [_ _]
   {:dispatch-n [[::popup/show-success-popup :app/success-removed]
                 [::nav/set-active-page :patients]]}))

(rf/reg-event-fx
 ::error-response
 (fn [_ [_ {:keys [response]}]]
   (let [{:keys [status data]} response]
     (case status
       :validate-error {:dispatch-n [[::show-form-validation-errors data conv/transform-error-path]
                                     [::popup/show-error-popup :app/validation-error]]}
       {:dispatch [::popup/show-error-popup :app/bad-request]}))))

;; CRUD

(rf/reg-event-db
 ::load-patient-data
 (fn [db [_ response]]
   (let [patient (-> response
                     :data
                     :patient
                     conv/convert-response-to-patient-data)]
     (merge db patient))))

(rf/reg-event-fx
 ::get-patient
 (fn [_ [_ patient-uid]]
   {:dispatch [::rpc/invoke
               {:method :get-patient
                :params {:patient-identifier patient-uid}}
               [::load-patient-data]
               [::error-response]]}))

(rf/reg-event-fx
 ::create-patient
 (fn [{:keys [db]} _]
   (let [prepared-data (conv/prepare-patient-data-to-request db)
         request {:method :create-patient
                  :params {:patient-data prepared-data}}]
     {:dispatch-n [[::clean-form-errors]
                   [::rpc/invoke
                    request
                    [::successful-create]
                    [::error-response]]]})))

(rf/reg-event-fx
 ::update-patient
 (fn [{:keys [db]} [_ patient-uid]]
   (let [prepared-data (conv/prepare-patient-data-to-request db)]
     {:dispatch-n [[::clean-form-errors]
                   [::rpc/invoke
                    {:method :update-patient
                     :params {:patient-identifier patient-uid
                              :patient-data prepared-data}}
                    [::successful-update]
                    [::error-response]]]})))

(rf/reg-event-fx
 ::delete-patient
 (fn [_ [_ patient-uid]]
   {:dispatch-n [[::clean-form-errors]
                 [::rpc/invoke
                  {:method :delete-patient
                   :params {:patient-identifier patient-uid}}
                  [::successful-delete]
                  [::error-response]]]}))