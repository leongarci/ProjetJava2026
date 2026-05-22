package protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import bernard_flou.Fabricateur;

public class CommandeSerializer {

    private static final Pattern FORMAT = Pattern.compile("^([A-Z_]+:\\d+;)+$");

    public static String serialize(Map<Fabricateur.TypeLunette, Integer> commande) {
        /**
         * Le format de sérialisation est "TYPE:QUANTITE;" pour chaque type de lunette.
         * Par exemple, une commande de 2 CHATGPT et 1 CLAUDE serait sérialisée en "CHATGPT:2;CLAUDE:1;".
         * @param commande La commande à sérialiser, sous forme de Map où la clé est le type de lunette et la valeur est la quantité commandée.
         * @return Une chaîne de caractères représentant la commande sérialisée, prête à être envoyée sur MQTT.
         */
        StringBuilder sb = new StringBuilder();
        commande.forEach((type, quantite) -> sb.append(type.name()).append(":").append(quantite).append(";"));
        return sb.toString();
    }

    public static Map<Fabricateur.TypeLunette, Integer> deserialize(String payload) {
        /**
         * Le format de désérialisation attend une chaîne de caractères au format "TYPE:QUANTITE;" pour chaque type de lunette.
         * Par exemple, une chaîne "CHATGPT:2;CLAUDE:1;" serait désérialisée en une Map avec 2 CHATGPT et 1 CLAUDE.
         *
         * @param payload La chaîne de caractères à désérialiser.
         * @return Une Map représentant la commande désérialisée.
         *
         */
        Map<Fabricateur.TypeLunette, Integer> deserialized = new HashMap<>();
        if (!FORMAT.matcher(payload).matches()) {
            throw new IllegalArgumentException("Format de commande invalide : " + payload);
        }
        for (String part : payload.split(";")) {
            if (part.isBlank()) {
                continue;
            }
            String[] split = part.split(":");
            deserialized.put(Fabricateur.TypeLunette.valueOf(split[0]), Integer.parseInt(split[1]));
        }
        return deserialized;
    }
}
