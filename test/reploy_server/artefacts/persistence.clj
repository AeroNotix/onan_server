(ns reploy-server.artefacts.persistence
  (:require [onan-server.artefacts.persistence :refer :all]
            [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :as ct :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))


(defspec simple-artefact 100
  (prop/for-all [name (gen/not-empty gen/string-alpha-numeric)
                 namespace (gen/not-empty gen/string-alpha-numeric)
                 [ma mi pa] (gen/tuple gen/pos-int gen/pos-int gen/pos-int)]
    (let [vsn (format "%d.%d.%d" ma mi pa)]
      (store-artefact namespace name vsn "" []))))

(deftest conflicting-artefact
  (is false))

(deftest recursive-dependency
  (is false))
