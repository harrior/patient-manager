(ns patients.pages.patient-grid
  (:require [re-frame.core :as rf]
            [patients.nav :as nav]
            [patients.components.locale :refer [locale]]
            [patients.components.table.core :as table]
            [patients.components.requests :as rpc]
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
 :load-patients-db
 (fn [db [_ params]]
   (assoc db
          :patients
          (->> params
               :data
               :patients
               reverse))))

(rf/reg-event-fx
 :request-patients-list
 (fn [_ _]
   {:dispatch [::rpc/invoke
               {:method :list-patients}
               [:load-patients-db]
               [::ui/show-error-popup :app/bad-request]]}))

;;
;; Patients grid page
;;

(defn- init []
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
               :filter-type :input}
              {:title :patient/gender
               :value :gender
               :filter-type :select}
              {:title :patient/birthday
               :value :birth-date
               :filter-type :date}
              {:title :address/text
               :value :address
               :filter-type :input}
              {:title :patient/insurance-number
               :value :insurance-number
               :filter-type :input}]}]])

(defn main
  []
  (init)
  [:div.container
   [page-header]
   [table-header]
   [table]
   [ui/footer]])