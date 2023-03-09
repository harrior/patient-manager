(ns patients.components.table.subs
  (:require (clojure.string :as s)
            [re-frame.core :as rf]
            [patients.components.table.helpers :as table-helpers]))

(rf/reg-sub
 ::get-table-filters
 (fn [db [_ table-id]]
   (get-in db [table-id :filters])))

(rf/reg-sub
 ::get-table-fields
 (fn [db [_ table-id]]
   (get-in db [table-id :fields])))

(rf/reg-sub
 ::get-table-filter-value
 (fn [db [_ table-id field-id]]
   (or (get-in db [table-id :filters field-id])
       "")))

(rf/reg-sub
 ::get-table-search-value
 (fn [db [_ table-id]]
   (get-in db [table-id :search])))

(rf/reg-sub
 ::get-table-sorted-by
 (fn [db [_ table-id]]
   (get-in db [table-id :sorted-by])))


(rf/reg-sub
 ::table-filtered-items
 (fn [[_ table-id sub]]
   [(rf/subscribe [sub])
    (rf/subscribe [::get-table-filters table-id])
    (rf/subscribe [::get-table-search-value table-id])
    (rf/subscribe [::get-table-fields table-id])
    (rf/subscribe [::get-table-sorted-by table-id])])
 (fn [[items filters search-value table-fields sorted-by]]
   (let [filtered-items (if (seq filters)
                          (filter (fn [item] (table-helpers/matches-all-filters? item filters))
                                  items)
                          items)
         found-items (if-not (s/blank? search-value)
                       (filter (fn [item]
                                 (-> item
                                     (select-keys table-fields)
                                     (table-helpers/match-string-in-item-values? search-value)))
                               filtered-items)
                       filtered-items)]
     (table-helpers/sort-by-field sorted-by found-items))))