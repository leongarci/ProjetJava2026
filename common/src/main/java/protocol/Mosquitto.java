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
        /**
         * Initialise le client MQTT en chargeant les paramètres de connexion depuis un fichier .properties situé dans le classpath.
         * Le fichier doit contenir une clé "broker" avec l'URL du broker MQTT.
         * En cas d'erreur lors du chargement du fichier ou de la connexion au broker, une exception est levée et un message d'erreur est loggé.
         *
         * @param client Le nom du client MQTT à utiliser pour la connexion
         * @throws MqttException Si une erreur survient lors de la connexion au broker MQTT
         */
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
