package com.rayson.eldoretonlinemarketadmin;

import android.graphics.drawable.Drawable;

public class Menus {
    String itemName;
    Drawable itemImg;

    public Menus() {
    }

    public Menus(String itemName, Drawable itemImg) {
        this.itemName = itemName;
        this.itemImg = itemImg;
    }

    public Drawable getItemImg() {
        return itemImg;
    }

    public void setItemImg(Drawable itemImg) {
        this.itemImg = itemImg;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


}
