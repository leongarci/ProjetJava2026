package app;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import protocol.CommandeListener;
import protocol.Mosquitto;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MosquittoApp extends Mosquitto {

    private static final Logger logger = LoggerFactory.getLogger(MosquittoApp.class);

    private CommandeListener listener = new CommandeListener() {
        public void onValidated(String uuid) {
        }

        public void onDelivery(String uuid, String serials) {
        }

        public void onCancelled(String uuid, String reason) {
        }

        public void onError(String uuid, String error) {
        }
    };

    public MosquittoApp(String client) {
        try {
            initClient(client+ UUID.randomUUID());

            if (mqttClient.isConnected()) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        /**
                         * Traite les messages qui arrivent sur les topics "orders/+" et "serials/#".
                         * Pour les commandes, elle extrait l'ID de la commande du topic, puis appelle les méthodes du listener en fonction du statut (validé, annulé, livraison, erreur).
                         * Pour les requêtes de numéros de série, elle extrait l'ID du topic et appelle la méthode onChecked du listener avec le type de lunette reçu dans le payload.
                         * @param topic   Le topic sur lequel le message est arrivé
                         * @param message Le message MQTT contenant le payload à traiter
                         * @throws Exception Si une erreur survient lors du traitement du message
                         */
                        logger.debug("[APP] Message received on: {}", topic);
                         String payload = new String(message.getPayload());
                         String responseTopic = topic;
                         if (responseTopic != null) {
                             String id = topic.split("/")[1];
                             if (topic.startsWith(topicNewOrder + id + "/")) {
                                 String status = topic.substring(topic.lastIndexOf("/") + 1);
                                 switch (status) {
                                     case "validated" ->
                                         listener.onValidated(id);
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
                                 listener.onChecked(id,payload);
                                 mqttClient.unsubscribe(topic);
                             }
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
             }
 
         } catch (MqttException e) {
             logger.error("[APP] MqttException : {}", e.getMessage(), e);
         }
    }

    public void disconnect() {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
            mqttClient.close();
        } catch (MqttException e) {
            logger.error("[APP] MqttException during disconnect: {}", e.getMessage(), e);
        }
    }

    public void order(String orderDetails, String uuid) {
        /**
         * Envoie la commande sur le topic "orders/{uuid}".
         * @param orderDetails Les détails de la commande à envoyer dans le payload du message MQTT
         * @param uuid         L'identifiant unique de la commande, utilisé pour construire le topic de la commande et pour s'abonner aux réponses associées à cette commande.
         *
         */
        MqttMessage newOrder = new MqttMessage(orderDetails.getBytes());
        String orderTopic = topicNewOrder + uuid;
        newOrder.setQos(this.qos);
        try {
            this.mqttClient.subscribe(orderTopic + "/#", this.qos);
            this.mqttClient.publish(orderTopic, newOrder);
        } catch (MqttException e) {
            logger.error("Failed to publish order {}", orderTopic, e);
        }
    }

    public void askSerials(String code) {
        /**
         * Envoie une requête pour obtenir les numéros de série associés à un code de lunette sur le topic "serials/{code}/check".
         * Le payload du message contient le code de lunette à vérifier.
         * Avant de publier la requête, l'application s'abonne au topic "serials/{code}" pour recevoir la réponse contenant les numéros de série.
         * En cas d'erreur lors de l'abonnement ou de la publication, un message d'erreur est loggé.
         *
         * @param code ,numéro de série de la lunette à vérifier, utilisé pour construire le topic de la requête et pour s'abonner à la réponse associée à ce code.
         */
        MqttMessage ask = new MqttMessage(code.getBytes());
        String topic = topicSerials + code + "/check";
        ask.setQos(this.qos);
        try {
            this.mqttClient.subscribe(topicSerials + code, this.qos);
            this.mqttClient.publish(topic, ask);
        } catch (MqttException e) {
            logger.error("Failed to ask serials for {}", code, e);
        }
    }

    public void setListener(CommandeListener listener) {
        this.listener = listener;
    }
}
