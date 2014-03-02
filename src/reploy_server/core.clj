(ns reploy-server.core
  (:use [compojure.core :only [defroutes routes GET POST]]
        [ring.middleware.json]
        [ring.middleware.stacktrace]
        [ring.util.response])
  (:require [ring.adapter.jetty :as jetty]
            [reploy-server.artefacts.http :refer [create-artefact
                                                  get-artefact]]))


(defroutes main-application
  (POST "/artefact" request
        (create-artefact request))
  (GET "/deps/:namespace/:name/:vsn" request
       (get-artefact request)))

(def wrap-middleware
  (-> main-application
      wrap-json-body
      wrap-json-response
      wrap-stacktrace-log))

(defn- start-server []
  ;; TODO: Put the port into a configuration file.
  (let [options {:port 45045}]
    (jetty/run-jetty wrap-middleware options)))

(defn -main [& args]
  (start-server))
