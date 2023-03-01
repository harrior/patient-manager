(ns patients.components.locale)

(def strings
  {:ru {:app/title "База данных пациентов"
        :app/add-patient "Добавить пациента"
        :app/search-placeholder "Поиск"
        :patients/fullname "ФИО"
        :patients/gender "Пол"
        :patients/birthday "Дата рождения"
        :patients/address "Адрес"
        :patients/insurance-number "Полис"
        }})

(defn locale
  ([key]
   (locale :ru key))
  ([locale key]
   (or (get-in strings [locale key])
       :keyword-not-defined)))