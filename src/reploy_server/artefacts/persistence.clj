(ns reploy-server.artefacts.persistence
  (:use [korma db core])
  (:require [reploy-server.users :as users]
            [reploy-server.db :refer [main-database]]))

(defentity deployment
  (has-one users/users)
  (database main-database))

(declare dependencies retrieve-stored)

(defentity dependencies
  (has-many dependencies))

(defn retrieve-dependencies [uuid]
  (let [deps (select dependencies
                     (where {:project uuid}))]
    (pmap #(retrieve-stored (:dependency %)) deps)))

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

(defn store-artefact [author namespace name version payload dependencies]
  (let [uuid (:uuid author)]
    (transaction
     (insert deployment
             (values {:author uuid
                      :namespace namespace
                      :name      name
                      :version   version
                      :payload   (.getBytes payload)}))
     (let [deps (map (fn [{:strs [namespace name version]}]
                        (:uuid (retrieve-stored namespace name version))) dependencies)]
       (if (some nil? deps)
         (do
           (rollback)
           {:error "Dependencies not found." :type :missing_deps})
         (doall
          (map (partial create-dependency uuid)
                (map (fn [{:strs [namespace name version]}]
                        (:uuid (retrieve-stored namespace name version))) dependencies))))))))
