(ns collab.render
  (:require [cljs.core.async :as a]
            [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(q/defcomponent Home []
  (d/div {} "Home"))

(q/defcomponent Editor [state channels]
  (apply d/div {}
    (map #(d/div {} %) (map #(:id %) (:members state)))))

(defn request-render [app]
  (q/render (Editor @(:state app) (:channels app)) (:dom-element app)))

(defn homepage [app]
  (q/render (Home) (:dom-element app)))
