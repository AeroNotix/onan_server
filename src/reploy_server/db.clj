(ns reploy-server.db
  (:use [korma db core]))

(defdb main-database (postgres
                      {:db   "reploy"
                       :user "postgres"}))
