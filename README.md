# my-zeppelin-clj

Simple Zeppelin Interpreter for Clojure.  I built this because the other solution didn't build well.

## Installation

First use ```mvn install:install-file``` to add zeppelin-interpreter-VERSION-XXX.jar to your local repository.

```bash

lein jar
cp target/zeppelin-clojure-0.5.6-incubating.jar $ZEPPELIN_HOME/interpreter/spark
cp ~/Downloads/clojure-1.8.0.jar $ZEPPELIN_HOME/interpreter/spark

```

Add hk.molloy.ClojureInterpreter to the zeppelin.interpreters property in ```$ZEPPELIN_HOME/conf/zeppelin-site.xml```

Start zeppelin and click interpreter.  You should see something like

spark %spark (default) , %pyspark , %sql , %dep , %clojure

Yeehah!

## License

Copyright © 2016 Matthew Molloy

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
