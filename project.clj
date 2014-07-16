(defproject onan_server "0.0.1"
  :description "Server side handling of: https://github.com/AeroNotix/onan"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:skip-aot onan-server.core
  :dependencies [[cloverage "1.0.4"]
                 [compojure "1.1.6"]
                 [korma "0.3.0-RC5"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/test.check "0.5.8"]
                 [org.mindrot/jbcrypt "0.3m"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [ring "1.2.1"]
                 [ring/ring-json "0.2.0"]])
