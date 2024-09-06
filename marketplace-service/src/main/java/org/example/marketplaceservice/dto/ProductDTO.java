package org.example.marketplaceservice.dto;


public class ProductDTO {

    private String name;

    private int price;

    private int count;

    private boolean isCount;

    public ProductDTO(String name, int price, int count, boolean isCount) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.isCount = isCount;
    }

    public ProductDTO() {
    }

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

    public boolean getIsCount() {
        return isCount;
    }

    public void setIsCount(boolean count) {
        isCount = count;
    }
}
