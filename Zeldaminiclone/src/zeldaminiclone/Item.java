package zeldaminiclone;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Item extends Rectangle {

    private BufferedImage sprite;
    public String type;

    public Item(int x, int y, BufferedImage sprite, String type) {
        super(x,y,16,16);
        this.sprite = sprite;
        this.type = type;
    }

    public void render(Graphics g){
        g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}
