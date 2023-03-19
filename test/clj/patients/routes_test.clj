(ns patients.routes-test
  (:require [clojure.test :refer [deftest is]]
            [patients.routes :as routes]
            [ring.mock.request :as mock]))

;;
;; Helpers
;;

(defn make-edn-post-request
  "Create and execute a POST request with EDN-encoded data to the specified URI.
   Returns the response as a Clojure data structure."
  [uri params]
  (let [response (routes/app
                  (-> (mock/request :post uri)
                      (assoc :body (pr-str params))
                      (mock/content-type "application/edn")))]
    (update response :body read-string)))

;;
;; Tests
;;

(deftest handler-not-exists-method-test
  (is (= (make-edn-post-request "/rpc" {:method :some-method})
         {:status 200
          :headers {"Content-Type" "application/edn"}
          :body {:status :error, :data {:text "Method :some-method not implemented."}}})))

(deftest handler-get-status-method-test
  (is (= (make-edn-post-request "/rpc" {:method :status})
         {:status 200
          :headers {"Content-Type" "application/edn"}
          :body {:status :ok :data {:message "Backend is up and running"}}})))

(deftest index-page-test
  (let [response (routes/handler (mock/request :get "/"))]
    (is (= 302 (:status response)))
    (is (= '{"Location" "/index.html"} (:headers response)))))

(deftest not-exists-page-test
  (let [response (routes/handler (mock/request :get "/not-exists-page"))]
    (is (= 404 (:status response)))))