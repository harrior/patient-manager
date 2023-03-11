(ns patients.serializers
  "Namespace for serialising patient data.")

(defn patient-serialiser
  "Serialises patient data.
   Accepts a `patient` record, and returns a serialised flat map of patient data

  Args:
    patient (map): A patient record.

  Returns:
    A map containing the serialised patient data with the following keys:
      - :insurance-number: The patient's insurance number.
      - :fullname: The patient's full name.
      - :address: The patient's address.
      - :gender: The patient's gender.
      - :birth-date: The patient's birth date.
      - :identifier: The patient's identifier.

  Raises:
    Exception: If any required fields are missing in the `patient` record."
  [patient]
  (let [patient* (:patients/patient patient)
        fullname (get-in patient* [:name 0 :text])
        address (get-in patient* [:address 0 :text])
        gender (:gender patient*)
        birth-date (:birth-date patient*)
        identifier (:patients/id patient)
        insurance-number (:insurance-number patient*)]
    {:insurance-number insurance-number
     :fullname fullname
     :address address
     :gender gender
     :birth-date birth-date
     :identifier identifier}))