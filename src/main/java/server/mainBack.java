package server;

import bernard_flou.Fabricateur;
import com.leong.Usine;

public class mainBack {
    public static void main(String[] args) {
        Fabricateur fabricateur = new Fabricateur();
        Usine usine = new Usine(fabricateur);
        new MosquittoBack("back-client", usine);
    }
}
