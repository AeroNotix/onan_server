(ns reploy-server.db
  (:use [korma db core])
  (:import [org.mindrot.jbcrypt BCrypt]))

(defdb main-database (postgres
                      {:db   "reploy"
                       :user "postgres"}))
