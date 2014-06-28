(ns collab.pane
  (:require [quiescent :as q :include-macros true]
            [quiescent.dom :as d]
            [collab.highlight :as hl]
            [collab.cursor :refer (Cursor)]))

(defn- class-name [classes]
  (apply str (interpose " " (map identity classes))))

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
