/*******************************************************************************
 * Copyright (c) 2016 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.eclipse.leshan.server.cluster;

import java.util.Properties;

import org.eclipse.leshan.server.client.Client;
import org.eclipse.leshan.server.client.ClientRegistryListener;
import org.eclipse.leshan.server.client.ClientUpdate;
import org.eclipse.leshan.server.cluster.serialization.ClientSerDes;
import org.eclipse.leshan.server.cluster.serialization.ClientUpdateSerDes;
import org.leshan.server.configuration.DataBaseConfiguration;

import com.eclipsesource.json.JsonObject;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * A Client registry Listener which publish registration event on Redis channel.
 */
public class RedisRegistrationEventPublisher implements ClientRegistryListener {

    private static String REGISTER_EVENT = "LESHAN_REG_NEW";
    private static String UPDATE_EVENT = "LESHAN_REG_UP";
    private static String DEREGISTER_EVENT = "LESHAN_REG_DEL";
    private Pool<Jedis> pool;
    private final String kafkaBroker1Add = DataBaseConfiguration.getInstance().getPropertyString("KAFKA_BROKER1_ADD");
    private final int kafkaBroker1Port = DataBaseConfiguration.getInstance().getPropertyInt("KAFKA_BROKER1_PORT");
    public RedisRegistrationEventPublisher(Pool<Jedis> p) {
        this.pool = p;
    }

    @Override
    public void registered(Client client) {
        // System.out.println(
        // "Registration" + "client EP=" + client.getEndpoint() + ",registrationID=" + client.getRegistrationId());
        String payload = ClientSerDes.sSerialize(client);
        try (Jedis j = pool.getResource()) {
            sendToBroker(REGISTER_EVENT, payload);
            j.publish(REGISTER_EVENT, payload);
        }
    }

    @Override
    public void updated(ClientUpdate update, Client registrationUpdated) {
        // System.out.println("Update" + "client EP=" + registrationUpdated.getEndpoint() + ",registrationID="
        // + registrationUpdated.getRegistrationId());
        JsonObject value = new JsonObject();
        value.add("regUpdate", ClientUpdateSerDes.jSerialize(update));
        value.add("regUpdated", ClientSerDes.jSerialize(registrationUpdated));

        try (Jedis j = pool.getResource()) {
            sendToBroker(UPDATE_EVENT, value.toString());
            j.publish(UPDATE_EVENT, value.toString());
        }
    }

    @Override
    public void unregistered(Client client) {
        // System.out
        // .println(
        // "Delete" + "client EP=" + client.getEndpoint() + ",registrationID="
        // + client.getRegistrationId());
        String payload = ClientSerDes.sSerialize(client);
        try (Jedis j = pool.getResource()) {
            sendToBroker(DEREGISTER_EVENT, payload);
            j.publish(DEREGISTER_EVENT, payload);
        }
    }

    @SuppressWarnings("deprecation")
    public void sendToBroker(String topic, String json) {
        try {
            Properties producerProps = new Properties();
            producerProps.put("metadata.broker.list", kafkaBroker1Add + ":" + kafkaBroker1Port);
            producerProps.put("serializer.class", "kafka.serializer.StringEncoder");
            producerProps.put("request.required.acks", "1");
            ProducerConfig producerConfig = new ProducerConfig(producerProps);
            Producer<Integer, String> producer;
            producer = new Producer<Integer, String>(producerConfig);
            KeyedMessage<Integer, String> keyedMsg = new KeyedMessage<Integer, String>(topic, json);
            producer.send(keyedMsg);
            producer.close();
        } catch (Exception e) {
            System.out.println("Exception occurred while sending data to kafka" + e.getMessage());
        }
    }

}
