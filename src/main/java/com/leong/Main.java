package com.leong;

import bernard_flou.Fabricateur;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Fabricateur fabricateur=new Fabricateur();
        Usine usine=new Usine(fabricateur);
        Map<Fabricateur.TypeLunette, Integer> commande = new HashMap<>();
        Map<Fabricateur.TypeLunette, Integer> commande2 = new HashMap<>();
        commande.put(Fabricateur.TypeLunette.CHATGPT, 5);
        commande2.put(Fabricateur.TypeLunette.CLAUDE,8);

        List<Fabricateur.Lunette> lunettes = usine.produire(commande);
        List<Fabricateur.Lunette> lunettes2 = usine.produire(commande2);
        lunettes.forEach(l -> System.out.println(l.type + " : " + l.serial));

        lunettes2.forEach(l -> System.out.println(l.type + " : " + l.serial));

    }
}
