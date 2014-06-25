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

(q/defcomponent File [file channels]
  (d/li {:className "list-group-item"}
    (d/a {:href "#" :onClick #(am/go (a/>! (:show-file channels) file))
          } (:id file))))

(q/defcomponent ChangeNick [conn]
  (q/on-mount
    (d/div {:id "change-nick" :className "modal fade bs-modal-sm"}
      (d/div {:className "modal-dialog modal-sm"}
        (d/div {:className "modal-content"}
          (d/form {:onSubmit (fn [e]
                               (.preventDefault e)
                               (let [input (.getElementById js/document "nickname")
                                     value (.-value input)]
                                  (when (not (s/blank? value))
                                    (.send conn (util/clj->json {:t "update-nick" :d value})))
                                  (.modal (js/$ "#change-nick") "hide")))}
            (d/div {:className "modal-body"}
              (d/label {} "Nickname")
              (d/input {:id "nickname" :className "form-control input-lg"}))
            (d/div {:className "modal-footer"}
              (d/button {:className "btn btn-default" :data-dismiss "modal"} "Close")
              (d/button {:className "btn btn-primary" :type "submit"} "Apply"))
            ))))
    (fn [node] (let [$elem (js/$ node)]
      (.on $elem "shown.bs.modal" #(.focus (.querySelector node "input")))))))

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

(defn- scroll-to [node pos]
  (let [$elem (js/$ node)
        $window (js/$ js/window)
        offset (* (.height $window) 0.3)
        top (-> pos (- 1) (* 23) (- offset))]
    (-> $elem (.stop) (.animate (clj->js {:scrollTop top})))))

(q/defcomponent Pane [[file cursors]]
  (let [hidden? (false? (:active file))]
    (q/on-render
      (d/div {:className (class-name #{"pane" (when hidden? "hidden")})}
        (d/pre {:className "content"} (hl/highlight file))
        (d/div {:className "filename"} (:id file))
        (apply d/div {:className "cursors"}
          (map #(Cursor %) cursors)))
      #(scroll-to % (:y (first cursors))))))

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
