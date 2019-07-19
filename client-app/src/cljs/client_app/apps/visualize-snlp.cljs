(ns client-app.apps.visualize-snlp
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [client-app.events :as events]
   [client-app.views :as views]
   [client-app.config :as config]
   [cljsjs.d3 :as d3]
   ))


(def height 450)
(def width 540)

(defn append-svg []
  (-> js/d3
      (.select "#slopegraph")
      (.append "svg")
      (.attr "height" height)
      (.attr "width" width)))

