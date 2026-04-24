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

    private BufferedImage[] sprites = new BufferedImage[6];
    private int curAnimation = 0;
    private int frames = 0;
    private int maxFrames = 10;
    private int maxAnimation = 3;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;

        try {
            // Carrega a imagem diretamente.
            sprites[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_front.png"));
            sprites[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_back.png"));
            sprites[2] = ImageIO.read(getClass().getResourceAsStream("/player/player_right.png"));
            sprites[3] = ImageIO.read(getClass().getResourceAsStream("/player/player_right2.png"));
            sprites[4] = ImageIO.read(getClass().getResourceAsStream("/player/player_left.png"));
            sprites[5] = ImageIO.read(getClass().getResourceAsStream("/player/player_left2.png"));


        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do Player!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Arquivo não encontrado! Verifique o caminho e se a pasta está marcada como 'Resources Root'");
        }
    }

    public void tick() {

        boolean moved = false;

        if (right && World.isFree(x + speed, y)) {
            x += speed;
            moved = true;
        } else if (left && World.isFree(x - speed, y)) {
            x -= speed;
            moved = true;
        }
        if (up && World.isFree(x, y - speed)) {
            y -= speed;
            moved = true;
        } else if (down && World.isFree(x, y + speed)) {
            y += speed;
            moved = true;
        }

        //Lógica do movimento
        if(moved){
            frames++;
            if(frames >= maxFrames){
                frames = 0;
                curAnimation++;
                if(curAnimation >= maxAnimation){
                    curAnimation = 0;
                }
            }
        }

        Camera.x = Camera.clamp(x - (Game.WIDTH / 2), 0, (World.WIDTH * 16) -
                Game.WIDTH);
        Camera.y = Camera.clamp(y - (Game.HEIGHT / 2), 0, (World.HEIGHT * 16) -
                Game.HEIGHT);
    }

    public void render(Graphics g) {
        g.drawImage(sprites[curAnimation], x - Camera.x, y - Camera.y, null);
    }
}
