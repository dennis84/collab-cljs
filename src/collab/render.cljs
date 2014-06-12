(ns collab.render
  (:require [cljs.core.async :as a]
            [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d]
            [collab.util :as util])
  (:require-macros [cljs.core.async.macros :as am]))

(q/defcomponent Home []
  (d/div {} "Home"))

(q/defcomponent Member [member]
  (d/li {:className "list-group-item"} (:name member)
    (when (true? (:me member))
      (d/span {:className "glyphicon glyphicon-user pull-right"}))
    (when (true? (:coding member))
      (d/span {:className "glyphicon glyphicon-pencil pull-right"}))))

(q/defcomponent Navigation [state channels]
  (d/div {:className "navigation"}
    (d/ul {:className "list-group"}
      (d/li {:className "list-group-item"} "Follow"
        (d/input {:type "checkbox" :className "pull-right"}))
      (d/li {:className "list-group-item"} "Online"
        (d/span {:className "label label-primary pull-right"} (count (:members state))))
      (d/li {:className "list-group-item"}
        (d/a {:href ""} "Change Nickname")))
    (d/h3 {} "Who's Online")
    (apply d/ul {:className "list-group"}
      (map #(Member (val %)) (:members state)))
  ))

(q/defcomponent Cursor [cursor]
  (d/b {} (:member cursor)))

(q/defcomponent Pane [data]
  (apply d/div {} 
    (d/pre {} (:id (:file data)))
    (map #(Cursor %) (:cursors data))))

(defn- filter-cursors-by-file [cs f]
  (filter #(= (:file %) (:id f)) cs))

(q/defcomponent Panes [state channels]
  (let [cs (:cursors state)
        fs (:files state)]
    (apply d/div {}
      (map #(Pane {:file % :cursors (filter-cursors-by-file cs %)}) fs))))

(q/defcomponent Editor [state channels]
  (d/div {:className "layout"}
    (Navigation state channels)
    (d/div {:className "editor-wrapper"}
      (Panes state channels))))

(defn request-render [app]
  (q/render (Editor @(:state app) (:channels app)) (:dom-element app)))

(defn homepage [app]
  (q/render (Home) (:dom-element app)))
