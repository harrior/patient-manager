(ns patients.components.table.core
  (:require [re-frame.core :as rf]
            [patients.components.locale :refer [locale]]
            [patients.components.table.events :as table-events]
            [patients.components.helpers :as helpers]
            [patients.components.table.subs :as table-subs]))

;;
;; Table Header
;;

(defn table-header
  [{:keys [id buttons]}]
  [:div {:class "table-controls"}
   [:div {:class "table-buttons"}
    (doall
     (for [button buttons]
       button))]
   [:div {:class "search-box"}
    [:input {:class "table-column-filter-input form-control"
             :placeholder (locale :app/search-placeholder)
             :value @(rf/subscribe [::table-subs/table-search-value id])
             :on-change (fn [e] (let [input-value (helpers/input-value-extractor e)]
                                  (rf/dispatch [::table-events/set-table-search [id input-value]])))}]]])

;;
;; Table
;;

(defn table
  [{:keys [id sub fields sorted-by on-click-row]}]
  (rf/dispatch [::table-events/init-table [id fields sorted-by]])
  (let [items @(rf/subscribe [sub])
        filters @(rf/subscribe [::table-subs/table-filtered-items id sub])]
    [:table {:class "table"}
     [:thead
      [:tr {:class "table-header"}
       (doall
        (for [field fields]
          ^{:key field}
          [:th {:class "table-column"}
           [:div {:class "table-column-text"} (locale (:title field))]
           [:div {:class "table-column-filter"}]
           (let [value (:value field)
                 on-change-fn (fn [event] (let [input-value (helpers/input-value-extractor event)]
                                            (rf/dispatch [::table-events/set-table-filters [id value input-value]])))
                 field-value @(rf/subscribe [::table-subs/table-filter-value id value])]
             (case (:filter-type field)
               :text-input [:input {:class "form-control"
                                    :id value
                                    :value field-value
                                    :on-change on-change-fn}]

               :select (let [options (->> items
                                          (map value)
                                          set
                                          sort)]
                         [:select {:class "form-control"
                                   :id value
                                   :value field-value
                                   :on-change on-change-fn}
                          [:option {:value ".+" :default true} " "]
                          (map (fn [value]
                                 ^{:key value} [:option {:value (str "^" value "$")} value])
                               options)])

               :date [:input {:class " form-control"
                              :type :date
                              :id value
                              :value field-value
                              :on-change on-change-fn}]
               [:div]))]))]]
     [:tbody
      (doall
       (for [item filters]
         ^{:key (random-uuid)}
         [:tr
          (merge
           {:class "table-row"}
           (when on-click-row
             {:on-click #(on-click-row item)}))

          (for [field fields]
            ^{:key field}
            [:td {:class "table-cell"} ((:value field) item)])]))]]))