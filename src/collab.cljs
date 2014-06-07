(ns collab
  (:require [cljs.core.async :as a]
            [collab.data :as data]
            [collab.ctrl :as ctrl]
            [collab.render :as render])
  (:require-macros [cljs.core.async.macros :as am]))

(defn load-app [elem]
  {:dom-element elem
   :state (atom (data/make))
   :render-pending? (atom false)
   :channels {:join (a/chan)}
   :consumers {:join ctrl/join}})

(defn init-updates [app]
  (doseq [[ch update-fn] (:consumers app)]
    (am/go (while true
      (let [val (a/<! (get (:channels app) ch))
            new-state (swap! (:state app) update-fn val)]
              (render/request-render app))))))

(defn ^:export main [elem]
  (let [app (load-app elem)]
    (init-updates app)
    (am/go (a/>! (:join (:channels app)) "foo"))
    (am/go (a/>! (:join (:channels app)) "bar"))
    (render/request-render app)))
