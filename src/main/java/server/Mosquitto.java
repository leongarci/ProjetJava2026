package server;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Mosquitto {

    private final String topicNewOrder = "orders/";
    private final String topicSerials = "serials/";
    private static final String username = "Lunettes";
    private static final String password = "lunettes";
    private int qos;
    private MqttClient mqttClient = null;

    public Mosquitto(String client) {
        try {
            this.mqttClient = new MqttClient("tcp://localhost:1883", client);
            this.qos = 1;
            MqttConnectOptions mqttOptions = new MqttConnectOptions();
            mqttOptions.setUserName(Mosquitto.username);
            mqttOptions.setPassword(Mosquitto.password.toCharArray());
            this.mqttClient.connect(mqttOptions);

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("Received message: " + new String(message.getPayload()));
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

                if (client.equals("truc client")) {
                    this.mqttClient.subscribe(topicNewOrder, this.qos);
                    this.mqttClient.subscribe(topicSerials, this.qos);
                }

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

    public void validateOrder(String uuid) {
        MqttMessage emptyMessage = new MqttMessage("".getBytes());
        String topic = topicNewOrder + uuid + "/validated";
        emptyMessage.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, emptyMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void cancelOrder(String uuid, String reason) {
        MqttMessage cancellation = new MqttMessage(reason.getBytes());
        String topic = topicNewOrder + uuid + "/cancelled";
        cancellation.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, cancellation);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void deliverOrder(String uuid, String content) {
        MqttMessage delivery = new MqttMessage(content.getBytes());
        String topic = topicNewOrder + uuid + "/delivered";
        delivery.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, delivery);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void errorOrder(String uuid, String error) {
        MqttMessage errorMessage = new MqttMessage(error.getBytes());
        String topic = topicNewOrder + uuid + "/error";
        errorMessage.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, errorMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void serialsInfos(String uuid, String serials) {
        MqttMessage serialsMessage = new MqttMessage(serials.getBytes());
        String topic = topicSerials + uuid;
        serialsMessage.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, serialsMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
