package protocol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Mosquitto {

    protected final String topicNewOrder = "orders/";
    protected final String topicSerials = "serials/";

    protected int qos;
    protected MqttClient mqttClient;
    private static final Logger logger = LoggerFactory.getLogger(Mosquitto.class);

    protected void initClient(String client) throws MqttException {
        // Remplacer les lignes 24 à 26 par :
        try (InputStream input = Mosquitto.class.getClassLoader().getResourceAsStream(".properties")) {
            if (input == null) {
                throw new FileNotFoundException("Impossible de trouver .properties dans le classpath");
            }
            Properties prop = new Properties();
            prop.load(input);
            this.mqttClient = new MqttClient(prop.getProperty("broker"), client, new MemoryPersistence());
            this.mqttClient.connect();
            this.qos = 1;
        } catch (IOException ex) {
            logger.error("Failed to initialize MQTT client", ex);

        }
    }
}
