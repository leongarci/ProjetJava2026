package server;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MosquittoApp {

    private final String topicNewOrder = "orders/";
    private final String topicSerials = "serials/";
    private int qos;
    private MqttClient mqttClient;

    public MosquittoApp(String client) {
        try {
            this.mqttClient = new MqttClient("tcp://localhost:1883", client);
            this.qos = 1;

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String payload = new String(message.getPayload());
                        String responseTopic = topic;
                        //TODO
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
            this.mqttClient.publish(orderTopic, newOrder);
            this.mqttClient.subscribe(orderTopic, this.qos);
        } catch (MqttException e) {
            e.printStackTrace();

        }
    }

    public void askSerials(String uuid) {
        MqttMessage ask = new MqttMessage(uuid.getBytes());
        String topic = topicSerials + uuid + "/check";
        ask.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, ask);
            this.mqttClient.subscribe(topicSerials + uuid, this.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
