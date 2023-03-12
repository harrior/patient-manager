(defproject patients "0.1.0"
  :description "Health Samurai Test Task by Sizov Sergey"

  :dependencies [;; Common libs
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.logging "1.2.4"]
                 [metosin/jsonista "0.3.7"]

                 ;; Database
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [com.github.seancorfield/honeysql "2.4.980"]
                 [org.postgresql/postgresql "42.1.4"]
                 [migratus "1.4.9"]

                 ;; HTTP-server
                 [ring "1.9.6"]
                 [compojure "1.7.0"]
                 [fogus/ring-edn "0.3.0"]
                 [ring/ring-mock "0.4.0"]

                 ;; ----- ClojureScript --------
                 [reagent "1.1.1"]
                 [re-frame "1.3.0"]
                 [thheller/shadow-cljs "2.20.20"]

                 ;; Styles
                 [stylefy "3.2.0"]
                 [stylefy/reagent "3.0.0"]

                 ;; HTTP-client
                 [day8.re-frame/http-fx "0.2.4"]

                 ;; Ajax client
                 [cljs-ajax "0.8.4"]

                 ;; Re-Frame debugger
                 [re-frisk "1.6.0"]

                 ;; Fix dependencies
                 [com.fzakaria/slf4j-timbre "0.3.21"]]

  :plugins [[thheller/shadow-cljs "2.20.20"]]

  :source-paths ["src/clj" "src/cljs"]

  :test-paths ["test/clj" "test/cljs"]

  :resource-paths ["resources"]

  :main patients.core/-main

  :manifest {"Main-Class" "patients.core"}

  :target-path "target/%s"

  :profiles {:shadow-cljs
             {:dependencies [[shadow-cljs/devtools "2.20.20"]]
              :plugins [[lein-shadow "0.4.1"]]
              :source-paths ["src/cjls"]
              :test-paths ["test/cljs"]
              :prep-tasks ["compile"]
              :builds {}
              :shadow-cljs {:config-files ["shadow-cljs.edn"]}}

             :uberjar
             {:aot :all
              :prep-tasks ["compile"
                           ["run" "-m" "shadow.cljs.devtools.cli" "release" "app" "--source-maps"]]}})