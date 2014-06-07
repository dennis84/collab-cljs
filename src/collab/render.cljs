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
    (map #(d/div {} %) (:members state))))

(defn request-render [app]
  (let [room (subs (.-hash js/location) 1)]
    (if (s/blank? room)
      (q/render (Home) (:dom-element app))
      (q/render (Editor @(:state app) (:channels app)) (:dom-element app)))))
