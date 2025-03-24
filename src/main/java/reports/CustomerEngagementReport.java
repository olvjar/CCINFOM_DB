package reports;

import java.util.List;
import model.entity.Inventory;

public class CustomerEngagementReport {

    public void generateReport(List<Inventory> inventoryList) {
        int totalRequests = inventoryList.size();
        int completedRepairs = (int) inventoryList.stream().filter(item -> item.getStatus().equals("Completed")).count();
        double averageRepairCost = inventoryList.stream()
                .mapToDouble(item -> item.getQuantityInStock() * 100) // Sample calculation
                .average().orElse(0.0);

        System.out.println("Total Repair Requests: " + totalRequests);
        System.out.println("Completed Repairs: " + completedRepairs);
        System.out.println("Average Repair Cost: $" + averageRepairCost);
    }
}
