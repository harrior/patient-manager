(ns patients.serializers-test
  (:require [clojure.test :refer [deftest is]]
            [patients.serializers :as serializers]))

(deftest patient-serialiser-test
    (let [patient {:patients/id #uuid "65c09345-28c9-4868-90b7-de56cec20b2a"
                   :patients/patient {:name
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
                                      :birth-date "1979-01-01"}}]
      (is (= (serializers/patient-serialiser patient)
             {:insurance-number "9876543210123456",
              :fullname "Петров Иван Иванович",
              :address "123456, Московская область, г. Москва, ул. Ленина, д. 12",
              :gender "male",
              :birth-date "1979-01-01",
              :identifier #uuid "65c09345-28c9-4868-90b7-de56cec20b2a"}))))
