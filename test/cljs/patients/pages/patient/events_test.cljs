(ns patients.pages.patient.events-test
  (:require [clojure.test :refer :all]
            [patients.pages.patient.events :as patient-events]))


(deftest test-clear-form-event
  (let [initial-state {:patient-address {:city "New York" :state "NY"}
                       :patient-data {:name "John Doe" :email "johndoe@example.com"}
                       :patient-name {:first-name "John" :last-name "Doe"}}
        expected-state {:patient-address {}
                        :patient-data {}
                        :patient-name {}}]
    (is (= expected-state (patient-events/clear-form initial-state)))))


#_(deftest test-events
  (testing "clear-form"
    (let [initial-state {:patient-address {:city "New York" :state "NY"}
                         :patient-data {:name "John Doe" :email "johndoe@example.com"}
                         :patient-name {:first-name "John" :last-name "Doe"}}
          expected-state {:patient-address {}
                          :patient-data {}
                          :patient-name {}}]
      (is (= expected-state (clear-form initial-state)))))

  (testing "clean-form-errors"
    (let [initial-state {:errors {:patient-name ["Name is required"]}}
          expected-state {:errors {}}]
      (is (= expected-state (clean-form-errors initial-state)))))

  (testing "show-form-validation-errors"
    (let [initial-state {:errors {}}
          expected-state {:errors {:name "Name is required"}}
          transform-fn (fn [_] {:name "Name is required"})]
      (is (= expected-state (show-form-validation-errors initial-state nil transform-fn)))))

  (testing "successful-update"
    (let [fx-result (successful-update nil nil)]
      (is (= [{:dispatch [[:popup/show-success-popup :app/success-updated]]}]
             (r/extract-values fx-result))))))
