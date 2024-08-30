package org.example.marketplaceservice.dto;


public class ProductInOrderDTO {

    private String name;

    private int price;

    private int count;

    public ProductInOrderDTO(String name, int price, int count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public ProductInOrderDTO() {
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
}
