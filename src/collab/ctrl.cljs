(ns collab.ctrl)

(defn join [state id]
  (update-in state [:members] concat [id]))

(defn leave [state id]
  (update-in state [:members]
    (fn [members] (remove #(= id %) members))))
