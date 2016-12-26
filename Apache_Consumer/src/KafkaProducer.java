/*import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
   //import org.apache.kafka.clients.producer.ProducerConfig;
   //import org.apache.kafka.clients.producer.Producer;
   //import org.apache.kafka.clients.producer.KeyedMessage;
    public class KafkaProducer {
       private static Producer<Integer, String> producer;
       private static final String topic= "mytopic";
       public void initialize() {
             Properties producerProps = new Properties();
             producerProps.put("metadata.broker.list", "localhost:9092");
            // producerProps.put("metadata.broker.list", "192.168.15.140:9092");
             producerProps.put("serializer.class", "kafka.serializer.StringEncoder");
             producerProps.put("request.required.acks", "1");
             ProducerConfig producerConfig = new ProducerConfig(producerProps);
             producer = new Producer<Integer, String>(producerConfig);
       }
       public void publishMesssage() throws Exception{            
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));               
         while (true){
             System.out.print("Enter message to send : ");
           String msg = null;
           msg = reader.readLine(); // Read message from console
           //Define topic name and message
           KeyedMessage<Integer, String> keyedMsg =
                        new KeyedMessage<Integer, String>(topic, msg);
           producer.send(keyedMsg); // This publishes message on given topic
           //if("Y".equals(msg)){ break; }
           System.out.println("Message [" + msg + "] message on Consumer's console");
         }
         //return;
       }
       public static void main(String[] args) throws Exception {

    	  
    		   KafkaProducer kafkaProducer = new KafkaProducer();
    		   kafkaProducer.initialize(); 
    		   kafkaProducer.publishMesssage();
    		   producer.close();
    	   
       }
   }*/