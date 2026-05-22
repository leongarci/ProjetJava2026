# Lancement de l'application

1. Téléchargez les deux fichiers .jar disponibles sur le dépôt GitHub (https://github.com/leongarci/ProjetJava2026)

_Si vous avez déjà Mosquitto installé sur votre PC, vous pouvez passer à la 5ème étape_

2. Téléchargez également de fichier docker-compose.yml disponible sur ce même dépôt

3. Assurez vous que Docker est bien installé sur votre PC, et assurez vous que le service est lancé

4. Depuis un terminal, placez vous à l'emplacement où vous avez téléchargé le fichier .yml, et lancez la commande `docker-compose up`

5. Lancez tout d'abord le fichier _backend.jar_ avec la commande `java -jar backend.jar` pour démarrer l'usine

6. Lancez ensuite le fichier _frontend.jar_ avec la commande `java -jar frontend.jar'` pour démarrer une interface client
