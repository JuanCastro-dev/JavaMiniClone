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

    public void tick(){
        if(right) x += speed;
        else if (left) x -= speed;

        if (up) y -= speed;
        else if (down) y+= speed;
    }

    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, 16, 16);
    }
}
