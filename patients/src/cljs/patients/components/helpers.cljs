(ns patients.components.helpers)

(defn input-value-extractor
  [event]
  (-> event
      .-target
      .-value))