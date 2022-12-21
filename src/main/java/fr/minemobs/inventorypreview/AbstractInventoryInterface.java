package fr.minemobs.inventorypreview;

import java.io.IOException;

public abstract class AbstractInventoryInterface {

    protected final int rows;

    public AbstractInventoryInterface(int rows) {
        this.rows = rows;
    }

    public void makeInterface() throws IOException {}

    public void dispose() throws IOException {}

    public void setItem(int x, int y, Item item) {}

    public void refresh() {}
}
