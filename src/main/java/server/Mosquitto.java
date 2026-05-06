package server;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public abstract class Mosquitto {

    protected final String topicNewOrder = "orders/";
    protected final String topicSerials = "serials/";
    protected int qos;
    protected MqttClient mqttClient;

    protected void initClient(String client) throws MqttException {
        this.mqttClient = new MqttClient("tcp://localhost:1883", client);
        this.qos = 1;
    }
}