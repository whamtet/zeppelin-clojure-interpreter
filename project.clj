(defproject zeppelin-clojure "0.5.6-incubating"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.zeppelin/zeppelin-interpreter "0.6.0"]
                 ]
  :aot [my-zeppelin-clj.core]
  )
