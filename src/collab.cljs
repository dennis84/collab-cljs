(ns collab
  (:require [collab.render :as render]))

(defn ^:export main [elem]
  (render/render elem))
