(ns reploy-server.users
  (:use [korma db core])
  (:import [org.mindrot.jbcrypt BCrypt]))


(defentity users)

(defn create-user [username password email]
  (let [salt (BCrypt/gensalt)]
    (insert users
            (values {:username username
                     :email email
                     :password (BCrypt/hashpw password salt)
                     :salt salt}))))

(defn delete-user [username]
  (delete users (where {:username username})))

(defn get-user [username]
  (seq (select users (where {:username username}))))
