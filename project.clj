(defproject mockster "0.1.0"
  :description "A simple, configurable data backend mock."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring/ring-core "1.1.3"]
                 [ring/ring-jetty-adapter "1.1.3"]
                 [org.clojure/data.json "0.1.3"]]
  :plugins [[lein-ring "0.7.4"]]
  :profiles {:dev {:dependencies [[ring-mock "0.1.3"]]}}
  :ring {:handler mockster.core/app})
