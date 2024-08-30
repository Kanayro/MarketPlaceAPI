package org.example.marketplaceservice.dto;


import java.util.List;

public class OrderDTO {

    private int cost;

    private List<ProductInOrderDTO> products;

    private String dateOfCreate;

    private String status;

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public List<ProductInOrderDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInOrderDTO> products) {
        this.products = products;
    }

    public String getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(String dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
