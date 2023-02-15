(ns patients.handlers
  (:require [ring.util.response :as response]
            [clojure.data.json :as json]
            [hiccup.page :refer [html5 include-css include-js]]))

(defn index
  ;; Redirect to main page
  [_]
  (ring.util.response/redirect "http://localhost:8080/parients"))

(defn patients
  ;; Main page
  [_]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    (include-css "/static/css/style.css")
    ]
   [:body {:class "body-container"}
    [:div#app
     [:h3 "ClojureScript has not been compiled!"]
     [:p "please run "
      [:b "lein figwheel"]
      " in order to start the compiler"]]
    (include-js "/static/js/testapp.js")]))
