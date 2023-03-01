(ns patients.core
  (:require
   [ajax.edn :as edn]
   [day8.re-frame.http-fx]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]
   [patients.db :as db]
   [patients.components.locale :refer [locale]]
   [patients.components.table :refer [table table-header]]))
(enable-console-print!)

(rf/reg-event-db
 ::update-patients-db
 (fn [db [_ params]]
   (assoc db
          :patients
          (->> params
               :data
               :patients
               reverse))))

(rf/reg-event-db
 ::bad-http-result
 (fn [_ _]
   (println "Something wrong!")))

(rf/reg-event-fx
 ::request-patients-list
 (fn [{:keys [db]} _]
   {:http-xhrio {:method          :post
                 :uri             "http://localhost:8000/rpc"
                 :params          {:method :list-patients}
                 :timeout         8000
                 :format          (edn/edn-request-format)
                 :response-format (edn/edn-response-format {:keywords? true})
                 :on-success      [::update-patients-db]
                 :on-failure      [::bad-http-result]}}))

(rf/reg-sub
 :patients
 (fn [db]
   (:patients db)))

;; Entry Point

(defn grid-page-content
  []
  [table-header {:buttons [[:button
                            {:class "add-patient form-button"
                             :on-click #(rf/dispatch [::request-patients-list])} (locale :app/add-patient)]]
                 :id :table}]

  [table {:id :table
          :sub :patients
          :on-click-row (fn [])
          :fields [{:title :patients/fullname
                    :value :fullname
                    :filter-type :input}
                   {:title :patients/gender
                    :value :gender
                    :filter-type :select}
                   {:title :patients/birthday
                    :value :birth-date
                    :filter-type :select}
                   {:title :patients/address
                    :value :address
                    :filter-type :input}
                   {:title :patients/insurance-number
                    :value :insurance-number
                    :filter-type :input}]}])



(defn index []
  [:div {:class "container"}
   [:header {:class "header"}
    [:h1 (locale :app/title)]]

   [:div {:class "patient-container"}
    [grid-page-content]
    ]
   [:footer {:class "footer"}]])



(rf/dispatch-sync [::db/initialize])
(rf/dispatch-sync [::request-patients-list])
(rdom/render [index] (js/document.getElementById "app"))
