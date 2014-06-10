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

(q/defcomponent Panes [state channels]
  (apply d/ul {}
    (map #(d/li {} %) (map #(:id %) (:files state)))))

(q/defcomponent Cursors [state channels]
  (apply d/ul {}
    (map #(d/li {} %) (map #(:id %) (:cursors state)))))

(q/defcomponent Editor [state channels]
  (d/div {}
    (Navigation state channels)
    (Panes state channels)
    (Cursors state channels)))

(defn request-render [app]
  (q/render (Editor @(:state app) (:channels app)) (:dom-element app)))

(defn homepage [app]
  (q/render (Home) (:dom-element app)))
