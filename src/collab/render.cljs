(ns collab.render
  (:require [cljs.core.async :as a]
            [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(q/defcomponent Home []
  (d/div {} "Home"))

(q/defcomponent Navigation [state channels]
  (apply d/ul {}
    (map #(d/li {} %) (map #(:name %) (:members state)))))

(q/defcomponent Cursor [cursor]
  (d/b {} (:file cursor)))

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
  (d/div {}
    (Navigation state channels)
    (Panes state channels)))

(defn request-render [app]
  (q/render (Editor @(:state app) (:channels app)) (:dom-element app)))

(defn homepage [app]
  (q/render (Home) (:dom-element app)))
