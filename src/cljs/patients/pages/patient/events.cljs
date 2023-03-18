(ns patients.pages.patient.events
  "Namespace for events related to the patient page."
  (:require [re-frame.core :as rf]
            [patients.events :as events]
            [patients.pages.patient.converters :as conv]
            [patients.components.confirm :as confirm]
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
 ::clear-form-errors
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
 (fn [_ [_ {:keys [status data]}]]
   (case status
     :validate-error {:dispatch-n [[::show-form-validation-errors data conv/transform-error-path]
                                   [::popup/show-error-popup :app/validation-error]]}
     :ok {:dispatch [::popup/show-success-popup :app/success-updated]})))

(rf/reg-event-fx
 ::successful-create
 (fn [_ [_ {:keys [status data]}]]
   (case status
     :validate-error {:dispatch-n [[::show-form-validation-errors data conv/transform-error-path]
                                   [::popup/show-error-popup :app/validation-error]]}
     :ok {:dispatch-n [[::popup/show-success-popup :app/success-created]
                       [::nav/set-active-page :patient (:patient-identifier data)]]})))

(rf/reg-event-fx
 ::successful-delete
 (fn [_ _]
   {:dispatch-n [[::popup/show-success-popup :app/success-removed]
                 [::nav/set-active-page :patients]]}))

(rf/reg-event-fx
 ::error-response
 (fn [_ [_ {:keys [response]}]]
   (let [_ response]
     {:dispatch [::popup/show-error-popup :app/bad-request]})))

(rf/reg-event-db
 ::store-patient-data
 (fn [db [_ response]]
   (let [patient (-> response
                     :data
                     :patient
                     conv/convert-response-to-patient-data)]
     (merge db patient))))

;; CRUD

(rf/reg-event-fx
 ::get-patient
 (fn [_ [_ patient-uid]]
   {:dispatch [::events/invoke
               {:request
                {:method :get-patient
                 :params {:patient-identifier patient-uid}}
                :on-success [::store-patient-data]
                :on-failure [::error-response]}]}))

(rf/reg-event-fx
 ::create-patient
 (fn [{:keys [db]} _]
   (let [prepared-data (conv/prepare-patient-data-to-request
                        (select-keys db [:patient-address :patient-data :patient-name]))
         request {:method :create-patient
                  :params {:patient-data prepared-data}}]
     {:dispatch-n [[::clear-form-errors]
                   [::events/invoke
                    {:request request
                     :on-success [::successful-create]
                     :on-failure [::error-response]}]]})))

(rf/reg-event-fx
 ::update-patient
 (fn [{:keys [db]} [_ patient-uid]]
   (let [prepared-data (conv/prepare-patient-data-to-request
                        (select-keys db [:patient-address :patient-data :patient-name]))]
     {:dispatch-n [[::clear-form-errors]
                   [::events/invoke
                    {:request
                     {:method :update-patient
                      :params {:patient-identifier patient-uid
                               :patient-data prepared-data}}
                     :on-success [::successful-update]
                     :on-failure [::error-response]}]]})))

(rf/reg-event-fx
 ::delete-patient
 (fn [_ [_ patient-uid]]
   {:dispatch-n [[::clear-form-errors]
                 [::confirm/show-confirm-dialog
                  {:message :app/confirm-deletion
                   :on-yes [::events/invoke
                            {:request
                             {:method :delete-patient
                              :params {:patient-identifier patient-uid}}
                             :on-success [::successful-delete]
                             :on-failure [::error-response]}]}]]}))