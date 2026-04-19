package zeldaminiclone;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//Classe que representa os blocos do cenário
public class Blocks extends Rectangle {

    public Blocks(int x, int y){
        super(x,y,16,16);
    }

    private BufferedImage texture;

    public Blocks(){
        try{
            texture = ImageIO.read(new File("resources/player/player_front.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do jogador");
            throw new RuntimeException(e);
        }
    }

    public void render(Graphics g) {
        g.drawImage(texture, x - Camera.x, y - Camera.y, null);
    }
}
