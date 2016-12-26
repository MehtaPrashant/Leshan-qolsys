import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
public class KafkaConsumer {
	private ConsumerConnector consumerConnector = null;
	//private final String topic = "abc";
	public void initialize(String brokerIp) {
		Properties props = new Properties();
		//props.put("zookeeper.connect", "192.168.15.143:2181");
		 props.put("zookeeper.connect", brokerIp+":2181");
		props.put("group.id", "test-consumer-group");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "300");
		props.put("auto.commit.interval.ms", "1000");
		ConsumerConfig conConfig = new ConsumerConfig(props);
		consumerConnector = Consumer.createJavaConsumerConnector(conConfig);
	}

	public void consume(String leshanIp, String topic) throws ClientProtocolException, IOException, InterruptedException {
		Map<String, Integer> topicCount = new HashMap<String, Integer>();       
		topicCount.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams =
				consumerConnector.createMessageStreams(topicCount);         
		List<KafkaStream<byte[], byte[]>> kStreamList = consumerStreams.get(topic);
		Thread.sleep(2000);
		for (final KafkaStream<byte[], byte[]> kStreams : kStreamList) {
			ConsumerIterator<byte[], byte[]> consumerIte = kStreams.iterator();
			while (consumerIte.hasNext()){
				byte[] bb = consumerIte.next().message();
				String consumedMsg = null;
				if(bb != null){
				consumedMsg = new String(bb);
				System.out.println("Message consumed from topic [" + topic + "] : " + consumedMsg);
				MyHttpClient.postCommandsToDevice(consumedMsg, leshanIp);
				}
			}
		}
	}
	//sample word count spark code     
	/* public static void wordCountJava8( String filename ){
          SparkConf conf = new SparkConf().setMaster("local").setAppName("KafkaConsumer");
          JavaSparkContext sc = new JavaSparkContext(conf);
          JavaRDD<String> input = sc.textFile( filename );
          JavaRDD<String> words = input.flatMap( s -> Arrays.asList( s.split( " " ) ).iterator() );
          JavaPairRDD<String, Integer> counts = words.mapToPair( t -> new Tuple2( t, 1 ) ).reduceByKey( (x, y) -> (int)x + (int)y );
          //counts.saveAsTextFile( "file_new7" );
       }*/

	public static void main(String[] args) throws InterruptedException, ClientProtocolException, IOException {
		// wordCountJava8("hello");
		String brokerIp = "localhost";
		String leshanIp = "192.168.15.151";
		String topic = "panelEvents";
		/*if(leshanIp == null || leshanIp.isEmpty()){
			leshanIp = brokerIp;
		}*/
		if(brokerIp != null && leshanIp != null){
			KafkaConsumer kafkaConsumer = new KafkaConsumer();
			kafkaConsumer.initialize(brokerIp);
			Thread.sleep(1000);
			kafkaConsumer.consume(leshanIp,topic);  
		}
	}	
}
