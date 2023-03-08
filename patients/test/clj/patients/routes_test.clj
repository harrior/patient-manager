(ns patients.routes-test
  (:require [clojure.test :refer :all]
            [patients.routes :as routes]
            [ring.mock.request :as mock]))

(deftest handler-not-exists-method-test
  (let [request (mock/request :post "/rpc" {:method :some-method})
        response (routes/handler request)]
    (is (= 500 (:status response)))
    (is (= {"Content-Type" "application/edn"} (:headers response)))
    (is (map? (read-string (:body response))))))

(deftest index-page-test
  (let [response (routes/handler (mock/request :get "/"))]
    (is (= 302 (:status response)))
    (is (= '{"Location" "/index.html"} (:headers response)))))

(deftest not-exists-page-test
  (let [response (routes/handler (mock/request :get "/not-exists-page"))]
    (is (= 404 (:status response)))))