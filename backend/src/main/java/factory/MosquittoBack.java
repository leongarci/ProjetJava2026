package factory;

import java.io.IOException;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bernard_flou.Fabricateur;
import protocol.CommandeSerializer;
import protocol.LivraisonSerializer;
import protocol.Mosquitto;

public class MosquittoBack extends Mosquitto {

    private final Usine usine;
    private static final Logger logger = LoggerFactory.getLogger(MosquittoBack.class);

    public MosquittoBack(String client, Usine usine) {
        this.usine = usine;
        try {
            initClient(client);

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String payload = new String(message.getPayload());
                        String id = topic.split("/")[1];
                        if (topic.equals(topicNewOrder + id)) {
                            try {
                                Map<Fabricateur.TypeLunette, Integer> commande = CommandeSerializer.deserialize(payload);
                                usine.valider(commande);
                                validateOrder(id);
                                usine.ajouterCommandeMutualisee(id, commande)
                                        .whenComplete((lunettes, error) -> {
                                            if (error != null) {
                                                Throwable cause = error.getCause() != null ? error.getCause() : error;
                                                errorOrder(id, cause.getMessage());
                                            } else {
                                                String serials = LivraisonSerializer.serialize(lunettes);
                                                deliverOrder(id, serials);
                                            }
                                        });

                            } catch (IllegalArgumentException e) {
                                cancelOrder(id, e.getMessage());
                            }

                        } else if (topic.equals(topicSerials + id + "/check")) {
                            String serial = String.valueOf(usine.getSerialsProduits().get(id));
                            System.out.println("serials produits: " + serial.getClass());
                            if (serial.equals("null")) {
                                serial = "invalid";
                            }
                            serialsInfos(id, serial);
                        }
                    }

                    @Override
                    public void connectionLost(Throwable cause) {
                        logger.warn("Connection lost: {}", cause == null ? "unknown cause" : cause.getMessage(), cause);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        logger.debug("Message publish complete: {}", token.isComplete());
                    }
                });

                this.mqttClient.subscribe(topicNewOrder + "+", this.qos);
                this.mqttClient.subscribe(topicSerials + "#", this.qos);

            }

            logger.info("Press Enter to disconnect");
            System.in.read();
            mqttClient.disconnect();
            mqttClient.close();
            usine.shutdown();

        } catch (MqttException e) {
            logger.error("MqttException in MosquittoBack constructor", e);
        } catch (IOException e) {
            logger.error("IOException while waiting for disconnect input", e);
            throw new RuntimeException(e);
        }

    }

    public void validateOrder(String uuid) {
        MqttMessage emptyMessage = new MqttMessage("".getBytes());
        String topic = topicNewOrder + uuid + "/validated";
        emptyMessage.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, emptyMessage);
        } catch (MqttException e) {
            logger.error("Failed to publish validateOrder for {}", uuid, e);
        }
    }

    public void cancelOrder(String uuid, String reason) {
        MqttMessage cancellation = new MqttMessage(reason.getBytes());
        String topic = topicNewOrder + uuid + "/cancelled";
        cancellation.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, cancellation);
        } catch (MqttException e) {
            logger.error("Failed to publish cancelOrder for {}: {}", uuid, reason, e);
        }
    }

    public void deliverOrder(String uuid, String content) {
        MqttMessage delivery = new MqttMessage(content.getBytes());
        String topic = topicNewOrder + uuid + "/delivery";
        delivery.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, delivery);
        } catch (MqttException e) {
            logger.error("Failed to publish deliverOrder for {}", uuid, e);
        }

    }

    public void errorOrder(String uuid, String error) {
        MqttMessage errorMessage = new MqttMessage(error.getBytes());
        String topic = topicNewOrder + uuid + "/error";
        errorMessage.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, errorMessage);
        } catch (MqttException e) {
            logger.error("Failed to publish errorOrder for {}", uuid, e);
        }
    }

    public void serialsInfos(String uuid, String serials) {
        MqttMessage serialsMessage = new MqttMessage(serials.getBytes());
        String topic = topicSerials + uuid;
        serialsMessage.setQos(this.qos);
        try {
            this.mqttClient.publish(topic, serialsMessage);
        } catch (MqttException e) {
            logger.error("Failed to publish serialsInfos for {}", uuid, e);
        }
    }
}
