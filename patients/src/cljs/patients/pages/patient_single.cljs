(ns patients.pages.patient-single
  (:require [patients.components.locale :refer [locale]]
            [patients.components.ui-elements :as ui]))

(defn patient-page
  []
  [:div {:class "container"}
   [:header {:class "header"}
    [:h1 (locale :app/add-patient)]
    [:div {:style {:display :flex}}
     [ui/button {:id :go-main-page-button
                 :label :app/title}]
     [ui/spacer]
     [ui/button {:id :create-patient
                 :label :app/create}]]]

   [:div {:style {:display :flex
                  :flex-direction :column
                  :gap 10}}

    [:fieldset {:style {:display :flex
                        :gap 10}}
     [:legend (locale :app/patient-data)]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/input-field {:id :patient/family
                       :label :patient/family}]
      [ui/input-field {:id :patient/name
                       :label :patient/name}]
      [ui/input-field {:id :patient/patronymic
                       :label :patient/patronymic}]]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/select-field {:id :patient/gender
                        :label :patient/gender
                        :options [{:id :male :text "male"}
                                  {:id :female :text "female"}]}]
      [ui/input-field {:id :patient/insurance-number
                       :label :patient/insurance-number}]
      [ui/date-field {:id :patient/birthday
                      :label :patient/birthday}]]]
    [:fieldset {:style {:display :flex
                        :gap 10}}
     [:legend (locale :app/patient-data)]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/input-field {:id :address/city
                       :label :address/city}]
      [ui/input-field {:id :address/state
                       :label :address/state}]
      [ui/input-field {:id :address/line
                       :label :address/line}]]
     [:div {:style {:display :flex
                    :flex-direction :column
                    :width "100%"}}
      [ui/input-field {:id :address/postalCode
                       :label :address/postalCode}]
      [ui/input-field {:id :address/district
                       :label :address/district}]]]]
   [:footer {:class "footer"}]])