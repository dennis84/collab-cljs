(ns collab.editor
  (:require [cljs.core.async :as a]
            [clojure.string :as s]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d]
            [collab.highlight :as hl]
            [collab.util :as util])
  (:require-macros [cljs.core.async.macros :as am]))

(defn- class-name [classes]
  (apply str (interpose " " (map identity classes))))

(q/defcomponent Member [member]
  (d/li {:className "list-group-item"} (:name member)
    (when (true? (:me member))
      (d/span {:className "glyphicon glyphicon-user pull-right"}))
    (when (true? (:coding member))
      (d/span {:className "glyphicon glyphicon-pencil pull-right"}))))

(q/defcomponent File [file]
  (d/li {:className "list-group-item"}
    (d/a {:href ""} (:id file))))

(q/defcomponent Navigation [state channels]
  (d/div {:className "navigation"}
    (d/ul {:className "list-group"}
      (d/li {:className "list-group-item"} "Follow"
        (d/input {:type "checkbox" :className "pull-right"}))
      (d/li {:className "list-group-item"} "Online"
        (d/span {:className "label label-primary pull-right"}
          (count (:members state))))
      (d/li {:className "list-group-item"}
        (d/a {:href ""} "Change Nickname")))
    (d/h3 {} "Who's Online")
    (apply d/ul {:className "list-group"}
      (map #(Member %) (vals (:members state))))
    (d/h3 {} "Files")
    (apply d/ul {:className "list-group"}
      (map #(File %) (vals (:files state))))))

(defn- get-cursor-top [c]
  (str (-> (:y c) (- 1) (* 23)) "px"))

(defn- get-cursor-left [c]
  (str (- (:x c) 1) "ch"))

(q/defcomponent Cursor [c]
  (d/div {:className "cursor-container"}
    (d/div {:className "cursor"
            :style {:top (get-cursor-top c) :left (get-cursor-left c)}
            } (:member c))))

(q/defcomponent Pane [[file cursors]]
  (let [hidden? (-> cursors (count) (< 1))]
    (d/div {:className (class-name #{"pane" (when hidden? "hidden")})}
      (d/pre {:className "content"} (hl/hightlight file))
      (d/div {:className "filename"} (:id file))
      (apply d/div {:className "cursors"}
        (map #(Cursor %) cursors)))))

(defn- filter-cursors-by-file [cs f]
  (filter #(= (:file %) (:id f)) cs))

(q/defcomponent Panes [state channels]
  (let [cs (vals (:cursors state))
        fs (vals (:files state))]
    (apply d/div {:className "editor"}
      (map #(Pane [% (filter-cursors-by-file cs %)]) fs))))

(q/defcomponent StatusOpened []
  (d/div {:className "center"}
    (d/img {:src "images/loading-bubbles.svg" :height 64 :width 64})
    (d/h3 {} "Waiting for code")))

(q/defcomponent StatusClosed []
  (d/div {:className "center"}
    (d/h3 {} "Connection lost")
    (d/button {:className "btn btn-primary"
               :onClick #(.reload js/location)
               } "refresh")))

(q/defcomponent Editor [state channels]
  (d/div {:className "layout"}
    (Navigation state channels)
    (d/div {:className "editor-wrapper"}
      (when (and (= (:status state) "opened") (nil? (:files state)))
        (StatusOpened))
      (when (= (:status state) "closed")
        (StatusClosed))
      (Panes state channels))))

(defn request-render [app]
  (q/render (Editor @(:state app) (:channels app)) (:dom-element app)))
