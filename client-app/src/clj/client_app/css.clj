(ns client-app.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:color "black"}]
  [:.link {:fill "none"}]
  [:.node [:text {:font "12px sans-serif"}]]
)
