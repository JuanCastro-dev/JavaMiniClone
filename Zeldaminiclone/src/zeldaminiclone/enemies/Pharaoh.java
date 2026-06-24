package zeldaminiclone.enemies;

import zeldaminiclone.Camera;
import zeldaminiclone.Game;
import zeldaminiclone.Item;
import zeldaminiclone.World;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Pharaoh extends Rectangle {

    private BufferedImage[] spritesFront = new BufferedImage[2];
    private BufferedImage[] spritesBack  = new BufferedImage[2];
    private BufferedImage[] spritesRight = new BufferedImage[2];
    private BufferedImage[] spritesLeft  = new BufferedImage[2];

    private BufferedImage attackDown, attackUp;
    private BufferedImage[] attackRight = new BufferedImage[2];
    private BufferedImage[] attackLeft  = new BufferedImage[2];
    private BufferedImage powerSprite;
    private BufferedImage deathSprite;

    private BufferedImage[] curDirection;
    private int curAnimation = 0;
    private int frames = 0;
    private int maxFrames = 10;
    private int dirFrames = 0;
    private int maxDirFrames = 120; // dobrado para reduzir velocidade de troca de direção
    private int curDir = 0;
    private Random rand = new Random();

    // delay de movimento para reduzir velocidade pela metade
    private int moveDelay = 0;

    public int vida = 300;
    private int lastPowerThreshold = 300;

    private float knockbackX = 0, knockbackY = 0;

    private boolean normalAttacking = false;
    private int normalAttackFrames = 0;
    private static final int NORMAL_ATTACK_DURATION = 120; // 2 segundos a 60fps
    private static final int ATTACK_RANGE = 20;
    private static final int ATTACK_COOLDOWN = 180; // dobrado
    private int attackCooldownFrames = 0;
    private BufferedImage curNormalAttackSprite;
    private BufferedImage[] curNormalAttackAnim; // sprites da animação de ataque (pode ter 1 ou 2 frames)

    private boolean powerAttacking = false;
    private int powerFrames = 0;
    private static final int POWER_DURATION = 40; // dobrado
    private int orbShotCount = 0;
    private int orbShotDelay = 0;
    private static final int ORB_SHOT_INTERVAL = 60;

    public boolean dead = false;
    private int deathFrames = 0;
    private static final int DEATH_DURATION = 90;
    private boolean droppedItem = false;

    public ArrayList<Orb> orbs = new ArrayList<>();

    public Pharaoh(int x, int y) {
        super(x, y, 16, 16);
        curDirection = spritesFront;

        try {
            spritesFront[0] = ImageIO.read(getClass().getResourceAsStream("/farao/farao_front1.png"));
            spritesFront[1] = ImageIO.read(getClass().getResourceAsStream("/farao/farao_front2.png"));
            spritesBack[0]  = ImageIO.read(getClass().getResourceAsStream("/farao/farao_up1.png"));
            spritesBack[1]  = ImageIO.read(getClass().getResourceAsStream("/farao/farao_up2.png"));
            spritesRight[0] = ImageIO.read(getClass().getResourceAsStream("/farao/farao_right1.png"));
            spritesRight[1] = ImageIO.read(getClass().getResourceAsStream("/farao/farao_right2.png"));
            spritesLeft[0]  = ImageIO.read(getClass().getResourceAsStream("/farao/farao_left1.png"));
            spritesLeft[1]  = ImageIO.read(getClass().getResourceAsStream("/farao/farao_left2.png"));
            attackDown      = ImageIO.read(getClass().getResourceAsStream("/farao/farao_attack_down.png"));
            attackUp        = ImageIO.read(getClass().getResourceAsStream("/farao/farao_attack_up.png"));
            attackRight[0]  = ImageIO.read(getClass().getResourceAsStream("/farao/farao_attack_right1.png"));
            attackRight[1]  = ImageIO.read(getClass().getResourceAsStream("/farao/farao_attack_right2.png"));
            attackLeft[0]   = ImageIO.read(getClass().getResourceAsStream("/farao/farao_attack_left1.png"));
            attackLeft[1]   = ImageIO.read(getClass().getResourceAsStream("/farao/farao_attack_left2.png"));
            powerSprite     = ImageIO.read(getClass().getResourceAsStream("/farao/farao_power.png"));
            deathSprite     = ImageIO.read(getClass().getResourceAsStream("/farao/farao_death.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar sprites do Pharaoh!");
            e.printStackTrace();
        }
    }

    public void tick() {
        // animação de morte
        if (dead) {
            deathFrames++;
            if (!droppedItem) {
                droppedItem = true;
                try {
                    BufferedImage staffSprite = ImageIO.read(new File("resources/itens/staff.png"));
                    World.items.add(new Item(x, y, staffSprite, "staff"));
                } catch (Exception e) {
                    System.err.println("Erro ao carregar staff.png");
                }
            }
            return;
        }

        // orbes
        for (int i = orbs.size() - 1; i >= 0; i--) {
            orbs.get(i).tick();
            if (!orbs.get(i).active) orbs.remove(i);
        }

        // knockback
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

        // poder especial
        int threshold = (vida / 50) * 50;
        if (threshold < lastPowerThreshold && !powerAttacking) {
            lastPowerThreshold = threshold;
            triggerPower();
        }

        if (powerAttacking) {
            powerFrames++;
            if (orbShotCount < 2) {
                orbShotDelay++;
                if (orbShotDelay >= ORB_SHOT_INTERVAL) {
                    orbShotDelay = 0;
                    shootOrbs();
                    orbShotCount++;
                }
            }
            if (orbShotCount >= 2 && powerFrames >= POWER_DURATION + ORB_SHOT_INTERVAL * 2) {
                powerAttacking = false;
                powerFrames = 0;
                orbShotCount = 0;
                orbShotDelay = 0;
            }
            return;
        }

        // ataque normal
        if (normalAttacking) {
            normalAttackFrames++;
            if (normalAttackFrames >= NORMAL_ATTACK_DURATION) {
                normalAttacking = false;
                normalAttackFrames = 0;
                attackCooldownFrames = 0;
            }
            return;
        }

        if (attackCooldownFrames < ATTACK_COOLDOWN) attackCooldownFrames++;

        int px = Game.player.x, py = Game.player.y;
        int dist = (int) Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2));
        if (dist <= ATTACK_RANGE && attackCooldownFrames >= ATTACK_COOLDOWN) {
            triggerNormalAttack(px, py);
            return;
        }

        // movimento com delay (metade da velocidade)
        moveDelay++;
        if (moveDelay < 2) {
            this.setBounds(x, y, 16, 16);
            return;
        }
        moveDelay = 0;

        dirFrames++;
        if (dirFrames >= maxDirFrames) {
            dirFrames = 0;
            curDir = rand.nextInt(4);
        }

        if      (curDir == 0 && World.isFree(x + 1, y)) { x += 1; moveAnimation(spritesRight); }
        else if (curDir == 1 && World.isFree(x - 1, y)) { x -= 1; moveAnimation(spritesLeft); }
        else if (curDir == 2 && World.isFree(x, y - 1)) { y -= 1; moveAnimation(spritesBack); }
        else if (curDir == 3 && World.isFree(x, y + 1)) { y += 1; moveAnimation(spritesFront); }

        this.setBounds(x, y, 16, 16);
    }

    private void triggerNormalAttack(int px, int py) {
        normalAttacking = true;
        normalAttackFrames = 0;
        int dx = px - x, dy = py - y;
        if (Math.abs(dx) >= Math.abs(dy)) {
            curNormalAttackAnim = dx >= 0 ? attackRight : attackLeft;
        } else {
            curNormalAttackAnim = dy >= 0 ? new BufferedImage[]{attackDown} : new BufferedImage[]{attackUp};
        }
        curNormalAttackSprite = curNormalAttackAnim[0];
        Game.player.vida -= 15;
        if (Game.player.vida <= 0) Game.gameOver();
    }

    private void triggerPower() {
        powerAttacking = true;
        powerFrames = 0;
        orbShotCount = 0;
        orbShotDelay = ORB_SHOT_INTERVAL;
    }

    private void shootOrbs() {
        int cx = x + 8, cy = y + 8;
        orbs.add(new Orb(cx, cy,  1,  0));
        orbs.add(new Orb(cx, cy, -1,  0));
        orbs.add(new Orb(cx, cy,  0,  1));
        orbs.add(new Orb(cx, cy,  0, -1));
    }

    public void takeDamage(int damage, int playerDir) {
        vida -= damage;
        if (vida <= 0) {
            vida = 0;
            dead = true;
        }
        switch (playerDir) {
            case 0 -> knockbackY =  8;
            case 1 -> knockbackY = -8;
            case 2 -> knockbackX =  8;
            case 3 -> knockbackX = -8;
        }
    }

    public boolean isDeathAnimationDone() {
        return dead && deathFrames >= DEATH_DURATION;
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
        for (Orb orb : orbs) orb.render(g);
        if (dead) {
            if (deathSprite != null)
                g.drawImage(deathSprite, x - Camera.x, y - Camera.y, null);
        } else if (powerAttacking) {
            g.drawImage(powerSprite, x - Camera.x, y - Camera.y, null);
        } else if (normalAttacking && curNormalAttackAnim != null) {
            int frameIndex = normalAttackFrames < NORMAL_ATTACK_DURATION / 2 ? 0 : Math.min(1, curNormalAttackAnim.length - 1);
            g.drawImage(curNormalAttackAnim[frameIndex], x - Camera.x, y - Camera.y, null);
        } else {
            g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
        }
    }
}
