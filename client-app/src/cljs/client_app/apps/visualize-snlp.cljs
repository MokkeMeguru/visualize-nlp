(ns client-app.apps.visualize-snlp
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [client-app.events :as events]
   [client-app.views :as views]
   [client-app.config :as config]
   [cljsjs.d3 :as d3]
   [client-app.subs :as subs]
   [goog.string :as gstr]
   [cljs.spec.alpha :as spec]))

(defn consume-tree-data
  ([]
   (consume-tree-data "result_tree"))
  ([category]
   (-> @(re-frame/subscribe [::subs/sentences]) clj->js (aget category))))

(defn gen-properties
  ([]
   {:width 800
    :height 600
    :root "#treegraph"
    :margin {:top 100 :left 100}
    :duration 500})
  ([properties]
   (merge
    (gen-properties)
    properties)))

(defn preprocess-root [root]
  (cond
    (nil? (.-children root)) ()
    (= 0  (->  root .-children .-length)) (set! (.-children root) nil)
    :else (.forEach (.-children root) #(preprocess-root % nil nil) ))
  root)

(defn gen-root [data idx properties]
  ;; TODO fix bug : first -> nth or nth -> first
  (let [root (-> js/d3
                 (.hierarchy (preprocess-root (first (nth data idx)))))
        _ (set! (.-x0 root) (/ (:height root) 2))
        _ (set! (.-y0 root) 0)]
    root))

(defn gen-tree [properties]
  (-> js/d3
      .tree
      (.size (clj->js [(:height properties) (- (:width properties) 160)]))))

(defn remove-svg [properties]
  (-> js/d3
      (.select (:root properties))
      (.selectAll "svg")
      .remove))

(defn gen-svg [properties]
  (let [margin-top (get-in properties [:margin  :top])
        margin-left (get-in properties [:margin :left])]
    (remove-svg (gen-properties))
   (-> js/d3
       (.select (:root properties))
       (.append "svg")
       (.attr "width" (+ (:width properties) margin-left))
       (.attr "height" (+ (:height properties) margin-top))
       (.append "g")
       (.attr "transform" (gstr/format "translate(%d, %d)" margin-left margin-top))
       (.attr "width"(:width properties))
       (.attr "height" (:height properties)))))

(defn toggle [selected-node]
  (if-not (nil? (-> selected-node .-children))
    (do
      (set! (.-children_hidden selected-node) (.-children selected-node))
      (set! (.-children selected-node) nil))
    (do
      (set! (.-children selected-node)  (.-children_hidden selected-node))
      (set! (.-children_hidden selected-node) nil)))
  selected-node)

(defn set-node-id [node]
  (when (nil? (.-id node))
     (re-frame/dispatch-sync [::events/inc-tree-count])
     (set! (.-id node) @(re-frame/subscribe [::subs/tree-count])))
  (.-id node))

(defn init-sentence-tree
  ([idx]
   (let [tree-data (consume-tree-data)
         properties (gen-properties)
         root (gen-root tree-data idx properties)
         tree (gen-tree properties)
         svg (gen-svg properties)]
     (re-frame/dispatch-sync [::events/init-tree-count])
     (letfn [(update-tree [source]
               (tree root)
               (.each root #(set! (.-y %) (* 160 (.-depth %))))
               (let [node (-> svg
                              (.selectAll ".node")
                              (.data (.descendants root) set-node-id))
                     node-enter (-> node
                                    .enter
                                    (.append "g")
                                    (.attr "class" "node")
                                    (.attr "transform" #(gstr/format "translate(%d,%d)" (.-x0 source) (.-y0 source) ))
                                    (.on "click" #(do (toggle %) (update-tree %))))
                     _  (-> node-enter
                            (.append "circle")
                            (.attr "r" 5)
                            (.style "fill" #(if (nil? (.-children_hidden %)) "#fff" "lightsteelblue")))
                     _ (-> node-enter
                           (.append "text")
                           (.attr "x" #(if (or (.-children_hidden %) (.-children %)) -13 13))
                           (.attr "dy" 3)
                           (.attr "font-size" "150%")
                           (.attr "text-anchor" #(if (or (.-children_hidden %) (.-children %)) "end" "start"))
                           (.text #(-> % .-data .-raw_word))
                           (.style "fill-opacity" 1))
                     node-update (.merge   node-enter node)
                     _ (-> node-update
                           .transition
                           (.duration (:duration properties))
                           (.attr "transform" #(gstr/format "translate(%d, %d)" (.-x %) (.-y %))))
                     _ (-> node-update
                           (.select "circle")
                           (.attr "r" 8)
                           (.style "fill" #(if (nil? (.-children_hidden %))  "#fff" "lightsteelblue")))
                     _ (-> node-update
                           (.select "text")
                           (.style "opacity" 1))
                     node-exit (-> node
                                   .exit
                                   .transition
                                   (.duration (:duration properties))
                                   (.attr "transform" #(gstr/format "translate(%d,%d)" (.-x source) (.-y source)))
                                   .remove)
                     _ (-> node-exit
                           (.select "circle")
                           (.attr "r" 1e-6))
                     _ (-> node-exit
                           (.select "text")
                           (.style "fill-opacity" 1e-6))
                     link (-> svg
                              (.selectAll ".link")
                              (.data (.links root) #(-> % .-target .-id)))
                     link-text (-> svg
                              (.selectAll ".link_text")
                              (.data (.links root) #(-> % .-target .-id)))
                     link-text-enter (-> link-text
                                         .enter
                                         (.insert "g" "g")
                                         (.attr "class" "link_text"))
                     link-enter (->  link
                                    (.append "path")
                                    (.attr "d" (-> js/d3
                                                   .linkVertical
                                                   (.x #(.-x0 source))
                                                   (.y #(.-y0 source)))))
                     _ (-> link-text-enter
                           (.append "text")
                           (.attr  "font-size" "150%")
                           (.attr "transform" #(gstr/format "translate(%d,%d)"
                                                            (/ (+ (-> % .-source .-x) (-> % .-target .-x)) 2)
                                                            (/ (+ (-> % .-source .-y) (-> % .-target .-y)) 2)
                                                            ))
                           (.attr "dy" 3)
                           (.attr "font-size" "100%")
                           (.attr "text-anchor" "middle")
                           (.attr "stroke-width" "1px")
                           (.attr "stroke" "black")
                           (.text #(-> % .-target .-data .-dependency_relation))
                           (.style "fill-opacity" 1))
                    link-update (.merge link-enter link)
                    link-text-update (.merge link-text-enter link-text)
                     _ (-> link-update
                           .transition
                           (.duration (:duration properties))
                           (.attr "d" (-> js/d3
                                          .linkVertical
                                          (.x #(.-x %))
                                          (.y #(.-y %)))))

                     _ (-> link
                           .exit
                           .transition
                           (.duration (:duration properties))
                           (.attr "d" (-> js/d3
                                          .linkVertical
                                          (.x #(.-x source))
                                          (.y #(.-y source))))
                           .remove)
                     _ (-> link-text
                           .exit
                           .transition
                           (.duration (:duration properties))
                           (.attr "transform" #(gstr/format "translate(%d,%d)"
                                                            (-> % .-source .-x)
                                                            (-> % .-source .-y)
                                                            )
.remove
)
                     _ (-> link-text
                           .exit
                           .transition
                           (.duration (:duration properties))
                          (.attr "font-size" "0")
                           .remove)
                     ]
                 (-> node
                     (.each #(do
                               (set! (.-x0 %) (.-x %))
                               (set! (.-y0 %) (.-y %)))))
                 ))]
       (update-tree root)))))

(re-frame/dispatch-sync [::events/initialize-sentences])
(init-sentence-tree 0)

;; (-> example-tree-data first first .-x0)
;; (.-x0 example-tree-data)
;; (def update-tree (init-sentence-tree 0))

;; (def tree-data (consume-tree-data))
;; (def properties (gen-properties))
;; (def root (gen-root tree-data 0 properties))
;; (def tree (gen-tree properties))
;; (def svg (gen-svg properties))
;; (re-frame/dispatch-sync [::events/init-tree-count])


;; (update-tree root)


;; TODO: GENERATE TEST FILE
;; (re-frame/dispatch-sync [::events/initialize-sentences])
;; (def example-tree-data (consume-tree-data))
;; (nil? (-> example-tree-data first first .-id))
;; (spec/valid? = (-> example-tree-data first first) (toggle (toggle (-> example-tree-data first first))))
;; (do
;;   (re-frame/dispatch-sync [::events/init-tree-count])
;;   (spec/valid? = (re-frame/subscribe [::subs/tree-count]) 0)
;; (spec/valid? > (re-frame/dispatch-sync [::events/inc-tree-count])(re-frame/subscribe [::subs/tree-count])))
