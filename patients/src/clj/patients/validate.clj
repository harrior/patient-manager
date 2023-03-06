(ns patients.validate
  "This module contains functions for validating patient data."
  (:require [clojure.spec.alpha :as s]))

;;
;; Common specs
;;

(s/def ::ne-string (s/and string?
                          not-empty))

(s/def ::uuid-string (s/and ::ne-string
                            #(-> %
                                 parse-uuid
                                 boolean)))

(s/def ::date (s/and #(re-matches #"\d{4}-\d{2}-\d{2}" %)
                     #(let [data-pattern (java.text.SimpleDateFormat. "yyyy-MM-dd")
                            parsed-date (.parse data-pattern %)]
                        (= %
                           (.format data-pattern parsed-date)))))

(s/def :period/start ::date)
(s/def :period/end ::date)

(s/def ::period (s/keys :opt-un [:period/start
                                 :period/end]))

;;
;; Specs for patient name
;;

(s/def :name/use #{"usual" "official" "temp" "nickname" "anonymous" "old" "maiden"})
(s/def :name/text ::ne-string)
(s/def :name/family ::ne-string)
(s/def :name/given (s/coll-of ::ne-string))
(s/def :name/prefix (s/coll-of ::ne-string))
(s/def :name/suffix (s/coll-of ::ne-string))
(s/def :name/period ::period)

(s/def ::name (s/coll-of
               (s/keys :req-un [:name/use
                                :name/text
                                :name/family
                                :name/given]
                       :opt-un [:name/suffix
                                :name/prefix
                                :name/period])))
;;
;; Specs for address
;;

(s/def :address/use #{"home" "work" "temp" "old" "billing"})
(s/def :address/type #{"postal" "physical" "both"})
(s/def :address/text ::ne-string)
(s/def :address/line ::ne-string)
(s/def :address/city ::ne-string)
(s/def :address/district ::ne-string)
(s/def :address/state ::ne-string)
(s/def :address/postal-code ::ne-string)
(s/def :address/country ::ne-string)
(s/def :address/period ::period)
(s/def ::address (s/coll-of
                  (s/keys :req-un [:address/use
                                   :address/type
                                   :address/text
                                   :address/line
                                   :address/city
                                   :address/country]
                          :opt-un [:address/postal-code
                                   :address/state
                                   :address/district
                                   :address/period])))

;;
;; Specs for patient
;;

(s/def :patient/address ::address)
(s/def :patient/name ::name)
(s/def :patient/identifier ::uuid-string)
(s/def :patient/gender #{"male" "female" "other" "unknown"})
(s/def :patient/birth-date ::date)
(s/def :patient/insurance-number (s/and ::ne-string
                                        #(re-matches #"\d{16}" %)))

(s/def ::patient
  (s/keys :req-un [:patient/name
                   :patient/insurance-number
                   :patient/gender
                   :patient/birth-date
                   :patient/address]
          :opt-un [:patient/identifier]))

;;
;; Validation functions
;;

(defn generate-error-path-for-explain-data
  "Generates error path for explain-data."
  [explain-data]
  (let [{:keys [in path pred]} explain-data
        is-missed-key? (or (empty? in)
                           (not= (last in) (last path)))]
    (if is-missed-key?
      (->> pred
           last
           last
           (conj in))
      in)))

(defn get-patient-validation-error-paths
  "Retrieves validation error paths for a patient record."
  [patient]
  (->> (s/explain-data ::patient patient)
       :clojure.spec.alpha/problems
       (map generate-error-path-for-explain-data)))

(defn patient-is-valid?
  "Checks whether a patient record is valid."
  [patient]
  (s/valid? ::patient patient))
