(ns patients.components.table.core
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style] :as stylefy]
            [patients.components.locale :refer [locale]]
            [patients.components.helpers :as helpers]
            [patients.components.table.events :as table-events]
            [patients.components.table.subs :as table-subs]
            [patients.components.styles :as styles]))

;;
;; Styles
;;

(def table-style {:width "100%"
                  :border-collapse :collapse
                  :border "1px solid #dddddd"})

(def table-column-filter-style {:padding "4px"})


(def table-column-style {:padding "5px 0 0"
                         :border "1px solid #dddddd"
                         :border-collapse :collapse})

(def table-row-style {:line-height "40px"
                      :text-align :left
                      ::stylefy/mode {:hover {:background-color "#f5f5f5"
                                              :cursor :pointer}}})
(def table-cell-style {:padding-left "10px"
                       :border "1px solid #dddddd"
                       :border-collapse :collapse
                       :height "24px"})

(def table-controls-style {:display :flex
                           :direction :row
                           :justify-content :space-between
                           :padding "10px 0"
                           :align-items :baseline})

;;
;; Table Header
;;

(defn table-header
  [{:keys [id buttons]}]
  [:div (use-style table-controls-style)
   [:div
    (doall
     (for [button buttons]
       button))]
   [:div (use-style styles/search-box)
    [:input (merge (use-style styles/search-box-field)
                   {:placeholder (locale :app/search-placeholder)
                    :value @(rf/subscribe [::table-subs/table-search-value id])
                    :on-change (fn [e] (let [input-value (helpers/input-value-extractor e)]
                                         (rf/dispatch [::table-events/set-table-search [id input-value]])))})]]])

;;
;; Table
;;

(defn table
  [{:keys [id sub fields sorted-by on-click-row]}]
  (rf/dispatch [::table-events/init-table [id fields sorted-by]])
  (let [items @(rf/subscribe [sub])
        filters @(rf/subscribe [::table-subs/table-filtered-items id sub])]
    [:table  (use-style table-style)
     [:thead
      [:tr
       (doall
        (for [field fields]
          ^{:key field}
          [:th (use-style table-column-style)
           [:div (locale (:title field))]
           [:div (use-style table-column-filter-style)]
           (let [value (:value field)
                 on-change-fn (fn [event] (let [input-value (helpers/input-value-extractor event)]
                                            (rf/dispatch [::table-events/set-table-filters [id value input-value]])))
                 field-value @(rf/subscribe [::table-subs/table-filter-value id value])
                 common-fields (merge {:id value
                                       :value field-value
                                       :on-change on-change-fn}
                                      (use-style styles/form-control-style))]
             (case (:filter-type field)
               :text-input [:input common-fields]

               :select (let [options (->> items
                                          (map value)
                                          set
                                          sort)]
                         [:select common-fields
                          [:option {:value ".+" :default true} " "]
                          (map (fn [value]
                                 ^{:key value} [:option {:value (str "^" value "$")} value])
                               options)])

               :date [:input (merge common-fields
                                    {:type :date})]
               [:div]))]))]]
     [:tbody
      (doall
       (for [item filters]
         ^{:key (random-uuid)}
         [:tr
          (merge
           (use-style table-row-style)
           (when on-click-row
             {:on-click #(on-click-row item)}))

          (for [field fields]
            ^{:key field}
            [:td (use-style table-cell-style) ((:value field) item)])]))]]))