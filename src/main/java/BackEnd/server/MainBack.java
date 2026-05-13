package BackEnd.server;

import bernard_flou.Fabricateur;
import BackEnd.protocol.Usine;

public class MainBack {
    public static void main(String[] args) {
        Fabricateur fabricateur = new Fabricateur();
        Usine usine = new Usine(fabricateur);
        new MosquittoBack("back-client", usine);
    }
}
