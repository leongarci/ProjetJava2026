package protocol;

import bernard_flou.Fabricateur;

import java.util.HashMap;
import java.util.Map;

public class CommandeSerializer {

    public static String serialize(Map<Fabricateur.TypeLunette,Integer> commande){
        StringBuilder sb = new StringBuilder();
        commande.forEach((type, quantite) -> sb.append(type.name()).append(":").append(quantite).append(";"));
        return sb.toString();
    }

    public static Map<Fabricateur.TypeLunette, Integer> deserialize(String payload) {
        Map<Fabricateur.TypeLunette, Integer> deserialized = new HashMap<>();
        for (String part : payload.split(";")) {
            String[] split = part.split(":");
            deserialized.put(Fabricateur.TypeLunette.valueOf(split[0]), Integer.parseInt(split[1]));
        }
        return deserialized;
    }
}
