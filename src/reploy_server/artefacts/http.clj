(ns reploy-server.artefacts.http)


(defn create-artefact [request]
  (let [[namespace name version]
        (map (:body request) ["namespace" "name" "version"])]
    {:status 200 :headers {"location" (format "/deps/%s/%s/%s"
                                              namespace name version)}}))

(defn get-artefact [request]
  {:status 200
   :body [
          {
           :namespace "rpt"
           :name "erlcql"
           :version "0.0.1"
           :checksum "bf1b57c493c54ad335ec051294b5cb4d"
           :payload "base64 encoded payload of ez package"
           },
          {
           :namespace "puzza007"
           :name "a_dependency"
           :version "0.4.0"
           :checksum "7a9c1d2a0f3a8db565b7e382ef5b1151"
           :payload "base64 encoded payload of ez package"
           }
          ]})
