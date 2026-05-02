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

    public Enemy(int x, int y) {
        super(x, y, 16, 16);
        this.rand = new Random();

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
}
