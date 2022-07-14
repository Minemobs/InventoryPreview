package fr.minemobs.inventorypreview;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Item {

    private final String name;
    private final BufferedImage image;

    private Item(String name) {
        this.name = name;
        BufferedImage img;
        try(InputStream is = Objects.requireNonNull(Item.class.getResourceAsStream("/assets/renders/" + name + ".png"))) {
            img = ImageIO.read(is);
        } catch (IOException | NullPointerException e) {
            try(InputStream is = getClass().getResourceAsStream("/assets/items/" + name + ".png")) {
                img = ImageIO.read(Objects.requireNonNull(is));
            } catch (Exception e1) {
                try(InputStream is = getClass().getResourceAsStream("/assets/blocks/" + name + ".png")) {
                    img = ImageIO.read(Objects.requireNonNull(is));
                } catch (Exception e2) {
                    throw new IllegalArgumentException("This item does not exist: " + name);
                }
            }
        }
        this.image = img;
    }

    public static Item get(String name) throws IllegalArgumentException {
        return new Item(name);
    }

    public String getName() {
        return name;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                '}';
    }
}
