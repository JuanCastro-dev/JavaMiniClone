package zeldaminiclone;

import java.awt.*;

//Atributos e métodos do player
public class Player {

    public int x, y;
    public int speed = 2;

    public boolean right, left, up, down;

    public Player(int x, int y){
        this.x = x;
        this.y = y;
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
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 16, 16);
    }
}
