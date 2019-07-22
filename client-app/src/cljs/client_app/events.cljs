(ns client-app.events
  (:require
   [re-frame.core :as re-frame]
   [client-app.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::reset-sentences
 (fn [_ sentences]
<<<<<<< HEAD
   (assoc db/default-db :sentences sentences)))

(re-frame/reg-event-db
 ::initialize-sentences
 (fn [_ _]
   (assoc db/default-db :sentences
          {"result_tree"
           [[{"children"
              [{"children"
                [{"children"
                  [{"children" [],
                    "dependency_relation" "case",
                    "governor" 1,
                    "index" 2,
                    "lemma" "の",
                    "raw_word" "の",
                    "upos" "ADP"}],
                  "dependency_relation" "nmod",
                  "governor" 3,
                  "index" 1,
                  "lemma" "あなた",
                  "raw_word" "あなた",
                  "upos" "PRON"}
                 {"children" [],
                  "dependency_relation" "case",
                  "governor" 3,
                  "index" 4,
                  "lemma" "は",
                  "raw_word" "は",
                  "upos" "ADP"}],
                "dependency_relation" "nsubj",
                "governor" 5,
                "index" 3,
                "lemma" "進捗",
                "raw_word" "進捗",
                "upos" "NOUN"}
               {"children" [],
                "dependency_relation" "cop",
                "governor" 5,
                "index" 6,
                "lemma" "だ",
                "raw_word" "です",
                "upos" "AUX"}
               {"children" [],
                "dependency_relation" "mark",
                "governor" 5,
                "index" 7,
                "lemma" "か",
                "raw_word" "か",
                "upos" "PART"}
               {"children" [],
                "dependency_relation" "punct",
                "governor" 5,
                "index" 8,
                "lemma" "？",
                "raw_word" "？",
                "upos" "PUNCT"}],
              "dependency_relation" "root",
              "governor" 0,
              "index" 5,
              "lemma" "どう",
              "raw_word" "どう",
              "upos" "ADV"}]]})))

=======
   (assoc :sentences sentences)))
>>>>>>> 2bdfd8b... updata environment
