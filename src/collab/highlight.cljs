(ns collab.highlight
  (:require [clojure.string :as s]
            [quiescent.dom :as d]))

(defn make-line-numbers [code]
  (apply d/span {:className "line-numbers-rows"}
    (map #(d/span {}) (s/split code #"\n"))))

(defn hightlight [file]
  (d/code {:className "hljs"} (:buffer file)
    (make-line-numbers (:buffer file))))
