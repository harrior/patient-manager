(ns patients.components.locale)

(def strings
  {:ru {:app/title "База данных пациентов"
        :app/add-patient "Добавить пациента"
        :app/edit-patient "Редактировать пациента"
        :app/search-placeholder "Поиск"
        :app/create "Создать"
        :app/save "Сохранить"
        :app/patient-data "Данные пациента"
        :app/patient-address "Адрес"

        :patient/fullname "ФИО"
        :patient/gender "Пол"
        :patient/birthday "Дата рождения"
        :patient/insurance-number "Полис"
        :patient/family "Фамилия"
        :patient/name "Имя"
        :patient/patronymic "Отчество"
        :address/city "Город"
        :address/state "Область\\Край"
        :address/line "Адрес"
        :address/text "Адрес"
        :address/postalCode "Индекс"
        :address/district "Район"}})

(defn locale
  ([key]
   (locale :ru key))
  ([locale key]
   (or (get-in strings [locale key])
       (name key))))