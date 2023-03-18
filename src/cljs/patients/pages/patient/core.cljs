(ns patients.pages.patient.core
  "Patient page."
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style]]
            [patients.components.locale :refer [locale]]
            [patients.components.styles :as styles]
            [patients.components.ui-elements :as ui]
            [patients.pages.patient.events :as evt]
            [patients.pages.patient.subs :as subs]
            [patients.nav :as nav]))

;;
;; Patient page
;;

(defn page-header
  []
  (let [patient-uid @(rf/subscribe [::subs/patient-uid])
        create-patient? @(rf/subscribe [::subs/new-patient?])]
    [:header (use-style styles/header)
     [:h1 (if create-patient?
            (locale :app/add-patient)
            (locale :app/edit-patient))]
     [:div {:style {:display :flex}}
      [ui/button {:id :open-grid-button
                  :label :app/patients-list
                  :on-click #(rf/dispatch [::nav/set-active-page :patients])}]
      [ui/spacer]
      (if create-patient?
        [ui/button {:id :create-patient-button
                    :label :app/create-patient
                    :on-click #(rf/dispatch [::evt/create-patient])}]
        [:div
         [ui/button {:id :save-patient-button
                     :label :app/save-patient
                     :on-click #(rf/dispatch [::evt/update-patient patient-uid])}]
         [ui/button {:id :delete-patient-button
                     :label :app/delele-patient
                     :on-click #(rf/dispatch [::evt/delete-patient patient-uid])}]])]]))

(defn patient-data
  []
  ^{:key (random-uuid)}
  [ui/fieldset-row {:title :app/patient-data}
   ^{:key (random-uuid)}
   [ui/fieldset-column
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-name
                     :field-id :family
                     :label :patient/family}]
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-name
                     :field-id :firstname
                     :label :patient/name}]
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-name
                     :field-id :patronymic
                     :label :patient/patronymic}]]
   ^{:key (random-uuid)}
   [ui/fieldset-column
    ^{:key (random-uuid)}
    [ui/select-field {:form-id :patient-data
                      :field-id :gender
                      :label :patient/gender
                      :options [{:id :none :text "-" :params {:hidden true}}
                                {:id :male :text :male}
                                {:id :female :text :female}
                                {:id :other :text :other}
                                {:id :unknown :text :unknown}]}]
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-data
                     :field-id :insurance-number
                     :label :patient/insurance-number}]
    ^{:key (random-uuid)}
    [ui/date-field {:form-id :patient-data
                    :field-id :birth-date
                    :label :patient/birthday}]]])

(defn patient-address
  []
  ^{:key (random-uuid)}
  [ui/fieldset-row {:title :app/patient-address}
   ^{:key (random-uuid)}
   [ui/fieldset-column
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-address
                     :field-id :city
                     :label :address/city}]
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-address
                     :field-id :state
                     :label :address/state}]
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-address
                     :field-id :line
                     :label :address/line}]]
   ^{:key (random-uuid)}
   [ui/fieldset-column
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-address
                     :field-id :postal-code
                     :label :address/postal-code}]
    ^{:key (random-uuid)}
    [ui/input-field {:form-id :patient-address
                     :field-id :district
                     :label :address/district}]]])

(defn patient-page
  []
  [:div (use-style styles/container)
   [page-header]
   [:div (use-style {:style {:display :flex
                             :flex-direction :column
                             :gap 10}})
    [patient-data]
    [patient-address]
    [ui/footer]]])

(defn main
  []
  (let [patient-uid @(rf/subscribe [::subs/patient-uid])]
    (rf/dispatch [::evt/clear-form-errors])
    (if (nil? patient-uid)
      (rf/dispatch [::evt/clear-form])
      (rf/dispatch [::evt/get-patient patient-uid])))
  (fn []
    [patient-page]))
