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

(def gen-artefact
  (gen/fmap (fn [[name namespace [ma mi pa]]]
              (let [vsn (format "%d.%d.%d" ma mi pa)]
                {:namespace namespace
                 :name name
                 :version vsn
                 :payload ""
                 :dependencies []}))
    (gen/tuple
      (gen/not-empty gen/string-alpha-numeric)
      (gen/not-empty gen/string-alpha-numeric)
      (gen/tuple gen/pos-int gen/pos-int gen/pos-int))))

(deftest missing-deps-test
  (let [jsx {"namespace" "talentdeficit", "name" "jsx", "version" "1.4.5" "payload" ""}
        mouture {"namespace" "nox", "name" "mouture", "version" "0.0.1" "payload" ""}]
    (is
      (=
        (missing-deps [jsx mouture]
          '({:version "0.0.1", :name "mouture", :namespace "nox"}))
        (list {"namespace" "talentdeficit", "name" "jsx", "version" "1.4.5"})))
    (is
      (= (missing-deps [jsx] [])
        (list {"namespace" "talentdeficit", "name" "jsx", "version" "1.4.5"})))))

(defspec simple-artefact 100
  (prop/for-all [artefact gen-artefact]
    (store-artefact artefact)))

(defspec dependency-on-same-artefact 3
  (prop/for-all [artefact gen-artefact]
    (is
      (=
        {:error {:missing_deps (list artefact)} :type :missing_deps}
        (store-artefact (assoc artefact :dependencies [artefact]))))))

(defspec missing-dependencies
  (prop/for-all [artefact gen-artefact
                 name (gen/not-empty gen/string-alpha-numeric)
                 namespace (gen/not-empty gen/string-alpha-numeric)
                 [ma mi pa] (gen/tuple gen/pos-int gen/pos-int gen/pos-int)]
    (let [vsn (format "%d.%d.%d" ma mi pa)
          dep {:namespace namespace
               :name      name
               :vsn       vsn}]
      (is
        (=
          {:error {:missing_deps [dep]} :type :missing_deps}
          (store-artefact (assoc artefact :dependencies [{:namespace namespace
                                                          :name      name
                                                          :vsn       vsn}])))))))
