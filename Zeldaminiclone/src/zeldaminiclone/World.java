package zeldaminiclone;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//Classe responsável pela criação do mapa
public class World {

    public static ArrayList<Blocks> blocks = new ArrayList<>();

    public World(String path) {
        try {
            BufferedImage map = ImageIO.read(new File(path));
            int width = map.getWidth();
            int height = map.getHeight();

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int pixelColor = map.getRGB(x, y);

                    int red = (pixelColor >> 16) & 0xff;
                    int green = (pixelColor >> 8) & 0xff;
                    int blue = pixelColor & 0xff;

                    if (red == 0 && green == 0 && blue == 0) {
                        blocks.add(new Blocks(x * 16, y * 16));
                    } else if (red == 0 && green == 0 && blue == 255) {
                        Game.player.x = x * 16;
                        Game.player.y = y * 16;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Arquivo não encontrado no caminho: " + path);
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).render(g);
        }
    }

    public static boolean isFree(int xnext, int ynext) {
        Rectangle futurePlayer = new Rectangle(xnext, ynext, 16, 16);
        for (int i = 0; i < blocks.size(); i++) {
            Blocks bloco = blocks.get(i);
            if (futurePlayer.intersects(bloco)) {
                return false;
            }
        }
        return true;
    }
}
