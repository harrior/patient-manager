(ns patients.rpc-test
  (:require [clojure.test :refer [deftest is]]
            [patients.rpc :as rpc]))

(deftest handle-rpc-errors-test
    (let [response (rpc/handle-rpc-errors :test-method {:param "test-value"} (Exception. "test-error-message"))]
      (is (= response {:status 200
                       :headers {"Content-Type" "application/edn"}
                       :body "{:status :error, :data {:text \"test-error-message\"}}"}))))

(deftest unknown-method-test
    (let [response (rpc/rpc-handler :unknown-method {})
          body-dict (-> response
                        :body
                        read-string)]
      (is (= (:status response) 200))
      (is (= (:headers response) {"Content-Type" "application/edn"}))
      (is (= body-dict {:status :error, :data {:text "Method :unknown-method not implemented."}}))))