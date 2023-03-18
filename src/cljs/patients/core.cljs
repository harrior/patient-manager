(ns patients.core
  "Initializes the patients app and renders the router."
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [stylefy.core :as stylefy]
            [stylefy.reagent :as stylefy-reagent]
            [patients.db :as db]
            [patients.router :refer [router]]))

(defn init
  []
  (rf/dispatch-sync [::db/initialize])
  (stylefy/init {:dom (stylefy-reagent/init)})
  (fn []
    [router]))

;; Entry point

(rdom/render [init] (js/document.getElementById "app"))
