(defproject patients "0.1.0-SNAPSHOT"
  :description "Health Samurai Test Task by Sizov Sergey"

  :dependencies [;; Standart libs
                 [org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]

                 ;; Database

                 ;; HTTP-server 
                 [ring "1.9.6"]
                 [compojure "1.7.0"]

                 ;; ----- ClojureScript --------
                 [thheller/shadow-cljs "2.20.20"]
                 [reagent "1.1.1"]
                 [re-frame "1.3.0"]

                 ;; Re-Frame debugger
                 [re-frisk "1.6.0"]
                 
                 ;; Fix dependencies
                 [com.fzakaria/slf4j-timbre "0.2"]]

  :plugins [[thheller/shadow-cljs "2.20.20"]]

  :source-paths ["src/clj" "src/cljs"]

  :resource-paths ["resources"]

  :main patients.core/-main

  :target-path "target/%s"

  :profiles {:shadow-cljs
             {:dependencies [[shadow-cljs/devtools "2.20.20"]]
              :plugins [[lein-shadow "0.4.1"]]
              :source-paths ["src/cjls"]
              :prep-tasks ["compile"]
              :builds {}
              :shadow-cljs {:config-files ["shadow-cljs.edn"]}}})