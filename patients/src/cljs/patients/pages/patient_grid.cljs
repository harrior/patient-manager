(ns patients.pages.patient-grid
  (:require [re-frame.core :as rf]
            [patients.nav :as nav]
            [patients.components.locale :refer [locale]]
            [patients.components.table :refer [table table-header]]
            [patients.components.requests :as rpc]
            [patients.components.ui-elements :refer [button]]))

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

(rf/reg-event-db
 :create-patient
 (fn [db]
   (-> db
       (assoc-in [:app :current-page] :patient-single)
       (assoc-in [:app :patient-uid] nil))))

;; NOTE: add error show
(rf/reg-event-db
 :bad-http-result
 (fn [_ _]
   (println "Something wrong!")))

(rf/reg-event-fx
 :request-patients-list
 (fn [_ _]
   {:dispatch [::rpc/invoke
               {:method :list-patients}
               [:load-patients-db]
               [:bad-http-result]]}))

;;
;; Patients grid page
;;

(defn init []
  (rf/dispatch [:request-patients-list]))

(defn patient-grid-page
  []
  [:div {:class "container"}
   [:header {:class "header"}
    [:h1 (locale :app/title)]]

   [:div {:class "patient-container"}
    [table-header {:buttons [^{:key :create-patient}
                             [button
                              {:id :create-patient
                               :label :app/add-patient
                               :on-click #(rf/dispatch [::nav/set-active-page :patient])}]]
                   :id :table}]

    [table {:id :table
            :sub :patients
            :sorted-by :fullname
            :on-click-row (fn [patient] (rf/dispatch [::nav/set-active-page :patient (:identifier patient)]))
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
                      :filter-type :input}]}]]
   [:footer {:class "footer"}]])

(defn main
  []
  (init)
  [patient-grid-page])