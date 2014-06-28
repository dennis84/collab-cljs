(ns collab.editor
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]
            [collab.pane :refer (Panes)]
            [collab.change-nick :refer (ChangeNick)]
            [collab.navigation :refer (Navigation)]
            [collab.status :refer (StatusOpened StatusClosed)]))

(q/defcomponent Editor [state channels conn]
  (d/div {:className "layout"}
    (Navigation state channels)
    (d/div {:className "editor-wrapper"}
      (when (and (= (:status state) "opened") (nil? (:files state)))
        (StatusOpened))
      (when (= (:status state) "closed")
        (StatusClosed))
      (Panes state channels))
    (ChangeNick conn)))

(defn request-render [app]
  (q/render
    (Editor @(:state app) (:channels app) (:connection app))
    (:dom-element app)))
