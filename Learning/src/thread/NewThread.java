package thread;

import java.util.Arrays;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;



public class NewThread {

	public static void main(String[] args){
		new Thread(new Runnable() {

			public void run() {
				String topic = "panelEvents";
				String group = "REQ_GROUP";

				Properties props = new Properties();
				props.put("bootstrap.servers", "localhost:9092");
				props.put("group.id", group);
				props.put("enable.auto.commit", "true");
				props.put("auto.commit.interval.ms", "1000");
				props.put("session.timeout.ms", "30000");
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				@SuppressWarnings("resource")
				org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, String>(props);
				consumer.subscribe(Arrays.asList(topic));
				System.out.println("Subscribed to topic " + topic);
				do {
					
					org.apache.kafka.clients.consumer.ConsumerRecords<String, String> records = consumer.poll(100);
					for (ConsumerRecord<String, String> record : records)
						 System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(),record.value());
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (true);
			}

			// }, String.format("Redis %s channel reader",
			// "LESHAN_REQ")).start();
		}).start();
	}

}
