(ns reploy-server.db
  (:use [korma db core])
  (:import [org.mindrot.jbcrypt BCrypt]))

(defdb db (postgres
           {:db   "reploy"
            :user "postgres"}))

(defentity users)

(defentity deployment
  (has-one users))

(declare dependencies)

(defentity dependencies
  (has-many dependencies))

(defn create-user [username password email]
  (let [salt (BCrypt/gensalt)
        uuid (java.util.UUID/randomUUID)]
    (insert users
            (values {:uuid uuid
                     :username username
                     :email email
                     :password (BCrypt/hashpw password salt)
                     :salt salt}))))

(defn delete-user [username]
  (delete users (where {:username username})))
