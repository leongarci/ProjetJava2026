package app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Product;

import java.io.InputStreamReader;
import java.util.List;

public class ProductService {
    private static List<Product> products;

    public static List<Product> loadProducts() {
        /**
         * Chargement paresseux des produits depuis le fichier JSON. Le fichier est lu une seule fois et les produits sont mis en cache pour les appels suivants.
         */
        if (products == null) {
            var stream = ProductService.class
                    .getResourceAsStream("/products.json");
            var reader = new InputStreamReader(stream);
            products = new Gson().fromJson(reader,
                    new TypeToken<List<Product>>(){}.getType());
        }
        return products;
    }

    public static Product findById(String id) {
        return loadProducts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);
    }
}