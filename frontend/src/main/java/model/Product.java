package model;

public class Product {
    private String id;
    private String name;
    private double price;
    private String badge;
    private String description;

    public String getId()          { return id; }
    public String getName()        { return name; }
    public double getPrice()       { return price; }
    public String getBadge()       { return badge != null ? badge : ""; }
    public String getDescription() { return description; }
}