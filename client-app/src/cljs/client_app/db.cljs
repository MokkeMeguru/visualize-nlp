(ns client-app.db)

(def default-db
  {:name "visualize-nlp"
   :example-dp [
                {:id  1
                 :word "hoge"
                 :children  [
                               {:id  2,
                                :word  "foo"
                                :children []}
                               {:id  3
                                :word  "bar"
                                :children  []}
                             ]}]})
