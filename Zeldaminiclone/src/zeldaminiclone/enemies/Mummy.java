package zeldaminiclone.enemies;

import zeldaminiclone.Camera;
import zeldaminiclone.Game;
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

    private BufferedImage attackDown, attackUp, attackLeft, attackRight;

    private int speed = 1;
    private int moveDelay = 0;
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

    private boolean attacking = false;
    private int attackFrames = 0;
    private static final int ATTACK_DURATION = 20;
    private BufferedImage curAttackSprite;

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

            attackDown  = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_attack_down.png"));
            attackUp    = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_attack_up.png"));
            attackRight = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_attack_right.png"));
            attackLeft  = ImageIO.read(getClass().getResourceAsStream("/mummy/mummy_attack_left.png"));

        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem dos inimigos!");
            e.printStackTrace();
        }
    }

    public void tick() {
        if (attacking) {
            attackFrames++;
            if (attackFrames >= ATTACK_DURATION) {
                attacking = false;
                attackFrames = 0;
            }
        }

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

        moveDelay++;
        if (moveDelay < 2) {
            this.setBounds(x, y, 16, 16);
            return;
        }
        moveDelay = 0;

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

        // verifica colisão com o player para disparar animação de ataque
        if (this.intersects(Game.player)) {
            triggerAttack();
        }

        this.setBounds(x, y, 16, 16);
    }

    private void triggerAttack() {
        if (attacking) return;
        attacking = true;
        attackFrames = 0;
        int px = Game.player.x, py = Game.player.y;
        int dx = px - x, dy = py - y;
        if (Math.abs(dx) >= Math.abs(dy)) {
            curAttackSprite = dx >= 0 ? attackRight : attackLeft;
        } else {
            curAttackSprite = dy >= 0 ? attackDown : attackUp;
        }
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
        if (attacking && curAttackSprite != null) {
            g.drawImage(curAttackSprite, x - Camera.x, y - Camera.y, null);
        } else {
            g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
        }
    }
}
