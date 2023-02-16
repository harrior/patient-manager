(ns patients.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [re-frame.core :as rf]))
(enable-console-print!)

;; Entry Point

(defn index []
  [:h1 
   "Hello World!"])

(rf/dispatch-sync [:initialize])
(rdom/render [index] (js/document.getElementById "app"))