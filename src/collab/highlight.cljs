(ns collab.highlight
  (:require [clojure.string :as s]
            [quiescent.dom :as d]))

(defn- make-line-numbers [code]
  (apply d/span {:className "line-numbers-rows"}
    (map #(d/span {}) (s/split code #"\n"))))

(defn- highlight-code [file]
  (if (.getLanguage js/hljs (:lang file))
    (.-value (.highlight js/hljs (:lang file) (:buffer file)))
    (.-value (.highlightAuto js/hljs (:buffer file)))))

(defn highlight [file]
  (let [code (highlight-code file)]
    (d/code {:className "hljs"}
      (d/span {:dangerouslySetInnerHTML {:__html code}})
      (make-line-numbers (:buffer file)))))
