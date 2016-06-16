(ns my-zeppelin-clj.core)

(import [org.apache.zeppelin.interpreter Interpreter InterpreterResult InterpreterResult$Code InterpreterContext Interpreter$FormType])

(Interpreter/register "clojure" "spark" "hk.molloy.ClojureInterpreter")

(gen-class
  :name hk.molloy.ClojureInterpreter
  :extends org.apache.zeppelin.interpreter.Interpreter)

(def spark-context nil)

(defn -open [this]
  (println "opening"))

(defn -close [this]
  (println "closing"))

(defn -interpret [this st context]
  (if (nil? spark-context)
    (def spark-context context))
  (InterpreterResult. InterpreterResult$Code/SUCCESS (str (load-string st))))

(defn -cancel [this context]
  (println "cancelling"))

(defn -getFormType [this]
  Interpreter$FormType/SIMPLE)

(defn -getProgress [this context]
  0)

(defn -completion [this buf cursor]
  '(""))
