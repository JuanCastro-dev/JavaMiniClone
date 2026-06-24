package zeldaminiclone.enemies;

import zeldaminiclone.Camera;
import zeldaminiclone.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Orb extends Rectangle {

    private int dx, dy;
    private static final int SPEED = 2;
    private static BufferedImage sprite;
    public boolean active = true;

    static {
        try {
            sprite = ImageIO.read(Orb.class.getResourceAsStream("/farao/farao_ball.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar farao_ball.png");
        }
    }

    public Orb(int x, int y, int dx, int dy) {
        super(x, y, 8, 8);
        this.dx = dx;
        this.dy = dy;
    }

    public void tick() {
        x += dx * SPEED;
        y += dy * SPEED;
        this.setBounds(x, y, 8, 8);

        // desativa se sair dos limites do mapa
        if (x < 0 || y < 0 || x > zeldaminiclone.World.WIDTH * 16 || y > zeldaminiclone.World.HEIGHT * 16) {
            active = false;
            return;
        }

        if (this.intersects(Game.player)) {
            Game.player.vida -= 40;
            if (Game.player.vida <= 0) Game.gameOver();
            active = false;
        }
    }

    public void render(Graphics g) {
        if (sprite != null)
            g.drawImage(sprite, x - Camera.x, y - Camera.y, 8, 8, null);
    }
}
