(ns patients.validate-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.spec.alpha :as spec]
            [patients.validate :as validate]))

(def valid-patient-data {:name
                         [{:use "usual"
                           :text "Петров Иван Иванович" :family "Петров"
                           :given ["Иван" "Иванович"]}]
                         :address [{:use "home"
                                    :city "г. Москва"
                                    :type "physical"
                                    :state "Московская область"
                                    :line "ул. Ленина, д. 12"
                                    :postalCode "123456"
                                    :country "RU"
                                    :district "Басманный"
                                    :text "123456, Московская область, г. Москва, ул. Ленина, д. 12"}]
                         :insurance-number "9876543210123456"
                         :gender "male"
                         :birth-date "1979-01-01"})

(def invalid-patient-data {:name
                           [{:use "usual"
                             :text "Петров Иван Иванович" :family "Петров"
                             :given ["Иван" "Иванович"]}]
                           :address [{:use "home"
                                      :city "г. Москва"
                                      :type "physical"
                                      :state "Московская область"
                                      :line "ул. Ленина, д. 12"
                                      :postalCode "123456"
                                      :country "RU"
                                      :district "Басманный"
                                      :text "123456, Московская область, г. Москва, ул. Ленина, д. 12"}]
                           :insurance-number "9876543"
                           :birth-date "1979-01-01"})

(deftest test-patient-is-valid?
  (is (validate/patient-is-valid? valid-patient-data))
  (is (not (validate/patient-is-valid? invalid-patient-data))))

(deftest test-get-patient-validation-error-paths
    (is (empty? (validate/get-patient-validation-error-paths valid-patient-data)))

    (is (= [[:gender] [:insurance-number]]
           (validate/get-patient-validation-error-paths invalid-patient-data))))

(deftest test-ne-string-spec
  (is (not (spec/valid? ::validate/non-empty-string 123)))
  (is (not (spec/valid? ::validate/non-empty-string "")))
  (is (spec/valid? ::validate/non-empty-string "string")))

(deftest test-date-string-spec
  (is (spec/valid? ::validate/date "2001-01-01"))
  (is (not (spec/valid? ::validate/date "")))
  (is (not (spec/valid? ::validate/date "2001-02-30")))
  (is (not (spec/valid? ::validate/date "01-02-2002"))))
