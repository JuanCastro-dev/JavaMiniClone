package zeldaminiclone;

import java.awt.*;
import java.awt.image.BufferedImage;

//Atributos e métodos do player
public class Player {

    public int x, y;
    public int speed = 2;

    public boolean right, left, up, down;

    private BufferedImage sprite;

    public Player(int x, int y){
        this.x = x;
        this.y = y;
        SpriteSheet sheet = new SpriteSheet("resources/spritesheet.png");
        sprite = sheet.getSprite(0, 0, 16, 16);
    }

    public void tick() {
        if (right && World.isFree(x + speed, y)) {
            x += speed;
        } else if (left && World.isFree(x - speed, y)) {
            x -= speed;
        }
        if (up && World.isFree(x, y - speed)) {
            y -= speed;
        } else if (down && World.isFree(x, y + speed)) {
            y += speed;
        }
    }

    public void render(Graphics g) {
        g.drawImage(sprite, x, y, null);
    }
}
