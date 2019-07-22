(ns client-app.db)

(def default-db
  {:name "re-frame x D3.js"
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
