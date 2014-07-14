(ns onan-server.artefacts.http
  (:require [clojure.string :as string]
            [onan-server.artefacts.persistence :as p]
            [onan-server.users :as users]
            [onan-server.utils :refer [md5sum]]))


(defn handle-create-artefact [namespace name version payload dependencies]
  (let [stored? (p/store-artefact namespace name version payload dependencies)]
    (if-not (:error stored?)
      {:status 200 :headers {"location" (format "/deps/%s/%s/%s"
                                                namespace name version)}}
      (condp = (:type stored?)
        :missing_deps
          {:status 404 :body stored?}
        :else
          {:status 500 :body stored?}))))

(defn par [what]
  (println what)
  what)

(defn create-artefact [request]
  (let [{:strs  [namespace name version payload checksum dependencies]} (:body request)]
    (if (not= (md5sum payload) (string/lower-case checksum))
      {:status 422 :body {:error "Checksums did not match."}}
      (if-let [stored (p/retrieve-stored namespace name version)]
        {:status 409}
        (handle-create-artefact namespace name version payload dependencies)))))

(defn get-artefact [request]
  (let [{:keys  [namespace name vsn]} (:params request)]
    (if-let [stored (p/retrieve-stored namespace name vsn)]
      {:status 200
       :body {:dependencies
              (map (comp
                     #(update-in % [:uuid] (fn [x] (.toString x)))
                     #(update-in % [:payload] (fn [x] (String. x)))) stored)}}
      {:status 404 :body {:error "No artefact was found."}})))
