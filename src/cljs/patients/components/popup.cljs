(ns patients.components.popup
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            [stylefy.core :refer [use-style] :as stylefy]
            [patients.components.locale :refer [locale]]))

;;
;; Styles
;;

(stylefy/keyframes "simple-animation"
                   [:0% {:opacity 0}]
                   [:50% {:opacity 0.8}]
                   [:100% {:opacity 0}])

(defn popup-style
  [color]
  {:display :block
   :position :fixed
   :bottom "25px"
   :right "25px"
   :min-width "200px"
   :min-height "20px"
   :background color
   :border "1px solid black"
   :border-radius "15px"
   :color :white
   :padding "20px"
   :animation-name "simple-animation"
   :animation-duration "3s"
   :animation-iteration-count "forwards"})

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
          [:div (use-style (popup-style color)) (locale message)]))})))

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