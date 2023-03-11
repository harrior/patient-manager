(ns patients.components.table.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::init-table
 (fn [db [_ [table-id fields sorted-by]]]
   (let [table-filds (map :value fields)
         table-settings (table-id db)
         new-setings {:fields table-filds
                      :sorted-by sorted-by}]
     (assoc db table-id (merge table-settings
                               new-setings)))))

(rf/reg-event-db
 ::set-table-search
 (fn [db [_ [table-id val]]]
   (assoc-in db [table-id :search] val)))

(rf/reg-event-db
 ::set-table-filters
 (fn [db [_ [table-id field val]]]
   (let [last-filter (get-in db [table-id :filters])
         new-filter (if (= val "")
                      (dissoc last-filter field)
                      (assoc last-filter field val))]
     (assoc-in db [table-id :filters] new-filter))))