package protocol;

import bernard_flou.Fabricateur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivraisonSerializer {
    public static String serialize(List<Fabricateur.Lunette> livraisons){
        StringBuilder sb = new StringBuilder();
        for(Fabricateur.Lunette livraison : livraisons){
            sb.append(livraison.type.name()).append(":").append(livraison.serial).append(";");
        }
        return sb.toString();
    }

    public static List<String> deserialize(String payload) {
        List<String> lunettes = new ArrayList<>();
        for (String part : payload.split(";")) {
            if (part.isBlank()) continue;
            String[] split = part.split(":");
            lunettes.add("Type: " + split[0] + " | Série: " + split[1]);
        }
        return lunettes;
    }
}
