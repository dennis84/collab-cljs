(ns collab
  (:require [cljs.core.async :as a]
            [clojure.string :as s]
            [collab.util :as util]
            [collab.data :as data]
            [collab.ctrl :as ctrl]
            [collab.conn :as conn]
            [collab.render :as render])
  (:require-macros [cljs.core.async.macros :as am]))

(defn load-app [elem]
  {:dom-element elem
   :state (atom (data/make))
   :render-pending? (atom false)
   :channels {:join (a/chan)
              :leave (a/chan)
              :members (a/chan)
              :update-member (a/chan)
              :code (a/chan)
              :cursor (a/chan)}
   :consumers {:join ctrl/join
               :leave ctrl/leave
               :members ctrl/members
               :update-member ctrl/update-member
               :code ctrl/code
               :cursor ctrl/cursor}})

(defn init-updates [app]
  (doseq [[ch update-fn] (:consumers app)]
    (am/go (while true
      (let [val (a/<! (get (:channels app) ch))
            new-state (swap! (:state app) update-fn val)]
              (render/request-render app))))))

(defn render-homepage [app]
  (render/homepage app))

(defn render-editor [app room]
  (init-updates app)
  (conn/init-websocket-receiver app room)
  (render/request-render app))

(defn ^:export main [elem]
  (let [app (load-app elem)
        room (subs (.-hash js/location) 1)]
    (if (s/blank? room)
      (render-homepage app)
      (render-editor app room))))
