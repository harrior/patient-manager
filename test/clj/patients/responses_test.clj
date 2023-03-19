(ns patients.responses-test
  (:require [clojure.test :refer [deftest is]]
            [patients.responses :as responses]))

(deftest generate-response-test
  (let [result (responses/generate-response {:status :ok :data {:name "John" :age 30}})]
    (is (= result {:status 200
                   :headers {"Content-Type" "application/edn"}
                   :body "{:status :ok, :data {:name \"John\", :age 30}}"})))

  (let [result (responses/generate-response {:status :error :data {:text "Not found"}})]
    (is (= result {:status 200
                   :headers {"Content-Type" "application/edn"}
                   :body "{:status :error, :data {:text \"Not found\"}}"}))))