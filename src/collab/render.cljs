(ns collab.render
  (:require [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(q/defcomponent Home []
  (d/div {} "Home"))

(q/defcomponent Editor [room]
  (d/div {} (s/join ["Room: ", room])))

(defn render [elem]
  (let [room (subs (.-hash js/location) 1)]
    (if (s/blank? room)
      (q/render (Home) elem)
      (q/render (Editor room) elem))))
