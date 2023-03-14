(ns patients.components.styles
  (:require [stylefy.core :as stylefy]))

(def header {:padding "20px 0"})

(def footer {:padding "20px 0"})

(def container {:max-width "1200px"
                :margin "auto auto"})

(def form-button
  {::stylefy/mode {:hover {:background-color "#e3e3e3"
                           :cursor :pointer}}
   :width "200px"
   :background-color :white
   :border "1px solid #ccc"
   :border-radius "4px"
   :padding "7px"
   :text-align :center
   :text-decoration :none
   :display :inline-block
   :font-size "16px"
   :box-shadow "inset 0 1px 1px rgb(0 0 0 / 8%)"})

(def form-control-style
  {:display :block
   :width "100%"
   :height "34px"
   :padding "6px 12px"
   :font-size "16px"
   :line-height "24px"
   :color "#555"
   :background-color "#fff"
   :background-image :none
   :border "1px solid #ccc"
   :border-radius "4px"
   :box-shadow "inset 0 1px 1px rgb(0 0 0 / 8%)"
   :transition "border-color ease-in-out .15s,box-shadow ease-in-out .15s"})

(def form-control-errors {:border "1px solid red"})

(defn form-field-style-with-error
  [is-error?]
  (merge form-control-style
         (when is-error? form-control-errors)))

(def search-box-field
  (merge form-control-style
         {:border "1px solid #dddddd"
          :border-radius "10px"
          :line-height "40px"}))

(def search-box {:width "200px"})
