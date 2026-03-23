package zeldaminiclone;

import java.awt.*;
import java.awt.image.BufferedImage;

//Classe que representa os blocos do cenário
public class Blocks extends Rectangle {

    public Blocks(int x, int y){
        super(x,y,16,16);
    }

    private BufferedImage texture;

    public Blocks(){
        SpriteSheet sheet = new SpriteSheet("resources/spritesheet.png");
        texture = sheet.getSprite(16, 0, 16, 16); // segundo sprite da primeira linha
    }

    public void render(Graphics g) {
        g.drawImage(texture, x, y, null);
    }
}
