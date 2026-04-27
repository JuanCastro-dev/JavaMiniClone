package zeldaminiclone;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Item extends Rectangle {

    private BufferedImage sprite;

    public Item(int x, int y, BufferedImage sprite) {
        super(x,y,16,16);
        this.sprite = sprite;
    }

    public void render(Graphics g){
        g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
    }
}
