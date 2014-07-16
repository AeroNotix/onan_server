(ns onan-server.utils)

(defn md5sum [s]
  (apply str
         (map (partial format "%02x")
              (.digest (doto (java.security.MessageDigest/getInstance "MD5")
                         .reset
                         (.update (.getBytes s)))))))

(defn key-fn [f m]
  (into {} (for [[k v] m] [(f k) v])))
