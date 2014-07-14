(ns reploy-server.artefacts.persistence
  (:use [korma db core])
  (:require [onan-server.artefacts.persistence :refer :all]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as ct :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))



(defn truncate-tables [f]
  (delete deployment)
  (delete dependencies)
  (f))

(use-fixtures :each truncate-tables)

(defspec simple-artefact 100
  (prop/for-all [name (gen/not-empty gen/string-alpha-numeric)
                 namespace (gen/not-empty gen/string-alpha-numeric)
                 [ma mi pa] (gen/tuple gen/pos-int gen/pos-int gen/pos-int)]
    (let [vsn (format "%d.%d.%d" ma mi pa)]
      (store-artefact namespace name vsn "" []))))

(defspec conflicting-artefact
  (prop/for-all [name (gen/not-empty gen/string-alpha-numeric)
                 namespace (gen/not-empty gen/string-alpha-numeric)
                 [ma mi pa] (gen/tuple gen/pos-int gen/pos-int gen/pos-int)]
    (let [vsn (format "%d.%d.%d" ma mi pa)]
      (is
        (=
          {:error "Dependencies not found." :type :missing_deps}
          (store-artefact namespace name vsn "" [{:namespace namespace
                                                  :name      name
                                                  :vsn       vsn}]))))))

(deftest recursive-dependency
  (is false))
