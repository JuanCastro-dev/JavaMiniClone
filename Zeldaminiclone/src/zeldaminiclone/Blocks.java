package zeldaminiclone;

import java.awt.*;

//Classe que representa os blocos do cenário
public class Blocks extends Rectangle {

    public Blocks(int x, int y){
        super(x,y,16,16);
    }

    public void render(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(x - Camera.x, y - Camera.y, width, height);
    }
}
