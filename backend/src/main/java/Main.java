
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bernard_flou.Fabricateur;
import factory.Usine;

public class Main {

    public static void main(String[] args) {
        Fabricateur fabricateur = new Fabricateur();
        Usine usine = new Usine(fabricateur);
        Map<Fabricateur.TypeLunette, Integer> commande = new HashMap<>();
        Map<Fabricateur.TypeLunette, Integer> commande2 = new HashMap<>();
        commande.put(Fabricateur.TypeLunette.CHATGPT, 2);
        commande.put(Fabricateur.TypeLunette.CLAUDE, 1);

        List<Fabricateur.Lunette> lunettes = usine.produire(commande);
        lunettes.forEach(l -> System.out.println(l.type + " : " + l.serial));

    }
}
