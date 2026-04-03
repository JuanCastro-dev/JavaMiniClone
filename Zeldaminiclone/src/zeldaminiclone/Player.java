package zeldaminiclone;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

//Atributos e métodos do player
public class Player {

    public int x, y;
    public int speed = 2;

    public boolean right, left, up, down;

    private BufferedImage sprite;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            // Carrega a imagem diretamente.
            // Verifique se o caminho está correto (corrigi 'palyer' para 'player')
            sprite = ImageIO.read(getClass().getResourceAsStream("/player/player_front.png"));

        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do Player!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Arquivo não encontrado! Verifique o caminho e se a pasta está marcada como 'Resources Root'");
        }
    }

    public void tick() {
        if (right && World.isFree(x + speed, y)) {
            x += speed;\
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
