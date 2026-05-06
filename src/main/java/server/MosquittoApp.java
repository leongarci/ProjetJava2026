package server;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MosquittoApp extends Mosquitto {

    public MosquittoApp(String client) {
        try {
            initClient(client);

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String payload = new String(message.getPayload());
                        String responseTopic = topic;
                        if (responseTopic != null) {
                            String id = topic.split("/")[1];
                            if (topic.startsWith(topicNewOrder + id + "/")) {
                                String status = topic.substring(topic.lastIndexOf("/") + 1);
                                switch (status) {
                                    case "validated" -> {
                                        /* Handle validated */ }
                                    case "cancelled" -> {
                                        /* Handle cancelled with payload */
                                        mqttClient.unsubscribe(topic);
                                    }
                                    case "delivery" -> {
                                        /* Handle delivery with payload*/
                                        mqttClient.unsubscribe(topic);
                                    }
                                    case "error" -> {
                                        /* Handle error with payload */
                                        mqttClient.unsubscribe(topic);
                                    }
                                }
                            } else if (topic.startsWith(topicSerials)) {
                                /* Handle serials with payload */
                                mqttClient.unsubscribe(topic);
                            }
                        }
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection is lost: " + cause.getMessage());
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        System.out.println("Message publish is complete: " + token.isComplete());
                    }
                });

            }

            /* Keep the application open, so that the subscribe operation can tested */
            System.out.println("Press Enter to disconnect");
            System.in.read();
            /* Proceed with disconnecting */
            mqttClient.disconnect();
            mqttClient.close();

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void order(String orderDetails, String uuid) {
        MqttMessage newOrder = new MqttMessage(orderDetails.getBytes());
        String orderTopic = topicNewOrder + uuid;
        newOrder.setQos(this.qos);
        try {
            this.mqttClient.subscribe(orderTopic + "/#", this.qos);
            this.mqttClient.publish(orderTopic, newOrder);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void askSerials(String uuid) {
        MqttMessage ask = new MqttMessage(uuid.getBytes());
        String topic = topicSerials + uuid + "/check";
        ask.setQos(this.qos);
        try {
            this.mqttClient.subscribe(topicSerials + uuid, this.qos);
            this.mqttClient.publish(topic, ask);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
