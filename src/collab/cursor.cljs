(ns collab.cursor
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(defn- get-cursor-top [c]
  (str (-> (:y c) (- 1) (* 23)) "px"))

(defn- get-cursor-left [c]
  (str (- (:x c) 1) "ch"))

(q/defcomponent Cursor [c]
  (q/on-mount
    (d/div {:className "cursor-container"
            :style {:top (get-cursor-top c) :left (get-cursor-left c)}
            :title (:member c)
            :data-placement "top"}
      (d/div {:className "cursor"}))
    (fn [node]
      (let [$elem (js/$ node)]
        (.tooltip $elem (clj->js {:container $elem}))
        (.tooltip $elem "show")
        (js/setTimeout #(.tooltip $elem "hide") 3000)))))
