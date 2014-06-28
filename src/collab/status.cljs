(ns collab.status
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]))

(q/defcomponent StatusOpened []
  (d/div {:className "center-me"}
    (d/div {}
      (d/img {:src "images/loading-bubbles.svg" :height 64 :width 64})
      (d/h3 {} "Waiting for code"))))

(q/defcomponent StatusClosed []
  (d/div {:className "center-me"}
    (d/div {}
      (d/h3 {} "Connection lost")
      (d/button {:className "btn btn-primary"
                 :onClick #(.reload js/location)
                 } "refresh"))))
