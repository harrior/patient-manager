(ns patients.components.popup
  (:require [reagent.core :as r]
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

(defn popup-component
  "Popup component that will display a message with a specific color and
   automatically close after 3 seconds.

   Params:
    - message: The text content of the popup.
    - color: The background color of the popup."
  [message color]
  (r/create-class
   {:component-did-mount
    (fn []
      (js/setTimeout #(rf/dispatch [::remove-popup]) 3000))
    :reagent-render
    (fn []
      [:div (use-style (popup-style color)) (locale message)])}))

;;
;; Events
;;

(rf/reg-event-db
 ::show-error-popup
 ^{:doc
   "Shows an error popup with a red background color.

    Params:
     - message: The text content of the error popup."}
 (fn [db [_ message]]
   (assoc db :popup [popup-component message :red])))

(rf/reg-event-db
 ::show-success-popup
 ^{:doc
   "Shows an error popup with a green background color.

    Params:
     - message: The text content of the success popup."}
 (fn [db [_ message]]
   (assoc db :popup [popup-component message :green])))

(rf/reg-event-db
 ::remove-popup
 ^{:doc "Removes the active popup from the application state."}
 (fn [db _]
   (assoc db :popup nil)))

;;
;; Subs
;;

(rf/reg-sub
 :active-popup
 ^{:doc "Returns the active popup component."}
 (fn [db _]
   (get db :popup)))

;;
;; Include
;;

(defn popup
  "Function to include the active popup to a page."
  []
  (let [current-popup @(rf/subscribe [:active-popup])]
    (when current-popup
      [:<> ^{:key (str (rand-int 1000000))} current-popup])))
