package zeldaminiclone.blocks;

import zeldaminiclone.Camera;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PyramidTorch extends Rectangle {

    private static BufferedImage texture;

    static {
        try {
            texture = ImageIO.read(new File("resources/map/piramid/pyramid_torch.png"));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar: resources/map/piramid/pyramid_torch.png");
        }
    }

    public PyramidTorch(int x, int y) {
        super(x, y, 16, 16);
    }

    public void render(Graphics g) {
        g.drawImage(texture, x - Camera.x, y - Camera.y, width, height, null);
    }
}
