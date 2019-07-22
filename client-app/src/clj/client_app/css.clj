(ns client-app.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:color "black"}]
  [:.link {:fill "none"
           :stroke "#ccc"
           :stroke-with "10px"}]
  [:.node {:fill "steelblue"
           :stroke "none"}]
  [:.tooltip
   {:position "absolute"
    :text-align "center"
    :width "auto"
    :height "auto"
    :padding "5px"
    :font "12px"
    :background "white"
    :-webkit-box-shadow "0px 0px 20px rgba(0, 0, 0, 0.8)"
    :-moz-box-shadow "0px 0px 20px rgba(0, 0, 0, 0.8)"
    :box-shadow "0px 0px 20px rgba(0, 0, 0, 0.8)"
    :visibility "hidden"}]
  [:.bar:hover
   {:fill "Brown"}])
