# my-zeppelin-clj

Simple Zeppelin Interpreter for Clojure.  I built this because the other solution didn't build well.

## Installation

Clone zeppelin source and install dependencies for this project.

```bash

lein jar
cp target/zeppelin-clojure-0.5.6-incubating.jar $ZEPPELIN_HOME/interpreter/spark
cp ~/Downloads/clojure-1.8.0.jar $ZEPPELIN_HOME/interpreter/spark

```

Add hk.molloy.ClojureInterpreter to the zeppelin.interpreters property in ```$ZEPPELIN_HOME/conf/zeppelin-site.xml```

Depending on your configuration, you may have to add clojure-1.8.0.jar to your classpath as well.

Start zeppelin and click interpreter.  You should see something like

spark %spark (default) , %pyspark , %sql , %dep , %clojure

Yeehah!

## Interop

The clojure interpreter can interop with the scala interpreter as follows

```clojure
(scala val_defined_in_scala) ;;gets the val
(bind "val_name" "com.mycompany.MyClass" val) ;;binds val to val_name in the scala interpreter
```

To show a dataframe (equivalent to z.show in scala)

```clojure
(show-df my-df num-of-rows)
```

## Issues

At present the Spark Interpreter must be invoked at least once before the clojure interpreter.  It is not known why.

## License

Copyright © 2016 Matthew Molloy

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
