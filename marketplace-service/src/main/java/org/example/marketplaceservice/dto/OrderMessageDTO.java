package org.example.marketplaceservice.dto;

public class OrderMessageDTO {

    private int id;

    private String status;

    public OrderMessageDTO(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public OrderMessageDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
