(ns patients.pages.patient-grid
  "Patients page."
  (:require [re-frame.core :as rf]
            [patients.events :as events]
            [patients.nav :as nav]
            [patients.components.locale :refer [locale]]
            [patients.components.table.core :as table]
            [patients.components.ui-elements :as ui]))

;;
;; Subs
;;

(rf/reg-sub
 :patients
 (fn [db]
   (:patients db)))

;;
;; Events
;;

(rf/reg-event-db
 :store-patients-data
 ^{:doc "Stores patient data in the app database."}
 (fn [db [_ params]]
   (assoc db
          :patients
          (->> params
               :data
               :patients))))

(rf/reg-event-fx
 :request-patients-list
 (fn [_ _]
   {:dispatch [::events/invoke
               {:request
                {:method :list-patients}
                :on-success [:store-patients-data]
                :on-failure [::ui/show-error-popup :app/bad-request]}]}))

;;
;; Patients grid page
;;

(defn init []
  (rf/dispatch [:request-patients-list]))

(defn- page-header
  []
  [:header {:class "header"}
   [:h1 (locale :app/title)]])

(defn- table-header
  []
  [table/table-header
   {:buttons [^{:key :create-patient}
              [ui/button
               {:id :create-patient
                :label :app/add-patient
                :on-click #(rf/dispatch [::nav/set-active-page :patient])}]]
    :id :table}])

(defn- table
  []
  [:div {:class "patient-container"}
   [table/table
    {:id :table
     :sub :patients
     :sorted-by :fullname
     :on-click-row (fn [patient]
                     (rf/dispatch [::nav/set-active-page :patient (:identifier patient)]))
     :fields [{:title :patient/fullname
               :value :fullname
               :filter-type :text-input}
              {:title :patient/gender
               :value :gender
               :filter-type :select}
              {:title :patient/birthday
               :value :birth-date
               :filter-type :date}
              {:title :address/text
               :value :address
               :filter-type :text-input}
              {:title :patient/insurance-number
               :value :insurance-number
               :filter-type :text-input}]}]])

(defn main
  []
  [:div.container
   [page-header]
   [table-header]
   [table]
   [ui/footer]])