package fr.minemobs.inventorypreview;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class InventoryInterface extends AbstractInventoryInterface {

    private int size;
    private BufferedImage top, bottom, image, slotImg;
    private Graphics2D graphics;
    private JLabel label;
    private JPanel panel;
    private JFrame frame;

    public InventoryInterface(int rows) {
        super(rows);
    }

    @Override
    public void makeInterface() throws IOException {
        try(InputStream slotIs = Objects.requireNonNull(InventoryPreview.class.getResourceAsStream("/slots.png"));
            InputStream topIs = Objects.requireNonNull(InventoryPreview.class.getResourceAsStream("/top.png"));
            InputStream bottomIs = Objects.requireNonNull(InventoryPreview.class.getResourceAsStream("/bottom.png"))) {
            slotImg = ImageIO.read(slotIs);
            top = ImageIO.read(topIs);
            bottom = ImageIO.read(bottomIs);
            size = slotImg.getHeight() * 2 * rows + top.getHeight() * 2 + bottom.getHeight() * 2;
            image = new BufferedImage(slotImg.getWidth() * 2, size, BufferedImage.TYPE_INT_ARGB);
            graphics = (Graphics2D) image.getGraphics();
            graphics.drawImage(top, 0, 0, top.getWidth() * 2, top.getHeight() * 2, null);
            for(int i = 0; i < rows; i++) {
                graphics.drawImage(slotImg, 0, top.getHeight() * 2 + i * (slotImg.getHeight() * 2), slotImg.getWidth() * 2, slotImg.getHeight() * 2, null);
            }
            graphics.drawImage(bottom, 0, top.getHeight() * 2 + rows * (slotImg.getHeight() * 2), bottom.getWidth() * 2, bottom.getHeight() * 2, null);
            
            label = new JLabel(new ImageIcon(image));
            panel = new JPanel();
            panel.add(label);
            frame = new JFrame();
            frame.setSize(new Dimension((int) (image.getWidth() * 1.15), size + 50));
            centerFrame(frame);
            frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
            frame.add(panel);
            frame.setVisible(true);
            frame.setAlwaysOnTop(true);
        }
    }

    @Override
    public void dispose() throws IOException {
        frame.removeAll();
        frame.dispose();
    }

    @Override
    public void setItem(int x, int y, Item item) {
        int x1 = 2 * (8 + 16 * x + 2 * x);
        int y1 = top.getHeight() * 2 + 2 + y * (slotImg.getHeight() * 2);
        Color color = graphics.getColor();
        Color c = new Color(0x8b8b8b);
        graphics.setColor(c);
        graphics.fillRect(x1, y1, 32, 32);
        graphics.setColor(color);
        if(item != null) {
            graphics.drawImage(item.getImage(), x1, y1, 32, 32, null);
        }
        
    }

    void centerFrame(final JFrame frame) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((size.width - frame.getWidth()) / 2, (size.height - frame.getHeight()) / 2);
    }

    @Override
    public void refresh() {
        var icon = new ImageIcon(image);
        icon.getImage().flush();
        label.setIcon(icon);
    }
}