(ns patients.components.ui-elements
  "A namespace for creating UI components."
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style]]
            [patients.components.locale :refer [locale]]
            [patients.components.helpers :as h]
            [patients.components.popup :refer [popup]]
            [patients.components.styles :as styles]))

;;
;; Events
;;

(rf/reg-event-db
 :set-input-value
 ^{:doc "Sets the input value of a form field in the app state."}
 (fn [db [_ form-id field-id value]]
   (assoc-in db [form-id field-id] value)))

;;
;; Subs
;;

(rf/reg-sub
 :input-value
 ^{:doc "Returns the input value of a form field."}
 (fn [db [_ form-id field-id]]
   (get-in db [form-id field-id])))

(rf/reg-sub
 :field-has-error?
 ^{:doc "Returns true if the form field has an error, false otherwise"}
 (fn [db [_ form-id field-id]]
   (not (nil? (get-in db [:errors form-id field-id])))))

;;
;; Form components
;;

(defn- form-field
  "Creates a form field element with the specified properties.

  Params:
  - label: The label for the form field.
  - form-id: The ID of the form containing the field.
  - field-id: The ID of the form field.
  - field-type: The type of the form field (:input, :select, or :date).
  - options: A collection of options for the select field (only used for field-type :select).
  - on-change: An optional custom function to handle the field's on-change event. "
  [{:keys [label
           form-id
           field-id
           field-type
           options
           on-change]}]
  (let [field-has-error? @(rf/subscribe [:field-has-error? form-id field-id])
        value @(rf/subscribe [:input-value form-id field-id])
        field-styles (use-style (styles/form-field-style-with-error field-has-error?))

        on-change-fn (if on-change
                       on-change
                       (fn [event] (rf/dispatch [:set-input-value
                                                 form-id
                                                 field-id
                                                 (h/input-value-extractor event)])))

        common-props (merge field-styles
                            {:id field-id
                             :value value
                             :on-change on-change-fn})]
    ^{:key [form-id field-id]}
    [:label (locale label)
     (case field-type
       :input [:input common-props]
       :select [:select (assoc common-props :value (or value :none))
                (doall
                 (for [{:keys [id text params] :or {params {}}} options]
                   ^{:key id} [:option (assoc params :value id) (locale (or text id))]))]
       :date [:input (assoc common-props
                            :type :date)])]))

(defn input-field
  "Creates an input field element with the specified properties."
  [props]
  (form-field (assoc props :field-type :input)))

(defn select-field
  "Creates a select field element with the specified properties."
  [props]
  (form-field (assoc props :field-type :select)))

(defn date-field
  "Creates a date input field element with the specified properties."
  [props]
  (form-field (assoc props :field-type :date)))


(defn button
  "Button element with the specified properties.

  Params:
  - label: The label for the button.
  - id: The ID of the button.
  - on-click: The function to handle the button's on-click event."
  [{:keys [label id on-click]}]
  [:button (merge (use-style styles/form-button)
                  {:id id
                   :on-click on-click})
   (locale label)])

;;
;; Visual components

(defn spacer
  "Spacer element that fills the available space."
  []
  [:div (use-style {:flex-grow 1})])

(defn fieldset-row
  "Fieldset element with a title and specified children, arranged in a row.

  Params:
  - title: The title for the fieldset.
  - children: The child elements as fieldset-columns."
  [{:keys [title]} & children]
  [:fieldset (use-style {:display :flex
                         :gap "10px"})
   [:legend (locale title)]
   (doall
    (for [child children]
      child))])

(defn fieldset-column
  "Container with elements arranged in a column."
  [& children]
  (into
   [:div (use-style {:display :flex
                     :flex-direction :column
                     :width "100%"})
    children]))

(defn header
  "Table header."
  [label]
  [:header (use-style styles/header)
   [:h1 (locale label)]])

(defn footer
  "Footer component."
  []
  [popup])