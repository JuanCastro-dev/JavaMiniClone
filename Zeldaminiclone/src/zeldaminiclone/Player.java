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

    private BufferedImage[] spritesFront = new BufferedImage[2];
    private BufferedImage[] spritesBack = new BufferedImage[2];
    private BufferedImage[] spritesRight = new BufferedImage[2];
    private BufferedImage[] spritesLeft = new BufferedImage[2];

    //Vetor que determina a posição atual
    private BufferedImage[] curDirection = new BufferedImage[2];

    private int curAnimation = 0;
    private int frames = 0;
    private int maxFrames = 10;
    private int maxAnimation = 2;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        curDirection = spritesFront;

        try {
            // Carrega a imagem diretamente.
            spritesFront[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_front.png"));
            spritesFront[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_front2.png"));

            spritesBack[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_back.png"));
            spritesBack[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_back2.png"));

            spritesRight[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_right.png"));
            spritesRight[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_right2.png"));

            spritesLeft[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_left.png"));
            spritesLeft[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_left2.png"));


        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do Player!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Arquivo não encontrado! Verifique o caminho e se a pasta está marcada como 'Resources Root'");
        }
    }

    public void tick() {

        boolean movedDown = false;
        boolean movedUp = false;
        boolean movedRight = false;
        boolean movedLeft = false;

        if (right && World.isFree(x + speed, y)) {
            x += speed;
            movedRight = true;
        } else if (left && World.isFree(x - speed, y)) {
            x -= speed;
            movedLeft = true;
        }
        if (up && World.isFree(x, y - speed)) {
            y -= speed;
            movedUp = true;
        } else if (down && World.isFree(x, y + speed)) {
            y += speed;
            movedDown = true;
        }

        if (movedRight){
            movePlayerAnimation(spritesRight);
        }
        if(movedLeft){
            movePlayerAnimation(spritesLeft);
        }
        if (movedUp){
            movePlayerAnimation(spritesBack);
        }
        if(movedDown){
            movePlayerAnimation(spritesFront);
        }

        Camera.x = Camera.clamp(x - (Game.WIDTH / 2), 0, (World.WIDTH * 16) -
                Game.WIDTH);
        Camera.y = Camera.clamp(y - (Game.HEIGHT / 2), 0, (World.HEIGHT * 16) -
                Game.HEIGHT);
    }

    public void render(Graphics g) {
        g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
    }

    private void movePlayerAnimation(BufferedImage[] sprites){
        frames++;
        curDirection = sprites;

        if(frames >= maxFrames){
            frames = 0;
            curAnimation++;
            if(curAnimation >= maxAnimation){
                curAnimation = 0;
            }
        }
    }
}
