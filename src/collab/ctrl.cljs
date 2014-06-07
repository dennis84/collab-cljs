(ns collab.ctrl)

(defn join [state id]
  (update-in state [:members] concat [id]))
