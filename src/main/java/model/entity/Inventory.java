package model.entity;

public class Inventory {
    private String productCode;
    private String productName;
    private int quantityInStock;
    private String status;

    public Inventory(String productCode, String productName, int quantityInStock, String status) {
        this.productCode = productCode;
        this.productName = productName;
        this.quantityInStock = quantityInStock;
        this.status = status;
    }

    // Getters and setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Product Code: " + productCode + ", Product Name: " + productName +
               ", Quantity in Stock: " + quantityInStock + ", Status: " + status;
    }
}
