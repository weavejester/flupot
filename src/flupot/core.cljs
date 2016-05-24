(ns flupot.core)

(defn push-child! [args child]
  (if (seq? child)
    (doseq [c child]
      (push-child! args c))
    (.push args child)))

(defn attrs->react [attrs]
  (reduce-kv
   (fn [o k v] (doto o (aset (name k) (clj->js v))))
   (js-obj)
   attrs))
