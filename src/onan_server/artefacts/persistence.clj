(ns onan-server.artefacts.persistence
  (:use [korma db core])
  (:require [onan-server.users :as users]
            [onan-server.db :refer [main-database]]
            [onan-server.utils :refer [key-fn]]))

(defentity deployment
  (has-one users/users)
  (database main-database))

(declare dependencies retrieve-stored)

(defentity dependencies
  (has-many dependencies))

(defn retrieve-dependencies [uuid]
  (let [deps (select dependencies
                     (where {:project uuid}))]
    (flatten (pmap #(retrieve-stored (:dependency %)) deps))))

(defn retrieve-stored
  ([uuid]
     (let [deployment (first
                       (select deployment
                               (where {:uuid uuid})
                               (limit 1)))
           deps (retrieve-dependencies uuid)]
       (when deployment
         (cons deployment deps))))
  ([namespace name version]
     (let [deployment (first
                       (select deployment
                         (where {:namespace namespace
                                 :name      name
                                 :version   version})
                         (limit 1)))
           uuid (:uuid deployment)
           deps (retrieve-dependencies uuid)]
       (when deployment
         (cons deployment deps)))))

(defn create-dependency [project dependency]
  (insert dependencies
          (values {:project    project
                   :dependency dependency})))

(defn missing-deps [required actual]
  (let [actual (into #{} (map (comp str :name) actual))]
    (keep
      #(if (not (actual (% "name")))
         (dissoc % "payload"))
      required)))

(defn store-artefact
  ([{:keys [namespace name version payload dependencies]}]
     (apply store-artefact [namespace name version payload dependencies]))
  ([namespace name version payload dependencies]
     (transaction
       (let [uuid (:uuid (insert deployment
                           (values {:namespace namespace
                                    :name      name
                                    :version   version
                                    :payload   (.getBytes payload)})))]
         (let [deps (apply concat
                      (map (fn [{:strs [namespace name version]}]
                             (retrieve-stored namespace name version)) dependencies))]
           (if (< (count deps) (count dependencies))
             (do (rollback)
                 {:error {:missing_deps (missing-deps dependencies deps)} :type :missing_deps})
             (do
               (doall (map (comp (partial create-dependency uuid) :uuid) deps))
               {:status :success})))))))
