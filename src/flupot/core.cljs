(ns flupot.core)

(defn push-child! [args child]
  (if (seq? child)
    (doseq [c child]
      (push-child! args c))
    (.push args child)))
