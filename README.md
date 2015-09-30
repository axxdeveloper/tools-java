# tools-java
Please execute Maven build to get tools-java.jar from target folder first
<pre>
mvn clean package
</pre>

## ActiveMQQueueSize
Used to print queue size of all Queues.
<pre>
Usage: java -cp tools-java.jar shooeugenesea.toolsjava.ActiveMQQueueSize ip ActiveMQJMXPort
Ex. java -cp tools-java.jar shooeugenesea.toolsjava.ActiveMQQueueSize 127.0.0.1 6000
</pre>  
