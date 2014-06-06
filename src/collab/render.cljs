(ns collab.render
  (:require [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(q/defcomponent Home []
  (d/div {} "Home"))

(q/defcomponent Editor [state]
  (apply d/div {}
    (map #(d/div {} %) (:members state))))

(defn render [app]
  (let [room (subs (.-hash js/location) 1)]
    (if (s/blank? room)
      (q/render (Home) (:dom-element app))
      (q/render (Editor (:state app)) (:dom-element app)))))
