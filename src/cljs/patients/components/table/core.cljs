(ns patients.components.table.core
  "This namespace provides the core functionality for table component."
  (:require [re-frame.core :as rf]
            [stylefy.core :refer [use-style] :as stylefy]
            [patients.components.locale :refer [locale]]
            [patients.components.helpers :as helpers]
            [patients.components.table.events :as table-events]
            [patients.components.table.subs :as table-subs]
            [patients.components.styles :as styles]))

;;
;; Styles
;;

(def table-style {:width "100%"
                  :border-collapse :collapse
                  :border "1px solid #dddddd"})

(def table-column-filter-style {:padding "4px"})

(def table-column-style {:padding "5px 0 0"
                         :border "1px solid #dddddd"
                         :border-collapse :collapse})

(def table-row-style {:line-height "40px"
                      :text-align :left
                      ::stylefy/mode {:hover {:background-color "#f5f5f5"
                                              :cursor :pointer}}})
(def table-cell-style {:padding-left "10px"
                       :border "1px solid #dddddd"
                       :border-collapse :collapse
                       :height "24px"})

(def table-controls-style {:display :flex
                           :direction :row
                           :justify-content :space-between
                           :padding "20px 0"
                           :align-items :baseline})

;;
;; Search box
;;

(defn search-box
  "Creates a search box component for the table.

  Args:
  - table-id: The ID of the table."
  [table-id]
  [:div (use-style styles/search-box)
   [:input (merge (use-style styles/search-box-field)
                  {:placeholder (locale :app/search-placeholder)
                   :value @(rf/subscribe [::table-subs/table-search-value table-id])
                   :on-change (fn [e] (let [input-value (helpers/input-value-extractor e)]
                                        (rf/dispatch [::table-events/set-table-search [table-id input-value]])))})]])

;;
;; Table Header
;;

(defn table-header
  "Creates a table header component for the table with buttons and a search box.

  Args:
  - map with keys:
    - table-id: The ID of the table.
    - buttons: Collection of button components."
  [{:keys [table-id buttons]}]
  [:div (use-style table-controls-style)
   [:div
    (doall
     (for [button buttons]
       button))]
   [search-box table-id]])

;;
;; Table
;;

(defn select-input
  "Creates a select input component based on the provided data source.

  Args:
  - common-fields: A map of fields common to all options.
  - map with keys:
    - data-source: A re-frame subscription to the source of the data.
    - value-key: A keyword representing the key used to extract values from the data."
  [common-fields {:keys [data-source value-key]}]
  (let [items @(rf/subscribe [data-source])

        avaliable-options (->> items
                               (map value-key)
                               set
                               sort)]
    [:select common-fields
     [:option {:value ".+" :default true} " "]
     (map (fn [value]
            ^{:key value} [:option {:value (str "^" value "$")} value])
          avaliable-options)]))

(defn- filter-field
  "Creates a filter field component for a table based on the provided field type.

  Args:
  - props: A map with keys:
    - table-id: Unique identifier for the table.
    - data-source: A re-frame subscription to the source of the data.
    - field: A map containing field properties such as value-key and filter-type."
  [{:keys [table-id data-source field] :as props}]
  (let [value-key (:value-key field)

        on-change-fn (fn [event] (let [input-value (helpers/input-value-extractor event)]
                                   (rf/dispatch [::table-events/set-table-filters [table-id value-key input-value]])))

        field-value @(rf/subscribe [::table-subs/table-filter-value table-id value-key])

        common-fields (merge {:id value-key
                              :value field-value
                              :on-change on-change-fn}
                             (use-style styles/form-control-style))]
    (case (:filter-type field)
      :text-input [:input common-fields]

      :select [select-input common-fields {:data-source data-source
                                           :value-key value-key}]

      :date [:input (merge common-fields
                           {:type :date})]
      :none [:div]
      [:div])))

(defn- columns-header
  "Creates a table header row with column titles and filter fields.

  Args:
  - map with keys:
    - table-id: Unique identifier for the table.
    - data-source: A re-frame subscription to the source of the data.
    - fields: A collection of field maps containing properties such as title, value-key, and filter-type."

  [{:keys [table-id data-source fields]}]
  [:thead
   [:tr
    (doall
     (for [field fields]
       (let [column-width (:column-width field)]
         ^{:key field}
         [:th (use-style (merge table-column-style
                                (when column-width
                                  {:width column-width
                                   :max-width column-width
                                   :min-width column-width})))
          [:div (locale (:title field))]
          [:div (use-style table-column-filter-style)]
          [filter-field {:table-id table-id
                         :data-source data-source
                         :field field}]])))]])

(defn- columns
  "Creates a table body with rows of data and optional click events for each row.

  Args:
  - map with keys:
    - table-id: Unique identifier for the table.
    - data-source: A re-frame subscription to the source of the data.
    - fields: A collection of field maps containing properties such as title and value-key.
    - on-click-row: Optional function to be called when a row is clicked."
  [{:keys [table-id data-source fields on-click-row]}]
  (let [filtered-items @(rf/subscribe [::table-subs/table-filtered-items table-id data-source])]
    [:tbody
     (doall
      (for [item filtered-items]
        ^{:key (random-uuid)}
        [:tr (merge
              (use-style table-row-style)
              (when on-click-row
                {:on-click #(on-click-row item)}))
         (doall
          (for [field fields]
            ^{:key field}
            [:td (use-style table-cell-style) ((:value-key field) item)]))]))]))

(defn- table-component
  "Create a table with a given ID, fields, and sorted columns.

  Args:
  - props: A map with keys for the `columns-header` and `columns` functions."
  [props]
  [:table (use-style table-style)
   [columns-header props]
   [columns props]])

(defn table
  "Create a table with a given ID, fields, and sorted columns.

   Params:
   - :table-id: The unique ID of the table.
   - :data-source: The data source for the table (e.g., :patients).
   - :sorted-by: The default column to sort the table by.
   - :on-click-row: A function to handle the on-click event for a row in the table.
   - :fields: A vector of maps containing the following properties for each field:
     - :title: The title of the column.
     - :value-key: The key used to retrieve the value from the data source.
     - :filter-type: The type of filter for the column (:text-input, :select, or :date).
     - :column-width: The width of the column (e.g., \"25%\").

  Example usage:

   [table/table
    {:table-id :table
     :data-source :patients
     :sorted-by :fullname

     :on-click-row (fn [patient]
                     (rf/dispatch [::nav/set-active-page :patient (:identifier patient)]))

     :fields [{:title :patient/fullname
               :value-key :fullname
               :filter-type :text-input
               :column-width \"25%\"}
              {:title :patient/gender
               :value-key :gender
               :filter-type :select
               :column-width \"10%\"}
              {:title :patient/birthday
               :value-key :birth-date
               :filter-type :date
               :column-width \"15%\"}
              {:title :address/text
               :value-key :address
               :filter-type :text-input
               :column-width \"35%\"}]}]])"
  [{:keys [table-id fields sorted-by] :as props}]
  (rf/dispatch [::table-events/init-table [table-id fields sorted-by]])
  (fn [props]
    [table-component props]))
