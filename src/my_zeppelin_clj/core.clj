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

(import [org.apache.spark.sql.types StructType StructField LongType DoubleType StringType Metadata])
(import scala.collection.immutable.Map$)
(import [org.apache.spark.sql Row$])

(defn struct-type [x]
(cond
(integer? x) (LongType.)
(number? x) (DoubleType.)
:default (StringType.)))

(defn struct-field [name value]
(StructField. name (struct-type value) true (Metadata. (.empty Map$/MODULE$))))

(defn schema [names values]
(StructType. (into-array (map struct-field names values))))

(defn row [x]
(->> x scala.collection.JavaConversions/asScalaBuffer .toList (.fromSeq Row$/MODULE$)))

(defn df [names rows]
(.createDataFrame (scala sqlContext) (map row rows) (schema names (first rows))))

(defn -interpret [this st context]
  (when (and (nil? interpreter) (try (eval 'org.apache.zeppelin.spark.SparkInterpreter) (catch Exception e)))
    (def my-this this)
    (def interpreter
      ((eval
         '(fn [this]
            (try
              (let [
                     interpreter-field
                     (doto (.getDeclaredField org.apache.zeppelin.spark.SparkInterpreter "interpreter")
                       (.setAccessible true))
                     interpreter-group (.getInterpreterGroup this)
                     interpreter-group (try (get interpreter-group "shared_session" interpreter-group) (catch Exception e interpreter-group))
                     inner-interpreter (fn [x] (if (instance? org.apache.zeppelin.interpreter.WrappedInterpreter x) (recur (.getInnerInterpreter x)) x))
                     ]
                (.get interpreter-field
                      (some #(if (instance? org.apache.zeppelin.spark.SparkInterpreter %) %)
                            (map inner-interpreter interpreter-group))))
              (catch Exception e e))
            )) this))
    (def zeppelin-context (scala z))
    (def show-df2
      (eval
        '(fn [sc ic df limit]
           (org.apache.zeppelin.spark.ZeppelinContext/showDF sc ic df limit))))
    )
  (when zeppelin-context
    (.setInterpreterContext zeppelin-context context))
  (let [
         result (load-string (format "(ns user) (use '[my-zeppelin-clj.core :only [scala bind show-df df]]) %s" st))
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
