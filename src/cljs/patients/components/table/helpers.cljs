(ns patients.components.table.helpers)

(defn matches-all-filters?
  [item filters]
  (every? identity
          (map (fn [filter-field]
                 (re-find (re-pattern (filter-field filters))
                          (str (filter-field item))))
               (keys filters))))

(defn match-string-in-item-values?
  [item search-value]
  (let [search-pattern (re-pattern search-value)]
    (some #(re-find search-pattern (str %)) (vals item))))

(defn sort-by-field
  [field items & [direction]]
  (let [direction (or direction :asc)]
    (case direction
      :asc (sort #(> (field %2) (field %1)) items)
      :desc (sort #(< (field %2) (field %1)) items))))