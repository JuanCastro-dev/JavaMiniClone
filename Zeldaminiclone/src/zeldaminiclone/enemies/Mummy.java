package zeldaminiclone.enemies;

import zeldaminiclone.Camera;
import zeldaminiclone.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Mummy extends Rectangle {

    private BufferedImage[] spritesFront = new BufferedImage[2];
    private BufferedImage[] spritesBack = new BufferedImage[2];
    private BufferedImage[] spritesRight = new BufferedImage[2];
    private BufferedImage[] spritesLeft = new BufferedImage[2];

    private int speed = 1;
    private Random rand;
    public int vida = 40;

    private float knockbackX = 0, knockbackY = 0;

    private BufferedImage[] curDirection = new BufferedImage[2];
    private int curAnimation = 0;
    private int frames = 0;
    private int maxFrames = 10;
    private int dirFrames = 0;
    private int maxDirFrames = 60;
    private int curDir = 0;

    public Mummy(int x, int y) {
        super(x, y, 16, 16);
        this.rand = new Random();
        curDir = rand.nextInt(4);
        curDirection = spritesFront;

        try {
            spritesFront[0] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_front1.png"));
            spritesFront[1] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_front2.png"));

            spritesBack[0] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_back1.png"));
            spritesBack[1] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_back2.png"));

            spritesRight[0] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_right1.png"));
            spritesRight[1] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_right2.png"));

            spritesLeft[0] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_left1.png"));
            spritesLeft[1] = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_left2.png"));

        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem dos inimigos!");
            e.printStackTrace();
        }
    }

    public void tick() {
        if (knockbackX != 0 || knockbackY != 0) {
            int nx = x + (int) knockbackX;
            int ny = y + (int) knockbackY;
            if (World.isFree(nx, y)) x = nx;
            if (World.isFree(x, ny)) y = ny;
            knockbackX *= 0.75f;
            knockbackY *= 0.75f;
            if (Math.abs(knockbackX) < 0.5f) knockbackX = 0;
            if (Math.abs(knockbackY) < 0.5f) knockbackY = 0;
            this.setBounds(x, y, 16, 16);
            return;
        }

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

    public void takeDamage(int damage, int playerDir) {
        vida -= damage;
        switch (playerDir) {
            case 0 -> knockbackY =  8;
            case 1 -> knockbackY = -8;
            case 2 -> knockbackX =  8;
            case 3 -> knockbackX = -8;
        }
    }

    public void render(Graphics g) {
        g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
    }
}
