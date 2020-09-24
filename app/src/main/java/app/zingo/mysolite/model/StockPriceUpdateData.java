package app.zingo.mysolite.model;

import java.io.Serializable;

public class StockPriceUpdateData implements Serializable {

    private String itemName;
    private String oldPrice;
    private String newPrice;
    int itemId;


    public String getItemName ( ) {
        return itemName;
    }

    public void setItemName ( String itemName ) {
        this.itemName = itemName;
    }

    public String getOldPrice ( ) {
        return oldPrice;
    }

    public void setOldPrice ( String oldPrice ) {
        this.oldPrice = oldPrice;
    }

    public String getNewPrice ( ) {
        return newPrice;
    }

    public void setNewPrice ( String newPrice ) {
        this.newPrice = newPrice;
    }

    public int getItemId ( ) {
        return itemId;
    }

    public void setItemId ( int itemId ) {
        this.itemId = itemId;
    }
}
