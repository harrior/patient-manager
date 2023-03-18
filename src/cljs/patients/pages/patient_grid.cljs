(ns patients.pages.patient-grid
  "Patients page."
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style]]
            [patients.events :as events]
            [patients.nav :as nav]
            [patients.components.locale :refer [locale]]
            [patients.components.popup :as popup]
            [patients.components.styles :as styles]
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
 ^{:doc "Requests the patients list and dispatches events on success or failure."}
 (fn [_ _]
   {:dispatch [::events/invoke
               {:request
                {:method :list-patients}
                :on-success [:store-patients-data]
                :on-failure [::popup/show-error-popup :app/bad-request]}]}))

;;
;; Patients grid page
;;

(defn- page-header
  []
  [:header (use-style styles/header)
   [:h1 (locale :app/title)]])

(defn- table-header
  []
  [table/table-header
   {:table-id :table
    :buttons [^{:key :create-patient}
              [ui/button
               {:id :create-patient
                :label :app/add-patient
                :on-click #(rf/dispatch [::nav/set-active-page :patient])}]]}])

(defn- table
  []
  [:div
   [table/table
    {:table-id :table
     :data-source :patients
     :sorted-by :fullname

     :on-click-row (fn [patient]
                     (rf/dispatch [::nav/set-active-page :patient (:identifier patient)]))

     :fields [{:title :patient/fullname
               :value-key :fullname
               :filter-type :text-input
               :column-width "25%"}
              {:title :patient/gender
               :value-key :gender
               :filter-type :select
               :column-width "10%"}
              {:title :patient/birthday
               :value-key :birth-date
               :filter-type :date
               :column-width "15%"}
              {:title :address/text
               :value-key :address
               :filter-type :text-input
               :column-width "35%"}
              {:title :patient/insurance-number
               :value-key :insurance-number
               :filter-type :text-input
               :column-width "15%"}]}]])

(defn- patients-page
  []
  [:div (use-style styles/container)
   [page-header]
   [table-header]
   [table]
   [ui/footer]])

(defn main
  []
  (rf/dispatch [:request-patients-list])
  (fn []
    [patients-page]))