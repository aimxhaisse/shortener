(defproject shortener "0.1.0-SNAPSHOT"
  :description "Yet Another URL Shortener"
  :url "http://302.sbrk.org/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-devel "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.4"]]
  :dev-dependencies [[lein-run "0.8.8"]]
  :main shortener.core)
