package com.leong;

import bernard_flou.Fabricateur;

import java.util.HashMap;
import java.util.Map;

import static protocol.CommandeSerializer.deserialize;
import static protocol.CommandeSerializer.serialize;

public class Main {
    public static void main(String[] args) {

        Map<Fabricateur.TypeLunette, Integer> commandeTest = new HashMap<>();
        commandeTest.put(Fabricateur.TypeLunette.CHATGPT, 2);
        commandeTest.put(Fabricateur.TypeLunette.CLAUDE, 1);
        commandeTest.put(Fabricateur.TypeLunette.LE_CHAT, 4);
        System.out.println("test");
        String serialized = serialize(commandeTest);
        System.out.println(serialized);

        Map<Fabricateur.TypeLunette, Integer> deserialized= deserialize(serialized);
        System.out.println(deserialized.toString());


    }
}
