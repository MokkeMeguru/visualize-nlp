(ns client-app.views
  (:require
   [re-frame.core :as re-frame]
   [client-app.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 "Hello from " @name]
     [:div.container {:style {:width "650px"}}
      [:div.row
       [:div.mx-auto {:style {:width "200px"}} "テキスト入力欄"]
       [:textarea.form-control {:rows 3 :placeholder "テキスト"} ]]]
     [:div#treegraph]
     [:div#animation]
     [:div#bar-chart]
     ]))
