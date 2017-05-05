(defproject calculadora "1.0.0"
  :description "calculadora"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]]  
  :test-path "spec"
  :profiles {:dev {:plugins [[lein-kibit "0.1.2"]
                             [speclj "2.5.0"]]
                   :dependencies [[speclj "2.5.0"]]}}
  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-localrepo "0.4.0"]]   
  :source-paths ["src"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :compiler {:output-to "browser-based/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}    
  ;; :hooks [leiningen.cljsbuild]

)
