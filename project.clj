(defproject reploy_server "0.0.1"
  :description "Server side handling of: https://github.com/AeroNotix/reploy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main ^:skip-aot reploy-server.core
  :dependencies [[compojure "1.1.6"]
                 [org.clojure/clojure "1.5.1"]
                 [ring "1.2.1"]
                 [ring/ring-json "0.2.0"]])
