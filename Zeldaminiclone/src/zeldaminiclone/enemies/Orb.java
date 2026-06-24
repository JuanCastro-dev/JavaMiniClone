package zeldaminiclone.enemies;

import zeldaminiclone.Camera;
import zeldaminiclone.Game;
import zeldaminiclone.World;
import zeldaminiclone.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Orb extends Rectangle {

    private int dx, dy;
    private static final int SPEED = 2;
    private static BufferedImage sprite;
    public boolean active = true;
    private boolean fromPlayer;

    static {
        try {
            sprite = ImageIO.read(Orb.class.getResourceAsStream("/farao/farao_ball.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar farao_ball.png");
        }
    }

    public Orb(int x, int y, int dx, int dy) {
        this(x, y, dx, dy, false);
    }

    public Orb(int x, int y, int dx, int dy, boolean fromPlayer) {
        super(x, y, 8, 8);
        this.dx = dx;
        this.dy = dy;
        this.fromPlayer = fromPlayer;
    }

    public void tick() {
        x += dx * SPEED;
        y += dy * SPEED;
        this.setBounds(x, y, 8, 8);

        if (x < 0 || y < 0 || x > World.WIDTH * 16 || y > World.HEIGHT * 16) {
            active = false;
            return;
        }

        if (fromPlayer) {
            for (int i = World.enemies.size() - 1; i >= 0; i--) {
                if (this.intersects(World.enemies.get(i))) {
                    World.enemies.get(i).takeDamage(10, 0);
                    if (World.enemies.get(i).vida <= 0) {
                        Player.score += 200;
                        World.enemies.remove(i);
                    }
                    active = false;
                    return;
                }
            }
            for (int i = World.mummies.size() - 1; i >= 0; i--) {
                if (this.intersects(World.mummies.get(i))) {
                    World.mummies.get(i).takeDamage(10, 0);
                    if (World.mummies.get(i).vida <= 0) {
                        Player.score += 200;
                        World.mummies.remove(i);
                    }
                    active = false;
                    return;
                }
            }
            for (int i = World.pharaohs.size() - 1; i >= 0; i--) {
                if (this.intersects(World.pharaohs.get(i))) {
                    World.pharaohs.get(i).takeDamage(10, 0);
                    active = false;
                    return;
                }
            }
        } else {
            if (this.intersects(Game.player)) {
                Game.player.vida -= 40;
                if (Game.player.vida <= 0) Game.gameOver();
                active = false;
            }
        }
    }

    public void render(Graphics g) {
        if (sprite != null)
            g.drawImage(sprite, x - Camera.x, y - Camera.y, 8, 8, null);
    }
}
