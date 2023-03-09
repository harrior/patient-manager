(ns patients.components.popup
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            [patients.components.locale :refer [locale]]))

;;
;; Popup
;;

(defn popup
  [message color]
  (let [visible? (r/atom true)]
    (r/create-class
     {:component-did-mount
      (fn []
        (js/setTimeout #(reset! visible? false) 3000))
      :reagent-render
      (fn []
        (when @visible?
          [:div {:style {:display :block
                         :position :fixed
                         :bottom 25
                         :right 25
                         :min-width 200
                         :min-height 20
                         :background color
                         :border "1px solid black"
                         :border-radius 15
                         :color :white
                         :font-size 16
                         :padding 20
                         :opacity 0.9
                         :animation "show-and-hide 3s forwards"}} (locale message)]))})))

(defn show-popup
  [message color]
  (let [container (js/document.createElement "div")]
    (js/document.body.appendChild container)
    (rd/render [popup message color] container)))

(defn show-error-popup
  [message]
  (show-popup message :red))

(defn show-success-popup
  [message]
  (show-popup message :green))

;;
;; Events
;;

(rf/reg-event-fx
 ::show-error-popup
 (fn [_ [_ message]]
   (show-error-popup message)
   {}))

(rf/reg-event-fx
 ::show-success-popup
 (fn [_ [_ message]]
   (show-success-popup message)
   {}))