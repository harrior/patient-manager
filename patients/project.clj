(defproject patients "0.1.0-SNAPSHOT"
  :description "Health Samurai Test Task by Sizov Sergey"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [;; Standart libs
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]

                 ;; Database

                 ;; HTTP-server 
                 [ring "1.9.6"]
                 [compojure "1.7.0"]
                 [ring/ring-jetty-adapter "1.9.6"]

                 ;; Frontend
                 [hiccup "1.0.5"]]

  :source-paths ["src/clj" "src/cljs"]

  :resource-paths ["resources"]

  :main ^:skip-aot patients.core

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :source-paths ["src/clj" "src/cljs"]
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
