package frontend;

import java.util.Map;

import bernard_flou.Fabricateur;
import common.CommandeListener;
import common.Mosquitto;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import common.protocol.CommandeSerializer;

public class MosquittoApp extends Mosquitto {

    private CommandeListener listener = new CommandeListener() {
        public void onValidated(String uuid) {}
        public void onDelivery(String uuid, String serials) {}
        public void onCancelled(String uuid, String reason) {}
        public void onError(String uuid, String error) {}
    };

    public MosquittoApp(String client) {
        try {
            initClient(client);

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("[APP] Message reçu sur : " + topic);
                        String payload = new String(message.getPayload());
                        String responseTopic = topic;
                        if (responseTopic != null) {
                            String id = topic.split("/")[1];
                            if (topic.startsWith(topicNewOrder + id + "/")) {
                                String status = topic.substring(topic.lastIndexOf("/") + 1);
                                switch (status) {
                                    case "validated" -> listener.onValidated(id);
                                    case "cancelled" -> {
                                        listener.onCancelled(id, payload);
                                        mqttClient.unsubscribe(topicNewOrder + id + "/#");
                                    }
                                    case "delivery" -> {
                                        listener.onDelivery(id, payload);
                                        mqttClient.unsubscribe(topicNewOrder + id + "/#");
                                    }
                                    case "error" -> {
                                        listener.onError(id, payload);
                                        mqttClient.unsubscribe(topicNewOrder + id + "/#");
                                    }
                                }
                            } else if (topic.startsWith(topicSerials)) {
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

        } catch (MqttException e) {
            System.err.println("[APP] MqttException : " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void disconnect() {
    try {
        if (mqttClient.isConnected()) {
            mqttClient.disconnect();
        }
        mqttClient.close();
    } catch (MqttException e) {
        System.err.println("[APP] MqttException during disconnect: " + e.getMessage());
        e.printStackTrace();
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

    public void setListener(CommandeListener listener) {
        this.listener = listener;
    }
}
