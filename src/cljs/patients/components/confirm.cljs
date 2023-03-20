(ns patients.components.confirm
  "This namespace contains components and events for displaying and
   handling a confirmation dialog in the application."
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style]]
            [patients.components.styles :as styles]
            [patients.components.locale :refer [locale]]))

;;
;; Styles
;;

(def dialog-style
  {:display :flex
   :flex-direction :column
   :align-items :center
   :justify-content :center
   :background-color "white"
   :border "1px solid black"
   :border-radius "8px"
   :padding "20px"
   :width "300px"})

(def backdrop-style
  {:position :fixed
   :top 0
   :left 0
   :width "100%"
   :height "100%"
   :background-color "rgba(0, 0, 0, 0.5)"
   :display :flex
   :align-items :center
   :justify-content :center})

(def button-style
  (assoc styles/form-button :width "80px"))

;;
;; Confirm component
;;

(defn confirm-dialog
  "Confirm dialogue component.
   Params:
    - message: Text of the message.
    - on-yes: Callback event for \"Yes\" button.
    - on-no: Callback event for \"No\" button."
  [message on-yes on-no]
  [:div (use-style dialog-style)
   [:div (locale message)]
   [:div (use-style {:display :flex
                     :gap "50px"
                     :padding "10px"})
    [:button (merge (use-style button-style)
                    {:on-click on-yes})  (locale :app/yes)]
    [:button (merge (use-style button-style)
                    {:on-click on-no})  (locale :app/no)]]])

;;
;; Events
;;

(rf/reg-event-db
 ::show-confirm-dialog
 ^{:doc "Shows confirm dialogue."}
 (fn [db [_ {:keys [message on-yes on-no]}]]
   (assoc db :confirm-dialog {:message message
                             :on-yes on-yes
                             :on-no on-no})))

(rf/reg-event-db
 ::remove-confirm-dialog
 ^{:doc "Removes confirm dialogue."}
 (fn [db _]
   (assoc db :confirm-dialog nil)))

;;
;; Subs
;;

(rf/reg-sub
 :active-confirm-dialog
 ^{:doc "Return active confirm dialogue."}
 (fn [db _]
   (get db :confirm-dialog)))

;;
;; Include
;;

(defn confirm
  "Function to include the active confirm dialogue to a page."
  []
  (let [active-dialog @(rf/subscribe [:active-confirm-dialog])]
    (when active-dialog
      (let [{:keys [message on-yes on-no]} active-dialog
            on-yes-fn #(do (when on-yes
                             (rf/dispatch on-yes))
                           (rf/dispatch [::remove-confirm-dialog]))
            on-no-fn #(do (when on-no
                            (rf/dispatch on-no))
                          (rf/dispatch [::remove-confirm-dialog]))]
        [:div (use-style backdrop-style)
         [confirm-dialog message on-yes-fn on-no-fn]]))))