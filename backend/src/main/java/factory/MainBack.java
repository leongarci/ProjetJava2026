package factory;

import bernard_flou.Fabricateur;

public class MainBack {

    public static void main(String[] args) {
        Fabricateur fabricateur = new Fabricateur();
        Usine usine = new Usine(fabricateur);
        new MosquittoBack("back-client", usine);
    }
}
