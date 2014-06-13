(ns collab.ctrl
  (:require [collab.util :as util]))

;;;; Member

(defn- make-member [id]
  {id {:name id :me false :coding false}})

(defn join [state [id sender]]
  (update-in state [:members]
    (fn [ms] (merge ms (make-member id)))))

(defn leave [state [id sender]]
  (update-in state [:members]
    (fn [ms] (dissoc ms id))))

(defn members [state [data sender]]
  (update-in state [:members]
    (fn [ms] (merge ms (into {}
      (map (fn [d] {(:id d) d}) data))))))

(defn- change-nick [state data]
  (assoc-in state [:members (:id data) :name] (:name data)))

(defn- change-nick-in-cursor [state data]
  (assoc-in state [:cursors (:id data) :member] (:name data)))

(defn update-member [state [data sender]]
  (-> state
    (change-nick data)
    (change-nick-in-cursor data)))

;;;; Code

(defn make-file [f]
  {(:file f) {:id (:file f) :buffer (:buffer f) :lang (:lang f)}})

(defn code [state [data sender]]
  (update-in state [:files] (fn [fs]
    (merge fs (make-file data)))))

;;;; Cursor

(defn make-cursor [id c member]
  {id {:x (:x c)
       :y (:y c)
       :file (:file c)
       :member (:name member)
       :follow true}})

(defn cursor [state [data sender]]
  (update-in state [:cursors] (fn [cs]
    (let [members (:members state)
          member (get members sender)]
      (merge cs (make-cursor sender data member))))))

;;;; Status

(defn open [state]
  (assoc-in state [:status] "opened"))

(defn close [state]
  (assoc-in state [:status] "closed"))
