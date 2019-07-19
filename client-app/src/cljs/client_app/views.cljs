(ns client-app.views
  (:require
   [re-frame.core :as re-frame]
   [client-app.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [:div#slopegraph]
     ]))
