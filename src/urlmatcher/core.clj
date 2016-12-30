(ns urlmatcher.core
  (:require [clojure.string :as str])
  )

(defprotocol Recognizable
  (recognize [this url]))

(defn url-match [url]
  (let [[_ schema host path params] (re-matches #"(https?://)([A-Za-z0-9\.\-]+)(\/[\+~%\/\.\w\-_]*)?\??([\-\+=&;%@\.\w_]*)?" url)]
    [schema
     host
     (if path (vec (remove empty? (str/split path #"/"))) [])
     (apply hash-map (remove empty?(str/split params #"[&=]")))]))

(defrecord Pattern [host path queryparam]
  Recognizable
  (recognize
    [this url]
    (let [[schema host path params] (url-match url)
          result (atom [])]
      (when (= host (.-host this))
        (let [pp (reduce (fn [a [path-seg pattern-seg]]
                          (if (= \? (first pattern-seg))
                            (update a :params conj [(keyword (apply str (rest pattern-seg))) path-seg])
                            (update a :valid (fn [old new] (if (and (nil? old) (true? new)) new)) (= path-seg pattern-seg))))
                        {:valid nil :params []}
                        (map vector path (.-path this)))
              qp (reduce (fn [a [k v]]
                           (if-let [x (get params k)]
                             (conj a [(keyword (apply str (rest v))) x])
                             ))
                         []
                         (.-queryparam this))
              ]
          (if (and (:valid pp) qp)
            (into [] (concat (:params pp) qp))))))))

(defn pattern
  "Creates a new pattern"
  [pattern]
  (let [host (second (re-find #"host\((.+?)\);" pattern))
        path (str/split (second (re-find #"path\((.+?)\);" pattern)) #"/")
        queryparam (into {} (map (fn [[x y]] (str/split y #"=")) (re-seq #"queryparam\((.+?)\);" pattern)))
        ]
    (->Pattern host path queryparam)))
