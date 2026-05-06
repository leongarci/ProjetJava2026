package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public abstract class Mosquitto {

    protected final String topicNewOrder = "orders/";
    protected final String topicSerials = "serials/";
    protected int qos;
    protected MqttClient mqttClient;

    protected void initClient(String client) throws MqttException {
        try (InputStream input = new FileInputStream(".properties")) {
            Properties prop = new Properties();
            prop.load(input);
            System.out.println(prop.getProperty("mail"));
            this.mqttClient = new MqttClient(prop.getProperty("broker"), client);
            this.qos = 1;
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }
}
