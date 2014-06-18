(ns collab
  (:require [cljs.core.async :as a]
            [clojure.string :as s]
            [collab.data :as data]
            [collab.ctrl :as ctrl]
            [collab.conn :as conn]
            [collab.home :as home]
            [collab.editor :as editor])
  (:require-macros [cljs.core.async.macros :as am]))

(defn load-app [elem room]
  {:dom-element elem
   :room room
   :state (atom (data/make))
   :connection (conn/make room)
   :render-pending? (atom false)
   :channels {:join (a/chan)
              :leave (a/chan)
              :members (a/chan)
              :update-member (a/chan)
              :code (a/chan)
              :cursor (a/chan)
              :close (a/chan)
              :open (a/chan)
              :show-file (a/chan)}
   :consumers {:join ctrl/join
               :leave ctrl/leave
               :members ctrl/members
               :update-member ctrl/update-member
               :code ctrl/code
               :cursor ctrl/cursor
               :close ctrl/close
               :open ctrl/open
               :show-file ctrl/show-file}})

(defn init-updates [app]
  (doseq [[ch update-fn] (:consumers app)]
    (am/go (while true
      (let [val (a/<! (get (:channels app) ch))
            new-state (swap! (:state app) update-fn val)]
              (editor/request-render app))))))

(defn ^:export main [elem]
  (let [room (subs (.-hash js/location) 1)]
    (if (s/blank? room)
      (home/request-render elem)
      (let [app (load-app elem room)]
        (init-updates app)
        (conn/init-websocket-receiver app)
        (editor/request-render app)))))
