(ns client-app.apps.visualize-snlp
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [client-app.events :as events]
   [client-app.views :as views]
   [client-app.config :as config]
   [cljsjs.d3 :as d3]))

(def height 450)
(def width 540)

(defn append-svg []
  (-> js/d3
      (.select "#slopegraph")
      (.append "svg")
      (.attr "height" height)
      (.attr "width" width)))

(defn remove-svg []
  (-> js/d3
      (.selectAll "#slopegraph svg")
      (.remove)))

(def example-data {2005 {:natural-gas 0.2008611514256557
                         :coal        0.48970650816857986
                         :nuclear     0.19367190804075465
                         :renewables  0.02374724819670379}
                   2015 {:natural-gas 0.33808321253456974
                         :coal        0.3039492492908485
                         :nuclear     0.1976276775179704
                         :renewables  0.08379872568702211}})

(defn format-percent [value]
  ((.format js/d3 ".2%") value))

(defn format-name [name-str]
  (-> name-str
      (clojure.string/replace "-" " ")
      (clojure.string/capitalize)))

(defn attrs [e1 m]
  (doseq [[k v] m]
    (.attr e1 k v)))

(def height-scale
  (-> js/d3
      (.scaleLinear)
      (.domain #js [0 1])
      (.range #js [(- height 15) 0])))

(defn data-join [percent tag class data]
  (let [join (-> percent
                 (.selectAll (str tag "." class))
                 (.data (into-array data)))
        enter (-> join
                  (.enter)
                  (.append tag)
                  (.classed class true))]
    (-> join (.exit) (.remove))
    (.merge join enter)))

(def column-1-start (/ width 4))
(def column-space (* 3 (/ width 4)))

(defn draw-header [svg years]
  (-> svg
      (data-join "text" "slopegraph-header" years)
      (.text (fn [data _] (str data)))
      (attrs {
              "x" (fn [_ index]
                    (+ 50 (* index column-space)))
              "y" 15})))

(defn draw-line [svg data-col-1 data-col-2]
  (-> svg
      (data-join "line" "slopegraph-line" data-col-1)
      (attrs {"x1" (+ 5 column-1-start)
              "x2" (- column-space 5)
              "y1" (fn [[_ v]]
                     (height-scale v))
              "y2" (fn [[k _]]
                     (height-scale (get data-col-2 k)))})))

(defn draw-column [svg data-col index custom-attrs]
  (-> svg
      (data-join "text" (str "text.slopegraph-column-" index) data-col)
      ;; (.selectAll (str "text.slopegraph-column-" index))
      ;; (.data (into-array data-col))
      ;; (.enter)
      ;; (.append "text")
      ;; (.classed (str "slopegraph-column" index) true)
      (.text (fn [[k v]] (str (format-name (name k)) " " (format-percent v))))
      (attrs (merge custom-attrs
                    {"y" (fn [[_ v]] (height-scale v))}))))

(defn draw-slopegraph [svg data]
  (let [data-2005 (get data 2005)
        data-2015 (get data 2015)]
    (draw-column svg data-2005 1 {"x" column-1-start})
    (draw-column svg data-2015 1 {"x" column-space})
    (draw-header svg [2005 2015])
    (draw-line svg data-2005 data-2015)))

