(ns patients.pages.patient-single
  (:require [clojure.string :as s]
            [re-frame.core :as rf]
            [patients.nav :as nav]
            [patients.components.locale :refer [locale]]
            [patients.components.requests :as rpc]
            [patients.components.ui-elements :refer [input-field
                                                     button
                                                     select-field
                                                     date-field
                                                     spacer
                                                     fieldset
                                                     fieldset-column
                                                     show-error-popup
                                                     show-success-popup]]))

;;
;; Helpers
;;

(defn- remove-empty-keys [m]
  (into {} (for [[k v] m
                 :when (not (or (nil? v)
                                (empty? v)))]
             [k v])))

;;
;; Data converters
;;

(defn prepare-patient-data-to-request
  [db]
  (let [{:keys [patient-address
                patient-data
                patient-name]} db

        {:keys [family firstname patronymic]} patient-name
        {:keys [postal-code state city line district]} patient-address
        {:keys [gender insurance-number birth-date]} patient-data

        patronymic (if (empty? firstname)
                     nil
                     patronymic)

        name-text (s/join " " (remove nil? [family firstname patronymic]))
        address-text (s/join ", " (remove nil? [postal-code state city line]))

        new-name (remove-empty-keys
                  {:use "usual"
                   :text name-text
                   :family family
                   :given (remove empty? [firstname patronymic])})
        new-address (remove-empty-keys {:use "home"
                                        :type "physical"
                                        :country "RU"
                                        :city city
                                        :state state
                                        :line line
                                        :postalCode postal-code
                                        :district district
                                        :text address-text})]
    (remove-empty-keys {:name [new-name]
                        :address [new-address]
                        :insurance-number insurance-number
                        :gender gender
                        :birth-date birth-date})))

(defn convert-response-to-patient-data
  [{:keys [gender insurance-number birth-date]
    [{:keys [city state line postalCode district]}] :address
    [{:keys [family] [firstname patronymic] :given}] :name}]

  {:patient-data
   (remove-empty-keys {:gender gender
                       :insurance-number insurance-number
                       :birth-date birth-date})
   :patient-address
   (remove-empty-keys {:city city
                       :state state
                       :line line
                       :postal-code postalCode
                       :district district})
   :patient-name
   (remove-empty-keys {:family family
                       :firstname firstname
                       :patronymic patronymic})})

(defn transform-error-path
  [error-paths]
  (reduce (fn [acc path]
            (let [form (first path)
                  key (last path)]
              (case form
                :name (case key
                        :given (assoc-in acc [:patient-name :firstname] true)
                        (assoc-in acc [:patient-name key] true))
                :address (assoc-in acc [:patient-address key] true)
                (assoc-in acc [:patient-data key] true))))
          {}
          error-paths))

;;
;; Events
;;

(rf/reg-event-db
 :clear-form
 (fn [db]
   (merge db
          {:patient-address {}
           :patient-data {}
           :patient-name {}})))

(rf/reg-event-fx
 :show-error-popup
 (fn [_ [_ message]]
   (show-error-popup message)
   {}))

(rf/reg-event-fx
 :show-success-popup
 (fn [_ [_ message]]
   (show-success-popup message)
   {}))

(rf/reg-event-db
 :show-form-validation-errors
 (fn [db [_ {:keys [error-paths]} transform-fn]]
   (let [errors-map (transform-fn error-paths)]
     (assoc db :errors errors-map))))

(rf/reg-event-db
 :clean-form-errors
 (fn [db [_]]
   (assoc db :errors {})))

;; Check CRUD response

(rf/reg-event-fx
 :check-update-request-result
 (fn [_ [_ {:keys [status data]} ]]
   (case status
     :ok {:dispatch [:show-success-popup :app/success-updated]}
     :validate-error {:dispatch-n [[:show-form-validation-errors data transform-error-path]
                                   [:show-error-popup :app/validation-error]]})))

(rf/reg-event-fx
 :check-registration-request-result
 (fn [_ [_ {:keys [status data]}]]
   (case status
     :ok {:dispatch-n [[:show-success-popup :app/success-created]
                       [::nav/set-active-page :patient (:patient-identifier data)]]}
     :validate-error {:dispatch-n [[:show-form-validation-errors data transform-error-path]
                                   [:show-error-popup :app/validation-error]]})))

(rf/reg-event-fx
 :check-delete-request-result
 (fn [_ [_ {:keys [status]}]]
   (case status
     :ok {:dispatch-n [[:show-success-popup :app/success-removed]
                       [::nav/set-active-page :patients]]}
     {:dispatch [:show-error-popup :app/bad-request]})))

;; CRUD

(rf/reg-event-db
 :load-patient-data
 (fn [db [_ response]]
   (let [patient (-> response
                     :data
                     :patient
                     convert-response-to-patient-data)]
     (merge db patient))))

(rf/reg-event-fx
 :get-patient
 (fn [_ [_ patient-uid]]
   {:dispatch [::rpc/invoke
               {:method :get-patient
                :params {:patient-identifier patient-uid}}
               [:load-patient-data]
               [:show-error-popup :app/bad-request]]}))

(rf/reg-event-fx
 :create-patient
 (fn [{:keys [db]} _]
   (let [prepared-data (prepare-patient-data-to-request db)
         request {:method :create-patient
                  :params {:patient-data prepared-data}}]
     {:dispatch-n [[:clean-form-errors]
                   [::rpc/invoke
                    request
                    [:check-registration-request-result]
                    [:show-error-popup :app/bad-request]]]})))

(rf/reg-event-fx
 :update-patient
 (fn [{:keys [db]} [_ patient-uid]]
   (let [prepared-data (prepare-patient-data-to-request db)]
     {:dispatch-n [[:clean-form-errors]
                   [::rpc/invoke
                    {:method :update-patient
                     :params {:patient-identifier patient-uid
                              :patient-data prepared-data}}
                    [:check-update-request-result]
                    [:show-error-popup :app/bad-request]]]})))

(rf/reg-event-fx
 :delete-patient
 (fn [_ [_ patient-uid]]
   {:dispatch-n [[:clean-form-errors]
                 [::rpc/invoke
                  {:method :delete-patient
                   :params {:patient-identifier patient-uid}}
                  [:check-delete-request-result]
                  [:show-error-popup :app/bad-request]]]}))

;;
;; Subs
;;

(rf/reg-sub
 :get-patient-uid
 (fn [db _]
   (first (get-in db [:app :page-params]))))

(rf/reg-sub
 :create-patient?
 (fn []
   (rf/subscribe [:get-patient-uid]))
 (fn [patient-uid]
   (nil? patient-uid)))

;;
;; Patient page
;;

(defn init
  []
  (let [patient-uid @(rf/subscribe [:get-patient-uid])]
    (rf/dispatch [:clean-form-errors])
    (if (nil? patient-uid)
      (rf/dispatch [:clear-form])
      (rf/dispatch [:get-patient patient-uid]))))

(defn page-header
  []
  (let [patient-uid @(rf/subscribe [:get-patient-uid])
        create-patient? @(rf/subscribe [:create-patient?])]
    [:header {:class "header"}
     [:h1 (if create-patient?
            (locale :app/add-patient)
            (locale :app/edit-patient))]
     [:div {:style {:display :flex}}
      [button {:id :open-grid-button
               :label :app/patients-list
               :on-click #(rf/dispatch [::nav/set-active-page :patients])}]
      [spacer]
      (if create-patient?
        [button {:id :create-patient-button
                 :label :app/create-patient
                 :on-click #(rf/dispatch [:create-patient])}]
        [:div
         [button {:id :save-patient-button
                  :label :app/save-patient
                  :on-click #(rf/dispatch [:update-patient patient-uid])}]
         [button {:id :delete-patient-button
                  :label :app/delele-patient
                  :on-click #(rf/dispatch [:delete-patient patient-uid])}]])]]))

(defn patient-data
  []
  ^{:key (random-uuid)}
  [fieldset {:title :app/patient-data}
   ^{:key (random-uuid)}
   [fieldset-column
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-name
                  :field-id :family
                  :label :patient/family}]
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-name
                  :field-id :firstname
                  :label :patient/name}]
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-name
                  :field-id :patronymic
                  :label :patient/patronymic}]]
   ^{:key (random-uuid)}
   [fieldset-column
    ^{:key (random-uuid)}
    [select-field {:form-id :patient-data
                   :field-id :gender
                   :label :patient/gender
                   :options [{:id :none :text "-" :params {:hidden true}}
                             {:id :male :text :male}
                             {:id :female :text :female}
                             {:id :other :text :other}
                             {:id :unknown :text :unknown}]}]
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-data
                  :field-id :insurance-number
                  :label :patient/insurance-number}]
    ^{:key (random-uuid)}
    [date-field {:form-id :patient-data
                 :field-id :birth-date
                 :label :patient/birthday}]]])

(defn patient-address
  []
  [fieldset {:title :app/patient-address}
   ^{:key (random-uuid)}
   [fieldset-column
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-address
                  :field-id :city
                  :label :address/city}]
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-address
                  :field-id :state
                  :label :address/state}]
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-address
                  :field-id :line
                  :label :address/line}]]
   ^{:key (random-uuid)}
   [fieldset-column
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-address
                  :field-id :postal-code
                  :label :address/postal-code}]
    ^{:key (random-uuid)}
    [input-field {:form-id :patient-address
                  :field-id :district
                  :label :address/district}]]])
(defn footer
  []
  [:footer {:class "footer"}])

(defn patient-page
  []
  [:div {:class "container"}
   [page-header]
   [:div {:style {:display :flex
                  :flex-direction :column
                  :gap 10}}
    [patient-data]
    [patient-address]
    [footer]]])

(defn main
  []
  (init)
  [patient-page])
