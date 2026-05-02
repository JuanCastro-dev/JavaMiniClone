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
    private int frames = 0;
    private int maxFrames = 10;
    private int dirFrames = 0;
    private int maxDirFrames = 60;
    private int curDir = 0;

    public Enemy(int x, int y) {
        super(x, y, 16, 16);
        this.rand = new Random();
        curDir = rand.nextInt(4);
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

    public void tick() {
        dirFrames++;
        if (dirFrames >= maxDirFrames) {
            dirFrames = 0;
            curDir = rand.nextInt(4);
        }

        if (curDir == 0 && World.isFree(x + speed, y)) {
            x += speed;
            moveAnimation(spritesRight);
        } else if (curDir == 1 && World.isFree(x - speed, y)) {
            x -= speed;
            moveAnimation(spritesLeft);
        } else if (curDir == 2 && World.isFree(x, y - speed)) {
            y -= speed;
            moveAnimation(spritesBack);
        } else if (curDir == 3 && World.isFree(x, y + speed)) {
            y += speed;
            moveAnimation(spritesFront);
        }

        this.setBounds(x, y, 16, 16);
    }

    private void moveAnimation(BufferedImage[] sprites) {
        frames++;
        curDirection = sprites;
        if (frames >= maxFrames) {
            frames = 0;
            curAnimation = (curAnimation + 1) % 2;
        }
    }

    public void render(Graphics g) {
        g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
    }
}
