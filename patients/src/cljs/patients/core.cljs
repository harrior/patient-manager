(ns patients.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [patients.db :as db]
            [patients.router :refer [router]]))

;; Entry point
(rf/dispatch-sync [::db/initialize])
(rdom/render [router] (js/document.getElementById "app"))
