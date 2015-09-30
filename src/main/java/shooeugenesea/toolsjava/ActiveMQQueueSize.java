package shooeugenesea.toolsjava;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Usage: java tools-java.jar shooeugenesea.toolsjava.ActiveMQQueueSize ip port
 * Ex. java tools-java.jar shooeugenesea.toolsjava.ActiveMQQueueSize 127.0.0.1 6000
 * 
 * */
public class ActiveMQQueueSize {
    
    private static final String USAGE_MSG = "Usage: java tools-java.jar shooeugenesea.toolsjava.ActiveMQQueueSize #{ip} #{ActiveMQPort}\n"
                                            + "Ex. java tools-java.jar shooeugenesea.toolsjava.ActiveMQQueueSize 127.0.0.1 6000";
    private static final String LOG_TIME_PATTERN = "yyyy-MM-dd HH:mm:SSS";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(LOG_TIME_PATTERN);
    
    public static void main(String[] params) throws Exception {
        validateParams(params);
        
        try {
            String url = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
            String jmxHost = params[0];
            Integer jmxPort = new Integer(params[1]);
            
            String completeURL = url;
            if (isNotBlank(jmxHost) && jmxHost != null) {
                completeURL = String.format(url, jmxHost, jmxPort);
            }
            
            JMXServiceURL jmxURL = new JMXServiceURL(completeURL);
            JMXConnector connector = JMXConnectorFactory.connect(jmxURL);
            MBeanServerConnection mbsc = connector.getMBeanServerConnection();
            
            for (ObjectName on: mbsc.queryNames(null, null)) {
                if ( on.getCanonicalName().contains("Type=Queue") ) {
                    printQueueSize(mbsc, on);
                }
            }            
        } catch (Throwable ex) {
            throw new RuntimeException(USAGE_MSG, ex);
        }
    }
    
    private static void printQueueSize(MBeanServerConnection mbsc, ObjectName on) {
        try {
            if (mbsc.isRegistered(on)) {
                AttributeList attributes = mbsc.getAttributes(on, new String[]{"QueueSize"});
                if ( attributes.size() == 1 ) {
                    Attribute queueSize = (Attribute) attributes.get(0);
                    System.out.println("Timestamp:" + sdf.format(new Date()) + ",ObjectName:" + on.getCanonicalName() + ",QueueSize:" + queueSize.getValue());                        
                }                            
            }
        } catch (Throwable ex) {
            System.out.println("Error print queue size. ObjectName:" + on + ", exception:" + ex);
        }
    }
    
    private static void validateParams(String[] params) {
        if ( params == null || params.length != 2 || !isValidPortRange(params[1]) ) {
            throw new IllegalArgumentException(USAGE_MSG);
        }
    }
    
    private static boolean isValidPortRange(String s) {
        try {
            new Integer(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static boolean isNotBlank(String s) {
        return s != null && s.trim().length() > 0;
    }
    
}
