package zeldaminiclone;

import zeldaminiclone.resources.Sounds;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//Atributos e métodos do player
public class Player extends Rectangle {

    public int x, y;
    public int speed = 2;
    public int vida = 50;
    public int maxVida = 50;
    public int itensColetados = 0;

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

    public static int score = 0;
    public static int highScore = 0;

    private boolean damaged = false;
    private int damageFrames = 0;

    private boolean takingItem = false;
    private int takingItemFrames = 0;
    private BufferedImage takingItemSprite;

    private boolean attacking = false;
    private int attackFrames = 0;
    private static final int ATTACK_DURATION = 20;
    private BufferedImage attackSprite;
    private BufferedImage attackUp, attackDown, attackLeft, attackRight;
    private int lastDir = 0; // 0=down, 1=up, 2=right, 3=left

    public boolean hasSword = false;
    private int swordHintFrames = 0;
    private static final int SWORD_HINT_DURATION = 300;

    public boolean hasStaff = false;
    public java.util.ArrayList<zeldaminiclone.enemies.Orb> playerOrbs = new java.util.ArrayList<>();
    private int staffCooldown = 0;
    private static final int STAFF_COOLDOWN = 60;

    public java.util.ArrayList<BufferedImage> inventory = new java.util.ArrayList<>();

    public Player(int x, int y) {
        super(x, y, 16, 16);
        this.x = x;
        this.y = y;
        curDirection = spritesFront;

        try {
            spritesFront[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_front.png"));
            spritesFront[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_front2.png"));

            spritesBack[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_back.png"));
            spritesBack[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_back2.png"));

            spritesRight[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_right.png"));
            spritesRight[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_right2.png"));

            spritesLeft[0] = ImageIO.read(getClass().getResourceAsStream("/player/player_left.png"));
            spritesLeft[1] = ImageIO.read(getClass().getResourceAsStream("/player/player_left2.png"));

            takingItemSprite = ImageIO.read(getClass().getResourceAsStream("/player/player_taking_item.png"));
            attackDown  = ImageIO.read(getClass().getResourceAsStream("/player/attack_down.png"));
            attackUp    = ImageIO.read(getClass().getResourceAsStream("/player/attack_up.png"));
            attackRight = ImageIO.read(getClass().getResourceAsStream("/player/attack_right.png"));
            attackLeft  = ImageIO.read(getClass().getResourceAsStream("/player/attack_left.png"));
            attackSprite = attackDown;

        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do Player!");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Arquivo não encontrado! Verifique o caminho e se a pasta está marcada como 'Resources Root'");
        }
    }

    public void tick() throws IOException {

        if (takingItem || attacking) {
            if (attacking) {
                attackFrames++;
                if (attackFrames >= ATTACK_DURATION) {
                    attacking = false;
                    attackFrames = 0;
                }
            } else {
                takingItemFrames++;
                if (takingItemFrames >= 120) {
                    takingItem = false;
                    takingItemFrames = 0;
                }
            }
            return;
        }

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

        if (movedRight)  { movePlayerAnimation(spritesRight); lastDir = 2; }
        if (movedLeft)   { movePlayerAnimation(spritesLeft);  lastDir = 3; }
        if (movedUp)     { movePlayerAnimation(spritesBack);  lastDir = 1; }
        if (movedDown)   { movePlayerAnimation(spritesFront); lastDir = 0; }

        Camera.x = Camera.clamp(x - (Game.WIDTH / 2), 0, (World.WIDTH * 16) - Game.WIDTH);
        Camera.y = Camera.clamp(y - (Game.HEIGHT / 2), 0, (World.HEIGHT * 16) - Game.HEIGHT);

        this.setBounds(x, y, 16, 16);

        // atualiza orbes do staff
        if (staffCooldown > 0) staffCooldown--;
        for (int i = playerOrbs.size() - 1; i >= 0; i--) {
            playerOrbs.get(i).tick();
            if (!playerOrbs.get(i).active) playerOrbs.remove(i);
        }

        for (int i = 0; i < World.items.size(); i++) {
            Item item = World.items.get(i);
            if (this.intersects(item)) {
                World.items.remove(item);
                score += 100;
                i--;
                itensColetados++;
                vida += 25;
                if (item.type.equals("heart")) {
                    new Sounds("resources/sounds/nice.wav").play();
                } else if (item.type.equals("sword")) {
                    new Sounds("resources/sounds/get_item.wav").play();
                    takingItem = true;
                    takingItemFrames = 0;
                    hasSword = true;
                    swordHintFrames = SWORD_HINT_DURATION;
                    inventory.add(item.getSprite());
                } else if (item.type.equals("armor")) {
                    new Sounds("resources/sounds/get_item.wav").play();
                    takingItem = true;
                    takingItemFrames = 0;
                    maxVida = 100;
                    vida = maxVida;
                    inventory.add(item.getSprite());
                } else if (item.type.equals("staff")) {
                    new Sounds("resources/sounds/get_item.wav").play();
                    takingItem = true;
                    takingItemFrames = 0;
                    hasStaff = true;
                    inventory.add(item.getSprite());
                }
                if (vida > maxVida) vida = maxVida;
                break;
            }
        }

        if (!damaged) {
            for (Enemy e : World.enemies) {
                if (this.intersects(e)) {
                    vida -= 1;
                    damaged = true;
                    damageFrames = 0;
                    new Sounds("resources/sounds/oof.wav").play();
                    if (vida == 0) Game.gameOver();
                    break;
                }
            }
            if (!damaged) {
                for (zeldaminiclone.enemies.Mummy m : World.mummies) {
                    if (this.intersects(m)) {
                        vida -= 8;
                        damaged = true;
                        damageFrames = 0;
                        new Sounds("resources/sounds/oof.wav").play();
                        if (vida == 0) Game.gameOver();
                        break;
                    }
                }
            }
        } else {
            damageFrames++;
            if (damageFrames >= 60) {
                damaged = false;
                damageFrames = 0;
            }
        }
    }

    public void attack() {
        if (!hasSword || attacking || takingItem) return;
        attacking = true;
        attackFrames = 0;
        attackSprite = switch (lastDir) {
            case 1 -> attackUp;
            case 2 -> attackRight;
            case 3 -> attackLeft;
            default -> attackDown;
        };
        int reach = 5;
        Rectangle hitbox = switch (lastDir) {
            case 1 -> new Rectangle(x, y - reach, 16, 16 + reach);
            case 2 -> new Rectangle(x, y, 16 + reach, 16);
            case 3 -> new Rectangle(x - reach, y, 16 + reach, 16);
            default -> new Rectangle(x, y, 16, 16 + reach);
        };
        for (int i = World.enemies.size() - 1; i >= 0; i--) {
            Enemy e = World.enemies.get(i);
            if (hitbox.intersects(e)) {
                e.takeDamage(10, lastDir);
                if (e.vida <= 0) {
                    World.enemies.remove(i);
                    score += 200;
                }
            }
        }
        for (int i = World.mummies.size() - 1; i >= 0; i--) {
            zeldaminiclone.enemies.Mummy m = World.mummies.get(i);
            if (hitbox.intersects(m)) {
                m.takeDamage(10, lastDir);
                if (m.vida <= 0) {
                    World.mummies.remove(i);
                    score += 200;
                }
            }
        }
        for (int i = World.pharaohs.size() - 1; i >= 0; i--) {
            zeldaminiclone.enemies.Pharaoh p = World.pharaohs.get(i);
            if (hitbox.intersects(p)) {
                p.takeDamage(10, lastDir);
                if (p.vida <= 0) {
                    World.pharaohs.remove(i);
                    score += 1000;
                }
            }
        }
    }

    public void staffAttack() {
        if (!hasStaff || staffCooldown > 0 || takingItem || attacking) return;
        staffCooldown = STAFF_COOLDOWN;
        int cx = x + 8, cy = y + 8;
        playerOrbs.add(new zeldaminiclone.enemies.Orb(cx, cy,  1,  0, true));
        playerOrbs.add(new zeldaminiclone.enemies.Orb(cx, cy, -1,  0, true));
        playerOrbs.add(new zeldaminiclone.enemies.Orb(cx, cy,  0,  1, true));
        playerOrbs.add(new zeldaminiclone.enemies.Orb(cx, cy,  0, -1, true));
    }

    public void renderSwordHint(Graphics g) {
        if (swordHintFrames <= 0) return;
        swordHintFrames--;
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 6));
        String msg = "Pressione F para atacar!";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (Game.WIDTH - msgWidth) / 2, Game.HEIGHT - 6);
    }

    public void render(Graphics g) {
        if (takingItem) {
            g.drawImage(takingItemSprite, x - Camera.x, y - Camera.y, null);
        } else if (attacking) {
            g.drawImage(attackSprite, x - Camera.x, y - Camera.y, null);
        } else if (vida > 0 && (!damaged || (damageFrames % 5 == 0))) {
            g.drawImage(curDirection[curAnimation], x - Camera.x, y - Camera.y, null);
        }
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
