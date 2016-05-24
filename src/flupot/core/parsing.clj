(ns flupot.core.parsing)

(defn quoted? [x]
  (and (list? x) (= 'quote (first x))))

(defn literal? [x]
  (or (quoted? x) (not (or (symbol? x) (list? x)))))
