package zeldaminiclone;

import zeldaminiclone.blocks.DirtBlock;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//Classe responsável pela criação do mapa
public class World {

    public static ArrayList<Blocks> blocks = new ArrayList<>();
    public static ArrayList<DirtBlock> dirtBlocks = new ArrayList<>();
    public static ArrayList<Item> items = new ArrayList<>();
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    public static int WIDTH, HEIGHT;

    private static BufferedImage background;

    static {
        try {
            background = ImageIO.read(new File("resources/map/map_grass.png"));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar background: resources/map/map_grass.png");
        }
    }

    public World(String path) {
        try {
            BufferedImage map = ImageIO.read(new File(path));
            WIDTH = map.getWidth();
            HEIGHT = map.getHeight();

            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    int pixelColor = map.getRGB(x, y);

                    int red = (pixelColor >> 16) & 0xff;
                    int green = (pixelColor >> 8) & 0xff;
                    int blue = pixelColor & 0xff;

                    if (red == 0 && green == 0 && blue == 0) {
                        blocks.add(new Blocks(x * 16, y * 16));
                    } else if (red == 0 && green == 0 && blue == 255) {
                        Game.player.x = x * 16;
                        Game.player.y = y * 16;
                    } else if (red == 255 && green == 0 && blue == 0) {
                        BufferedImage heartSprite = ImageIO.read(new File("resources/itens/full_heart.png"));
                        items.add(new Item(x * 16, y * 16, heartSprite));
                    }else if (red == 0 && green == 255 && blue == 0) {
                        enemies.add(new Enemy(x * 16, y * 16));
                    }
                    else if (red == 137 && green == 81 && blue == 41) {
                        dirtBlocks.add(new DirtBlock(x * 16, y * 16));
                    }else {
                        System.out.println("Pixel desconhecido em (" + x + "," + y + "): R=" + red + " G=" + green + " B=" + blue);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Arquivo não encontrado no caminho: " + path);
        }
    }

    public void render(Graphics g) {
        g.drawImage(background, -Camera.x, -Camera.y, WIDTH * 16, HEIGHT * 16, null);
        for (int i = 0; i < blocks.size(); i++) {
            blocks.get(i).render(g);
        }
    }

    public void renderItens(Graphics g) {
        for (Item item : items) {
            item.render(g);
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
