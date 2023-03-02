(ns patients.pages.patient-grid
  (:require [ajax.edn :as edn]
            [day8.re-frame.http-fx]
            [re-frame.core :as rf]
            [patients.components.locale :refer [locale]]
            [patients.components.table :refer [table table-header]]
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
 :update-patients-db
 (fn [db [_ params]]
   (assoc db
          :patients
          (->> params
               :data
               :patients
               reverse))))

(rf/reg-event-db
 :bad-http-result
 (fn [_ _]
   (println "Something wrong!")))

(rf/reg-event-fx
 :request-patients-list
 (fn [_ _]
   {:http-xhrio {:method          :post
                 :uri             "http://localhost:8000/rpc"
                 :params          {:method :list-patients}
                 :timeout         8000
                 :format          (edn/edn-request-format)
                 :response-format (edn/edn-response-format {:keywords? true})
                 :on-success      [:update-patients-db]
                 :on-failure      [:bad-http-result]}}))

;;
;; Patients grid page
;;

(defn init []
  (rf/dispatch-sync [:request-patients-list]))

(defn patient-grid-page
  []
  [:div {:class "container"}
   [:header {:class "header"}
    [:h1 (locale :app/title)]]

   [:div {:class "patient-container"}
    [table-header {:buttons [^{:key :create-patient}
                             [ui/button
                              {:id :create-patient
                               :label :app/add-patient}]]
                   :id :table}]

    [table {:id :table
            :sub :patients
            :sorted-by :fullname
            :on-click-row (fn [])
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