package org.example.marketplaceservice.dto;


public class ProductDTO {

    private String name;

    private int price;

    private int count;

    private boolean isCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCount() {
        return isCount;
    }

    public void setCount(boolean count) {
        isCount = count;
    }
}
