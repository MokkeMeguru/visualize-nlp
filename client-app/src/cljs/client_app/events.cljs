(ns client-app.events
  (:require
   [re-frame.core :as re-frame]
   [client-app.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::reset-sentences
 (fn [_ sentences]
   (assoc :sentences sentences)))
