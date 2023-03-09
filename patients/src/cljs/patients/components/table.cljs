(ns patients.components.table
  (:require
   [clojure.string :as s]
   [re-frame.core :as rf]
   [patients.components.locale :refer [locale]]
   [patients.components.helpers :as h]))

;;
;; Helpers
;;

(defn- matches-all-filters?
  [item filters]
  (every? identity
          (map (fn [filter-field]
                 (re-find (re-pattern (filter-field filters))
                          (str (filter-field item))))
               (keys filters))))

(defn- match-string-in-item-values?
  [item search-value]
  (let [search-pattern (re-pattern search-value)]
    (some #(re-find search-pattern (str %)) (vals item))))

(defn- sort-by-field
  [field items & [direction]]
  (let [direction (or direction :asc)]
    (case direction
      :desc (sort #(< (field %2) (field %1)) items)
      (sort #(> (field %2) (field %1)) items))))

;;
;; Events
;;

(rf/reg-event-db
 :init-table
 (fn [db [_ [table-id fields sorted-by]]]
   (let [table-filds (map :value fields)
         table-settings (table-id db)
         new-setings {:fields table-filds
                      :sorted-by sorted-by}]
     (assoc db table-id (merge table-settings
                               new-setings) ))))

(rf/reg-event-db
 :set-table-search
 (fn [db [_ [table-id val]]]
   (assoc-in db [table-id :search] val)))

(rf/reg-event-db
 :set-table-filters
 (fn [db [_ [table-id field val]]]
   (let [last-filter (get-in db [table-id :filters])
         new-filter (if (= val "")
                      (dissoc last-filter field)
                      (assoc last-filter field val))]
     (assoc-in db [table-id :filters] new-filter))))

;;
;; Subs
;;

(rf/reg-sub
 :get-table-filters
 (fn [db [_ table-id]]
   (get-in db [table-id :filters])))

(rf/reg-sub
 :get-table-fields
 (fn [db [_ table-id]]
   (get-in db [table-id :fields])))

(rf/reg-sub
 :get-table-filter-value
 (fn [db [_ table-id field-id]]
   (or (get-in db [table-id :filters field-id])
       "")))

(rf/reg-sub
 :get-table-search-value
 (fn [db [_ table-id]]
   (get-in db [table-id :search])))

(rf/reg-sub
 :get-table-sorted-by
 (fn [db [_ table-id]]
   (get-in db [table-id :sorted-by])))


(rf/reg-sub
 :table-filtered-items
 (fn [[_ table-id sub]]
   [(rf/subscribe [sub])
    (rf/subscribe [:get-table-filters table-id])
    (rf/subscribe [:get-table-search-value table-id])
    (rf/subscribe [:get-table-fields table-id])
    (rf/subscribe [:get-table-sorted-by table-id])])
 (fn [[items filters search-value table-fields sorted-by]]
   (let [filtered-items (if (seq filters)
                         (filter (fn [item] (matches-all-filters? item filters))
                                 items)
                         items)
         found-items (if-not (s/blank? search-value)
                       (filter (fn [item]
                                 (-> item
                                     (select-keys table-fields)
                                     (match-string-in-item-values? search-value)))
                               filtered-items)
                       filtered-items)]
     (sort-by-field sorted-by found-items))))

;;
;; Components
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
             :value @(rf/subscribe [:get-table-search-value id])
             :on-change (fn [e] (let [input-value (h/input-value-extractor e)]
                                  (rf/dispatch [:set-table-search [id input-value]])))}]]])

(defn table
  [{:keys [id sub fields sorted-by on-click-row]}]
  (rf/dispatch [:init-table [id fields sorted-by]])
  (let [items @(rf/subscribe [sub])
        filters @(rf/subscribe [:table-filtered-items id sub])]
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
                 on-change-fn (fn [event] (let [input-value (h/input-value-extractor event)]
                                            (rf/dispatch [:set-table-filters [id value input-value]])))
                 field-value @(rf/subscribe [:get-table-filter-value id value])]
             (case (:filter-type field)
               :input [:input {:class "form-control"
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