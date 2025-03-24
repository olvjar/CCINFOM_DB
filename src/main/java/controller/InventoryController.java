package controller;

import model.entity.Inventory;
import model.service.InventoryService;
import java.util.List;

public class InventoryController {
    private InventoryService inventoryService = new InventoryService();

    public void addInventory(String productCode, String productName, int quantity, String status) {
        Inventory item = new Inventory(productCode, productName, quantity, status);
        inventoryService.addInventoryItem(item);
    }

    public void updateInventory(String productCode, int newQuantity) {
        inventoryService.updateInventoryItem(productCode, newQuantity);
    }

    public void deleteInventory(String productCode) {
        inventoryService.deleteInventoryItem(productCode);
    }

    public List<Inventory> viewInventory() {
        return inventoryService.getAllInventoryItems();
    }
}
