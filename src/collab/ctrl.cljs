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

;;;; Cursor

(defn- make-cursor [id c member]
  {id {:x (:x c)
       :y (:y c)
       :file (:file c)
       :member (:name member)}})

(defn- update-cursor [state data sender]
  (update-in state [:cursors] (fn [cs]
    (let [members (:members state)
          member (get members sender)]
      (merge cs (make-cursor sender data member))))))

(defn- update-values [m f & args]
  (reduce (fn [r [k v]] (assoc r k (apply f v args))) {} m))

(defn- follow-file [state id force?]
  (if (or (true? (:follow state)) (true? force?))
    (-> state
      (update-in [:files] update-values assoc :active false)
      (assoc-in [:files id :active] true))
    state))

(defn cursor [state [data sender]]
  (-> state
    (update-cursor data sender)
    (follow-file (:file data))))

;;;; Code

(defn- make-file [f]
  {(:file f) {:id (:file f)
              :buffer (:buffer f)
              :lang (:lang f)
              :active false}})

(defn code [state [data sender]]
  (-> state
    (update-in [:files] (fn [fs] (merge fs (make-file data))))
    (follow-file (:file data))))

;;;; Status

(defn open [state]
  (assoc-in state [:status] "opened"))

(defn close [state]
  (assoc-in state [:status] "closed"))

;;;; Misc

(defn show-file [state file]
  (follow-file state (:id file) true))

(defn toggle-follow [state checked]
  (assoc-in state [:follow] checked))
