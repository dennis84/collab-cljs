(ns collab
  (:require [cljs.core.async :as a]
            [collab.data :as data]
            [collab.ctrl :as ctrl]
            [collab.render :as render]))

(defn load-app [elem]
  {:dom-element elem
   :state (data/make)
   :channels {:join (a/chan)
              :leave (a/chan)}
   :consumers {:join ctrl/join}})

(defn ^:export main [elem]
  (let [app (load-app elem)]
    (render/render app)))
