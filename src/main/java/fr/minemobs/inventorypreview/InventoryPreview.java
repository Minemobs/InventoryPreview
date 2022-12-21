package fr.minemobs.inventorypreview;

import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InventoryPreview {

    private AbstractInventoryInterface ui;

    InventoryPreview() {}

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int rows = -1;
        while(rows < 1 || rows > 6) {
            System.out.println("Enter the number of rows:");
            try {
                rows = sc.nextInt();
                if(rows < 1 || rows > 6) {
                    System.out.println("The number of rows must be between 1 and 6.");
                }
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Invalid input!");
            }
        }
        sc.nextLine();
        boolean exit = false;
        Item[][] slots = new Item[rows][9];
        InventoryPreview preview = new InventoryPreview();
        preview.ui = (args.length > 0 && args[0].equals("nogui") ? new FakeInterface(rows) : new InventoryInterface(rows));
        try {
            preview.ui.makeInterface();
        } catch(IOException e) {
            System.out.println("Could not create the UI");
            preview.ui = new FakeInterface(rows);
        }
        while(!exit) {
            if(slots.length == 0) break;
            System.out.printf("Enter the slots (0-%d) or \"border\" to fill the borders or \"exit\" to exit:%n", slots.length * 9 - 1);
            String line = sc.nextLine();
            if(line.equalsIgnoreCase("exit") || line.equals("q")) {
                exit = true;
            } else if(line.equalsIgnoreCase("border")) {
                preview.fillBorders(slots);
            } else {
                try {
                    int slot = Integer.parseInt(line);
                    if(slot < 0 || slot >= slots.length * 9) {
                        System.out.println("Invalid slot!");
                    } else {
                        System.out.println("Enter the item name (for example \"diamond_sword\"):");
                        String item = sc.nextLine();
                        int x = slot % 9;
                        int y = (int) Math.floor(slot / 9f);
                        try {
                            slots[y][x] = item.equalsIgnoreCase("null") ? null : Item.get(item);
                            preview.ui.setItem(x, y, slots[y][x]);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid item!");
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input!");
                    sc.nextLine();
                }
            }
            preview.ui.refresh();
        }
        sc.close();
        
        try {
            preview.ui.dispose();
        } catch(IOException e) {
            e.printStackTrace();
        }
        preview.generateCode(slots);
    }

    private void generateCode(Item[][] slots) {
        StringBuilder builder = new StringBuilder();
        boolean hasBorder = hasBorders(slots);
        if(hasBorder) {
            builder.append("""
                private void fillBorders(Inventory inv) {
                    for (int i = 0; i < inv.getSize(); i++) {
                        if(i % 9 == 0 || i % 9 == 8 || i < 9 || i > inv.getSize() - 9) {
                            ItemStack is = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                            ItemMeta meta = is.getItemMeta();
                            meta.setDisplayName(" ");
                            is.setItemMeta(meta);
                            inv.setItem(i, is);
                        }
                    }
                }
                """);
        }
        builder.append(String.format("Inventory inv = Bukkit.createInventory(null, %d);\n", slots.length * 9));
        if(hasBorder) builder.append("fillBorder(inv);\n");
        for (int y = 0; y < slots.length; y++) {
            for (int x = 0; x < slots[y].length; x++) {
                if(slots[y][x] != null && !slots[y][x].getName().equals("__border__")) {
                    builder.append(String.format("inv.setItem(%d, new ItemStack(Material.%s));", y * 9 + x, slots[y][x].getName().toUpperCase())).append("\n");
                }
            }
        }
        System.out.println(builder.substring(0, builder.length() - 1));
    }

    private boolean hasBorders(Item[][] slots) {
        return Arrays.stream(slots).flatMap(Arrays::stream).anyMatch(item -> item != null && item.getName().equals("__border__"));
    }

    private void fillBorders(Item[][] slots) {
        for (int y = 0; y < slots.length; y++) {
            for (int x = 0; x < slots[y].length; x++) {
                if (x == 0 || x == 8 || y == 0 || y == slots.length - 1) {
                    slots[y][x] = Item.BORDER;
                    ui.setItem(x, y, Item.BORDER);
                }
            }
        }
    }    
}