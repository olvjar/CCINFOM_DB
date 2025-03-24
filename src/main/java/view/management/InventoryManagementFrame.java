package view.management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import controller.InventoryController;

public class InventoryManagementFrame extends JFrame {
    private InventoryController controller = new InventoryController();

    public InventoryManagementFrame() {
        setTitle("Inventory Management");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        JButton addButton = new JButton("Add Item");
        JButton updateButton = new JButton("Update Item");
        JButton deleteButton = new JButton("Delete Item");
        JButton viewButton = new JButton("View Inventory");

        addButton.addActionListener(e -> controller.addInventory("P001", "Monitor", 10, "Available"));
        updateButton.addActionListener(e -> controller.updateInventory("P001", 15));
        deleteButton.addActionListener(e -> controller.deleteInventory("P001"));
        viewButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Viewing Inventory"));

        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(viewButton);
        add(panel);
    }
}
