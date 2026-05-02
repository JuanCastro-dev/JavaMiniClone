package zeldaminiclone;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Rectangle {

    private BufferedImage[] spritesFront = new BufferedImage[2];
    private BufferedImage[] spritesBack = new BufferedImage[2];
    private BufferedImage[] spritesRight = new BufferedImage[2];
    private BufferedImage[] spritesLeft = new BufferedImage[2];

    private int speed = 1;
    private Random rand;

    private BufferedImage[] curDirection = new BufferedImage[2];
    private int curAnimation = 0;

    public Enemy(int x, int y) {
        super(x, y, 16, 16);
        this.rand = new Random();
        curDirection = spritesFront;

        try {
            spritesFront[0] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_front.png"));
            spritesFront[1] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_front2.png"));

            spritesBack[0] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_back.png"));
            spritesBack[1] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_back2.png"));

            spritesRight[0] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_right.png"));
            spritesRight[1] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_right2.png"));

            spritesLeft[0] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_left.png"));
            spritesLeft[1] = ImageIO.read(getClass().getResourceAsStream("/skeleton/skeleton_left2.png"));

        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem dos inimigos!");
            e.printStackTrace();
        }
    }

    public void tick(){
        int dir = rand.nextInt(4); // 0 = direita, 1 = esquerda, 2 = cima, 3 = baixo
        if (dir == 0 && World.isFree(x + speed, y)) x += speed;
        else if (dir == 1 && World.isFree(x - speed, y)) x -= speed;
        else if (dir == 2 && World.isFree(x, y - speed)) y -= speed;
        else if (dir == 3 && World.isFree(x, y + speed)) y += speed;
    }

    public void render(Graphics g) {
        g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
    }
}
