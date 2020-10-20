package com.rayson.eldoretonlinemarketadmin;

public class Product {
    private String name, Image, description, price, menuId;

    public Product() {
    }

    public Product(String name, String image) {
        this.name = name;
        Image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
    }

    public Product(String name, String image, String description, String price, String menuId) {
        this.name = name;
        Image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
