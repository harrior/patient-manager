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
  (stylefy/init {:dom (stylefy-reagent/init)})
  [router])

;; Entry point
(rf/dispatch-sync [::db/initialize])
(rdom/render [init] (js/document.getElementById "app"))
