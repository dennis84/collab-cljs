(ns collab.navigation
  (:require [cljs.core.async :as a]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(q/defcomponent Member [member]
  (d/li {:className "list-group-item"} (:name member)
    (when (true? (:me member))
      (d/span {:className "glyphicon glyphicon-user pull-right"}))
    (when (true? (:coding member))
      (d/span {:className "glyphicon glyphicon-pencil pull-right"}))))

(q/defcomponent File [file channels]
  (d/li {:className "list-group-item"}
    (d/a {:href "#" :onClick #(am/go (a/>! (:show-file channels) file))
          } (:id file))))

(q/defcomponent Navigation [state channels]
  (d/div {:className "navigation"}
    (d/ul {:className "list-group"}
      (d/li {:className "list-group-item"} "Follow"
        (d/input {:type "checkbox"
                  :className "pull-right"
                  :checked (:follow state)
                  :onClick (fn [e] (let [value (.-checked (.-target e))]
                             (am/go (a/>! (:toggle-follow channels) value))))
                  }))
      (d/li {:className "list-group-item"} "Online"
        (d/span {:className "label label-primary pull-right"}
          (count (:members state))))
      (d/li {:className "list-group-item"}
        (d/a {:href "#"
              :data-toggle "modal"
              :data-target "#change-nick"} "Change Nickname")))
    (d/h3 {} "Who's Online")
    (apply d/ul {:className "list-group"}
      (map #(Member %) (vals (:members state))))
    (d/h3 {} "Files")
    (apply d/ul {:className "list-group"}
      (map #(File % channels) (vals (:files state))))))
