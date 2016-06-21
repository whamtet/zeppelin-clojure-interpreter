(ns my-zeppelin-clj.core)

(import [org.apache.zeppelin.interpreter Interpreter InterpreterResult InterpreterResult$Code InterpreterContext Interpreter$FormType])

(Interpreter/register "clojure" "spark" "hk.molloy.ClojureInterpreter")

(gen-class
  :name hk.molloy.ClojureInterpreter
  :extends org.apache.zeppelin.interpreter.Interpreter)

(def interpreter nil)
(def zeppelin-context nil)
(def show-df2 nil)

(defmacro scala [s]
  `(some-> interpreter .interpreter (.valueOfTerm ~(str s)) .get))

(defmacro bind [k type v]
  `(-> my-zeppelin-clj.core/interpreter .interpreter (.bind ~(str k) ~type ~v scala.collection.immutable.Nil$/MODULE$)))

(defn show-df [df limit]
  (show-df2 (scala sc) (.getInterpreterContext zeppelin-context) df limit))

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
                               (.getInterpreterGroup this))))))) this))
    (def zeppelin-context (scala z))
    (def show-df2
      (eval
        '(fn [sc ic df limit]
           (org.apache.zeppelin.spark.ZeppelinContext/showDF sc ic df limit))))
    )
  (when zeppelin-context
    (.setInterpreterContext zeppelin-context context))
  (let [
         result (load-string st)
         result (if (and (string? result) (.startsWith result "%table"))
                  result
                  (pr-str result))
         ]
    (InterpreterResult. InterpreterResult$Code/SUCCESS result)))

(defn -cancel [this context]
  (println "cancelling"))

(defn -getFormType [this]
  Interpreter$FormType/SIMPLE)

(defn -getProgress [this context]
  0)

(defn -completion [this buf cursor]
  '(""))
