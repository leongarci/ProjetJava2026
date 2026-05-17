package protocol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public abstract class Mosquitto {

    protected final String topicNewOrder = "orders/";
    protected final String topicSerials = "serials/";
    protected int qos;
    protected MqttClient mqttClient;

    protected void initClient(String client) throws MqttException {
        try (InputStream input = new FileInputStream(".properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.mqttClient = new MqttClient(prop.getProperty("broker"), client,new MemoryPersistence());
            this.mqttClient.connect();
            this.qos = 1;
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }
}
