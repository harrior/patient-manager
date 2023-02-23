(ns patients.validate
  "This module contains functions for validating patient data."
  (:require [clojure.spec.alpha :as s]))

;; Common specs
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

;; Specs for patient name
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

;; Specs for address

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

;; Specs for patient
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

;; TODO: Add return path to unvalid params
(defn validate-patient
  "Validates a patient record."
  [patient]
  (s/valid? ::patient patient))


(comment
  (def patient {:name [{:use "usual"
                        :text "Иванов Иван Иванович"
                        :family "Иванов"
                        :given ["Иван" "Иванович"]
                        :prefix ["Mr."]
                        :period {:end "2025-01-01"}}]
                :insurance-number "1234567890123456"
                :gender "male"
                :birth-date "1989-07-06"
                :address [{:use "home"
                           :type "both"
                           :text "Россия, Московская область, Москва, ул. Ленина, д. 25, кв. 43"
                           :line "ул. Ленина, д. 25, кв. 43"
                           :city "Москва"
                           :district "Московская область"
                           :country "Россия"
                           :period {:start "2005-01-01"}}
                          {:use "home"
                           :type "both"
                           :text "Россия, Московская область, Москва, ул. Ленина, д. 25, кв. 43"
                           :line "ул. Ленина, д. 25, кв. 43"
                           :city "Москва"
                           :district "Московская область"
                           :country "Россия"
                           :period {:start "2005-01-01"}}]})


  (s/valid? ::patient
            patient)

  (->> (s/explain-data ::patient patient))


  #:clojure.spec.alpha{:problems
                       ({:path [:insurance-number],
                         :pred (clojure.core/fn [%] (clojure.core/re-matches #"\d{16}" %)),
                         :val "123456789012a3456",
                         :via [:patients.validate/patient :patient/insurance-number],
                         :in [:insurance-number]}
                        {:path [:address],
                         :pred (clojure.core/fn [%] (clojure.core/contains? % :country)),
                         :val
                         {:use "home",
                          :type "both",
                          :text "Россия, Московская область, Москва, ул. Ленина, д. 25, кв. 43",
                          :line "ул. Ленина, д. 25, кв. 43",
                          :city "Москва",
                          :district "Московская область",
                          :country1 "Россия",
                          :period {:start "2005-01-011"}},
                         :via [:patients.validate/patient :patients.validate/address],
                         :in [:address 0]}
                        {:path [:address :period :start],
                         :pred (clojure.core/fn [%] (clojure.core/re-matches #"\d{4}-\d{2}-\d{2}" %)),
                         :val "2005-01-011",
                         :via
                         [:patients.validate/patient
                          :patients.validate/address
                          :patients.validate/period
                          :patients.validate/date],
                         :in [:address 0 :period :start]}),
                       :spec :patients.validate/patient,
                       :value
                       {:name
                        [{:use "usual",
                          :text "Иванов Иван Иванович",
                          :family "Иванов",
                          :given ["Иван" "Иванович"],
                          :prefix ["Mr."],
                          :period {:end "2025-01-01"}}],
                        :insurance-number "123456789012a3456",
                        :gender "male",
                        :birth-date "1989-07-06",
                        :address
                        [{:use "home",
                          :type "both",
                          :text "Россия, Московская область, Москва, ул. Ленина, д. 25, кв. 43",
                          :line "ул. Ленина, д. 25, кв. 43",
                          :city "Москва",
                          :district "Московская область",
                          :country1 "Россия",
                          :period {:start "2005-01-011"}}]}}

  (s/explain-data ::patient
            {:name [{:use "usual"
                     :text "Иванов Иван Иванович"
                     :family "Петров"
                     :given ["Иван" "Иванович"]
                     :prefix ["mr"]}]
             :insurance-number "1234567890123456"
             :gender "male"
             :birth-date "1989-07-06"
             :address [{:use "home"
                        :city "г. Москва"
                        :type "physical"
                        :state "Московская область"
                        :line "ул. Ленина, д. 12"
                        :postalCode "123456"
                        :period {:start "1989-07-06"}
                        :country "RU"
                        :district nil
                        :text "123456, Московская область, г. Москва, ул. Ленина, д. 12"}]})

  (s/valid? ::name [{:use "usual"
                     :text "Иванов Иван Иванович"
                     :family "Иванов"
                     :given ["Иван" "Иванович"]
                     :prefix ["Mr."]
                     :period {:end "2025-01-01"}}])

  (s/valid? ::uuid-string "")

  (s/valid? ::date "1989-07-06")
  (s/valid? ::date "1989-07-01")
  (s/valid? ::date "1989-14-01")

  (s/valid? ::period {:end "1989-01-11"})
  (s/valid? ::period {:start "1989-01-11"})
  )