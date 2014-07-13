(ns onan-server.db
  (:use [korma db core]))

(defdb main-database (postgres
                      {:db   "onan"
                       :user "postgres"}))
