package fr.minemobs.inventorypreview;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class InventoryPreview {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int rows = -1;
        while(rows < 0 || rows > 6) {
            System.out.println("Enter the number of rows:");
            try {
                rows = sc.nextInt();
                if(rows < 0 || rows > 6) {
                    System.out.println("The number of rows must be between 0 and 6.");
                }
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Invalid input!");
            }
        }
        sc.nextLine();
        try(InputStream slotIs = Objects.requireNonNull(InventoryPreview.class.getResourceAsStream("/slots.png"));
            InputStream topIs = Objects.requireNonNull(InventoryPreview.class.getResourceAsStream("/top.png"));
            InputStream bottomIs = Objects.requireNonNull(InventoryPreview.class.getResourceAsStream("/bottom.png"))) {
            BufferedImage slotImg = ImageIO.read(slotIs);
            BufferedImage top = ImageIO.read(topIs);
            BufferedImage bottom = ImageIO.read(bottomIs);
            int size = slotImg.getHeight() * rows + top.getHeight() + bottom.getHeight();
            BufferedImage image = new BufferedImage(slotImg.getWidth(), size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            for(int i = 0; i < rows; i++) {
                graphics.drawImage(slotImg, 0, top.getHeight() + i * slotImg.getHeight(), null);
            }
            boolean exit = false;
            Item[][] slots = new Item[rows][9];
            while(!exit) {
                if(slots.length == 0) break;
                System.out.printf("Enter the slots (0-%d) or \"border\" to fill the borders or \"exit\" to exit:%n", slots.length * 9 - 1);
                String line = sc.nextLine();
                if(line.equalsIgnoreCase("exit") || line.equals("q")) {
                    exit = true;
                } else if(line.equalsIgnoreCase("border")) {
                    fillBorders(slots);
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
                                slots[y][x] = Item.get(item);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid item!");
                            }
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input!");
                        sc.nextLine();
                    }
                }
            }
            for(int i = 0; i < rows; i++) {
                for(int j = 0; j < 9; j++) {
                    Item item = slots[i][j];
                    if(item == null) continue;
                    int x = 8 + 16 * j + 2 * j;
                    int y = top.getHeight() + 1 + i * slotImg.getHeight();
                    graphics.drawImage(item.getImage(), x, y, null);
                }
            }
            graphics.drawImage(top, 0, 0, null);
            graphics.drawImage(bottom, 0, top.getHeight() + rows * slotImg.getHeight(), null);
            JLabel label = new JLabel(new ImageIcon(image));
            JPanel panel = new JPanel();
            panel.add(label);
            JFrame frame = new JFrame();
            frame.setSize(new Dimension((int) (image.getWidth() * 1.15), size + 50));
            centerFrame(frame);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(panel);
            frame.setVisible(true);
            frame.setAlwaysOnTop(true);
            ImageIO.write(image, "png", new File("inventory.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void fillBorders(Item[][] slots) {
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                if (j == 0 || j == 8 || i == 0 || i == slots.length - 1) {
                    slots[i][j] = Item.get("gray_stained_glass");
                }
            }
        }
    }

    private static void centerFrame(final JFrame frame) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((size.width - frame.getWidth()) / 2, (size.height - frame.getHeight()) / 2);
    }
}