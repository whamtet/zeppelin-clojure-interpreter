(ns my-zeppelin-clj.core)

(import [org.apache.zeppelin.interpreter Interpreter InterpreterResult InterpreterResult$Code InterpreterContext Interpreter$FormType])

(Interpreter/register "clojure" "spark" "hk.molloy.ClojureInterpreter")

(gen-class
  :name hk.molloy.ClojureInterpreter
  :extends org.apache.zeppelin.interpreter.Interpreter)

(def interpreter nil)

(defmacro scala [s]
  `(some-> interpreter .interpreter (.valueOfTerm ~(str s)) .get))

(defmacro bind [k v]
  `(-> my-zeppelin-clj.core/interpreter .interpreter (.bind ~(str k) "Object" ~v scala.collection.immutable.Nil$/MODULE$)))

(defn -open [this]
  (println "opening"))

(defn -close [this]
  (println "closing"))

(defn -interpret [this st context]
  (when (and (nil? interpreter) (try (eval 'org.apache.zeppelin.spark.SparkInterpreter) (catch Exception e)))
    (def interpreter
      ((eval
         '(fn [this]
            (let [
                   interpreter-field
                   (doto (.getDeclaredField org.apache.zeppelin.spark.SparkInterpreter "interpreter")
                     (.setAccessible true))
                   ]
              (.get interpreter-field
                    (some #(if (instance? org.apache.zeppelin.spark.SparkInterpreter %) %)
                          (map #(-> % .getInnerInterpreter .getInnerInterpreter)
                               (.getInterpreterGroup this))))))) this)))

  (InterpreterResult. InterpreterResult$Code/SUCCESS (pr-str (load-string st))))

(defn -cancel [this context]
  (println "cancelling"))

(defn -getFormType [this]
  Interpreter$FormType/SIMPLE)

(defn -getProgress [this context]
  0)

(defn -completion [this buf cursor]
  '(""))
