(ns client-app.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [client-app.events :as events]
   [client-app.views :as views]
   [client-app.config :as config]
   [client-app.apps.visualize-snlp :as vs]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [::events/init-tree-count])
  (re-frame/dispatch-sync [::events/initialize-sentences])
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
