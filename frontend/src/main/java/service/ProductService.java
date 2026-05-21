package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Product;
import java.io.InputStreamReader;
import java.util.List;

public class ProductService {

    private static List<Product> cache;

    public static List<Product> getProducts() {
        if (cache == null) {
            var stream = ProductService.class
                    .getResourceAsStream("/assets/products.json");
            cache = new Gson().fromJson(
                    new InputStreamReader(stream),
                    new TypeToken<List<Product>>(){}.getType()
            );
        }
        return cache;
    }

    public static Product findById(String id) {
        return getProducts().stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst().orElse(null);
    }
}