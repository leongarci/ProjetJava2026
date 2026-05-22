# Lancement de l'application

1. Téléchargez les deux fichiers .jar disponibles sur le dépôt GitHub (https://github.com/leongarci/ProjetJava2026)

## Prérequis

- Java 17+
- Docker

## Lancement

1. Téléchargez `backend-1.0-SNAPSHOT.jar` et `frontend-1.0-SNAPSHOT.jar` depuis la page GitHub du projet.

2. Démarrez le broker MQTT Mosquitto.

> Si Mosquitto est déjà installé sur votre machine, passez directement à l'étape suivante.

3. Démarrez le backend :
```bash
java -jar backend-1.0-SNAPSHOT.jar
```

4. Démarrez le frontend :
```bash
java -jar frontend-1.0-SNAPSHOT.jar
```

> Vous pouvez lancer plusieurs instances du frontend pour simuler plusieurs clients.

```properties
broker=tcp://localhost:1883
```