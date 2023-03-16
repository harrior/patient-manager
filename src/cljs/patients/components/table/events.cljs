(ns patients.components.table.events
  "This namespace contains re-frame event handlers for table-related actions."
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::init-table
 ^{:doc
   "Initializes table settings in the app db.

   Args:
   - db: Current app db state.
   - table-id: Unique identifier for the table.
   - fields: Collection of field maps, each containing :value-key.
   - sorted-by: Keyword indicating the field to sort the table by."}
 (fn [db [_ [table-id fields sorted-by]]]
   (let [table-filds (map :value-key fields)
         table-settings (table-id db)
         new-setings {:fields table-filds
                      :sorted-by sorted-by}]
     (assoc db table-id (merge table-settings
                               new-setings)))))

(rf/reg-event-db
 ::set-table-search
 ^{:doc
   "Sets the search value for a table in the app db.

   Args:
   - db: Current app db state.
   - table-id: Unique identifier for the table.
   - value: Search value to be set."}
 (fn [db [_ [table-id value]]]
   (assoc-in db [table-id :search] value)))

(rf/reg-event-db
 ::set-table-filters
 ^{:doc
   "Updates the filter value for a specific field in the app db.

   Args:
   - db: Current app db state.
   - table-id: Unique identifier for the table.
   - field: The field for which the filter value is being updated.
   - value: The new filter value."}
 (fn [db [_ [table-id field value]]]
   (let [last-filter (get-in db [table-id :filters])
         new-filter (if (= value "")
                      (dissoc last-filter field)
                      (assoc last-filter field value))]
     (assoc-in db [table-id :filters] new-filter))))