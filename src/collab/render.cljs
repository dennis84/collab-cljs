(ns collab.render
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(q/defcomponent Editor []
  (d/div {} "editor"))

(defn render [elem] (q/render (Editor) elem))
