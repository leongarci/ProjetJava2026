package protocol;

import java.util.ArrayList;
import java.util.List;

import bernard_flou.Fabricateur;

public class LivraisonSerializer {

    public static String serialize(List<Fabricateur.Lunette> livraisons) {
        /**
         * Le format de serialisation est "TYPE:SERIE;" pour chaque lunette livrée.
         * Par exemple, une livraison de 2 CHATGPT avec les séries S001 et S002 serait sérialisée en "CHATGPT:S001;CHATGPT:S002;".
         * @param livraisons La liste des lunettes livrées, où chaque lunette a un type et un numéro de série.
         * @return Une chaîne de caractères représentant la livraison sérialisée, prête à être envoyée sur MQTT.
         */
        StringBuilder sb = new StringBuilder();
        for (Fabricateur.Lunette livraison : livraisons) {
            sb.append(livraison.type.name()).append(":").append(livraison.serial).append(";");
        }
        return sb.toString();
    }

    public static List<String> deserialize(String payload) {
        /**
         * Le format de désérialisation attend une chaîne de caractères au format "TYPE:SERIE;" pour chaque lunette livrée.
         * Par exemple, une chaîne "CHATGPT:S001;CLAUDE:S002;" serait désérialisée en une liste de chaînes avec "Type: CHATGPT | Série: S001" et "Type: CLAUDE | Série: S002".
         * @param payload La chaîne de caractères à désérialiser.
         * @return Une liste de chaînes représentant les lunettes désérialisées.
         *
         */
        List<String> lunettes = new ArrayList<>();
        for (String part : payload.split(";")) {
            if (part.isBlank()) {
                continue;
            }
            String[] split = part.split(":");
            lunettes.add("Type: " + split[0] + " | Série: " + split[1]);
        }
        return lunettes;
    }
}
