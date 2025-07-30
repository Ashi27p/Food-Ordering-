import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;


class MenuItem {
    int id;
    String name;
    int price;

    MenuItem(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String toString() {
        return name + " - ₹" + price;
    }
}

class Order {
    int orderId;
    String customerName;
    List<MenuItem> items;
    int totalAmount;

    Order(int orderId, String customerName, List<MenuItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = items;
        this.totalAmount = items.stream().mapToInt(i -> i.price).sum();
    }

    public String toString() {
        return "Order #" + orderId + " | " + customerName + " | Total: ₹" + totalAmount;
    }
}

class DeliveryAgent implements Comparable<DeliveryAgent> {
    String name;
    int distance;

    DeliveryAgent(String name, int distance) {
        this.name = name;
        this.distance = distance;
    }

    public int compareTo(DeliveryAgent other) {
        return Integer.compare(this.distance, other.distance);
    }

    public String toString() {
        return name + " (Dist: " + distance + "km)";
    }
}

public class FoodDeliveryGUI extends JFrame {
    Map<Integer, MenuItem> menu = new HashMap<>();
    Queue<Order> orderQueue = new LinkedList<>();
    PriorityQueue<DeliveryAgent> agents = new PriorityQueue<>();
    List<Order> delivered = new ArrayList<>();
    int orderId = 1;

    JList<MenuItem> menuList;
    DefaultListModel<MenuItem> menuModel = new DefaultListModel<>();
    JTextArea output = new JTextArea(10, 30);

    public FoodDeliveryGUI() {
        setTitle("🍔 Food Delivery System");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Setup menu
        addMenuItems();
        menuList = new JList<>(menuModel);
        JScrollPane scroll = new JScrollPane(menuList);

        JButton placeOrderBtn = new JButton("Place Order");
        JButton processOrderBtn = new JButton("Process Order");
        JButton viewDeliveredBtn = new JButton("View Delivered Orders");

        placeOrderBtn.addActionListener(e -> placeOrder());
        processOrderBtn.addActionListener(e -> processOrder());
        viewDeliveredBtn.addActionListener(e -> viewDelivered());

        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(scroll);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 5, 5));
        buttons.add(placeOrderBtn);
        buttons.add(processOrderBtn);
        buttons.add(viewDeliveredBtn);
        topPanel.add(buttons);

        add(topPanel, BorderLayout.NORTH);
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        initAgents();
        setVisible(true);
    }

    void addMenuItems() {
        String[][] items = {
            {"Pizza", "250"}, {"Burger", "120"}, {"Pasta", "180"},
            {"Fries", "100"}, {"Biryani", "200"}, {"Sandwich", "90"},
            {"Coffee", "60"}, {"Juice", "70"}, {"Ice Cream", "80"}
        };
        for (int i = 0; i < items.length; i++) {
            MenuItem m = new MenuItem(i + 1, items[i][0], Integer.parseInt(items[i][1]));
            menu.put(m.id, m);
            menuModel.addElement(m);
        }
    }

    void initAgents() {
        agents.add(new DeliveryAgent("Raj", 2));
        agents.add(new DeliveryAgent("Asha", 1));
        agents.add(new DeliveryAgent("John", 3));
    }

    void placeOrder() {
       List<MenuItem> selected = menuList.getSelectedValuesList();
    if (selected.isEmpty()) {
        output.setText("⚠️ Please select at least one item.");
        return;
    }

    String name = JOptionPane.showInputDialog(this, "Enter customer name:");
    if (name == null || name.trim().isEmpty()) {
        output.setText("⚠️ Customer name cannot be empty.");
        return;
    }

    Order order = new Order(orderId++, name.trim(), selected);
    orderQueue.offer(order);
    output.setText("✅ Order placed:\n" + order);    }

    void processOrder() {
        if (orderQueue.isEmpty()) {
            output.setText("⏳ No pending orders.");
            return;
        }
        if (agents.isEmpty()) {
            output.setText("🚫 No delivery agents available.");
            return;
        }

        Order order = orderQueue.poll();
        DeliveryAgent agent = agents.poll();

        output.setText("🚚 Processing " + order + "\nAssigned to: " + agent);
        delivered.add(order);

        // Simulate agent coming back
        agents.add(new DeliveryAgent(agent.name, agent.distance + 1));
    }

    void viewDelivered() {
        if (delivered.isEmpty()) {
            output.setText("📭 No delivered orders yet.");
        } else {
            StringBuilder sb = new StringBuilder("📦 Delivered Orders:\n");
            for (Order o : delivered) sb.append(o).append("\n");
            output.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        new FoodDeliveryGUI();
    }
}
