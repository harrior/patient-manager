(ns patients.components.table.helpers
  "Namespace containing helper functions for the table component.")

(defn matches-all-filters?
  "Checks if the given item matches all specified filters."
  [item filters]
  (every? identity
          (map (fn [filter-field]
                 (re-find (re-pattern (filter-field filters))
                          (str (filter-field item))))
               (keys filters))))

(defn match-string-in-item-values?
  "Checks if the search-value is present in any of the item's values."
  [item search-value]
  (let [search-pattern (re-pattern search-value)]
    (some #(re-find search-pattern (str %)) (vals item))))

(defn sort-by-field
  "Sorts the items by the specified field and direction (ascending by default)."
  [field items & [direction]]
  (let [direction (or direction :asc)]
    (case direction
      :asc (sort #(> (field %2) (field %1)) items)
      :desc (sort #(< (field %2) (field %1)) items))))