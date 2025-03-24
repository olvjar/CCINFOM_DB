package model.service;

import java.util.ArrayList;
import java.util.List;
import model.entity.Inventory;

public class InventoryService {
    private List<Inventory> inventoryList = new ArrayList<>();

    public void addInventoryItem(Inventory item) {
        inventoryList.add(item);
    }

    public void updateInventoryItem(String productCode, int newQuantity) {
        for (Inventory item : inventoryList) {
            if (item.getProductCode().equals(productCode)) {
                item.setQuantityInStock(newQuantity);
                break;
            }
        }
    }

    public void deleteInventoryItem(String productCode) {
        inventoryList.removeIf(item -> item.getProductCode().equals(productCode));
    }

    public List<Inventory> getAllInventoryItems() {
        return inventoryList;
    }
}
