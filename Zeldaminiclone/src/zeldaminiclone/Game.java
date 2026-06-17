package zeldaminiclone;

import zeldaminiclone.resources.Save;
import zeldaminiclone.resources.Sounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;

public class Game extends Canvas implements Runnable, KeyListener {

    private JFrame frame;
    public static final int  WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;

    private Thread thread;
    private boolean isRunning = true;

    public static Player player;
    static World world;

    public static int currentLevel = 1;
    public static final int maxLevel = 4;
    public static boolean nearExit = false;

    public static String gameState = "MENU";

    private Sounds music;

    private int fadeFrames = 0;
    private boolean fadeIn = false;
    private boolean fadeOut = false;
    private boolean pendingLevelChange = false;

    public Game(){
        Player.highScore = Save.loadScore();

        thread = new Thread(this);
        player = new Player(0,0);
        world = new World("resources/map/map_1.png");

        Camera.x = 0;
        Camera.y = 0;

        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();
        this.addKeyListener(this);

        thread.start();

        music = new Sounds("resources/sounds/ritmada.wav");
        music.loop();
    }

    public void initFrame(){
        frame = new JFrame("Zelda Mini Clone");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void stop(){
        isRunning = false;
        try{
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    //Lógica de atualização das ações
    public void tick() throws IOException {
        if (gameState.equals("NORMAL")) {
            player.tick();
            for (Enemy e : World.enemies) {
                e.tick();
            }
            for (zeldaminiclone.enemies.Mummy m : World.mummies) {
                m.tick();
            }
            nearExit = World.exitX >= 0 &&
                Math.abs(player.x - World.exitX) < 16 &&
                Math.abs(player.y - World.exitY) < 16;
        }
        if (gameState.equals("GAME_OVER")) {
            fadeFrames++;
            if (fadeFrames > 120) {
                fadeFrames = 0;
                gameState = "MENU";
            }
            return;
        }
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null){
            this.createBufferStrategy(3);
            return;
        }

        //Pinta o quadro
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH*SCALE,HEIGHT*SCALE);

        if (gameState.equals("MENU")) {
            renderMenu(g);
        } else if (gameState.equals("NORMAL")) {
            g.scale(SCALE, SCALE);
            world.render(g);
            world.renderItens(g);
            for (Enemy e : World.enemies) {
                e.render(g);
            }
            for (zeldaminiclone.enemies.Mummy m : World.mummies) {
                m.render(g);
            }
            player.render(g);
            world.renderExit(g);
            player.renderSwordHint(g);
            renderHUD(g);
        } else if (gameState.equals("GAME_OVER")) {
            renderGameOver(g);
        }

        renderFade(g);

        g.dispose();
        bs.show();
    }

    //Jogo em execução
    @Override
    public void run() {
        while (isRunning){
            try {
                tick();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            render();
            try{
                Thread.sleep(1000/60);  //Define a taxa de 60fps
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.right = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            player.left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP){
            player.up = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            player.down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_E){
            trocarFase();
        }
        if (e.getKeyCode() == KeyEvent.VK_F && gameState.equals("NORMAL")) {
            player.attack();
        }
        if (gameState.equals("GAME_OVER") && Player.score > Player.highScore) {
            Player.highScore = Player.score;
            Save.saveScore(Player.highScore);
        }
        if (gameState.equals("MENU") || gameState.equals("GAME_OVER")) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (gameState.equals("MENU")){
                    fadeIn = true;
                    fadeFrames = 255;
                }
                player.vida = 50;
                player.itensColetados = 0;
                Game.currentLevel = 1;
                restartGame("resources/map/map_1.png");
                gameState = "NORMAL";
                music.stop();
                music = new Sounds("resources/sounds/music_1.wav");
                music.loop();
            }
            Player.score = 0;
        }
    }

    public void trocarFase() {
        if (!nearExit || fadeOut || pendingLevelChange) return;
        nearExit = false;
        if (currentLevel >= maxLevel) return;
        currentLevel++;
        pendingLevelChange = true;
        fadeOut = true;
        fadeFrames = 0;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            player.right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT){
            player.left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP){
            player.up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            player.down = false;
        }
    }

    public void renderHUD(Graphics g){
        //Barra de vida
        g.setColor(Color.RED);
        g.fillRect(10, 10, 100, 3);

        //Vida atual
        int vidaAtual = player.vida * 100 / 50;
        g.setColor(vidaAtual < 35 ? Color.ORANGE : Color.GREEN);
        g.fillRect(10,10,vidaAtual,3);

        //Borda da barra de vida
        g.setColor(Color.WHITE);
        g.drawRect(10, 10, 100, 3);

        //Itens coletados
        g.setColor(Color.WHITE);
        Font font = new Font("arial", Font.BOLD, 5);
        g.setFont(font);
        g.drawString("Itens coletados: "+ player.itensColetados,10,25);

        //Pontuação do jogador
        g.setColor(Color.white);
        g.drawString("Pontos: " + Player.score, 10, 35);
        g.drawString("Recorde: " + Player.highScore, 10, 45);
    }

    public void renderFade(Graphics g) {
        if (fadeIn) {
            fadeFrames -= 5;
            if (fadeFrames <= 0) {
                fadeFrames = 0;
                fadeIn = false;
            }
        } else if (fadeOut) {
            fadeFrames += 5;
            if (fadeFrames >= 255) {
                fadeFrames = 255;
                fadeOut = false;
                Player.score += 500;
                fadeIn = true;
                if (pendingLevelChange) {
                    pendingLevelChange = false;
                    music.stop();
                    music = new Sounds("resources/sounds/music_" + currentLevel + ".wav");
                    music.loop();
                    restartGame("resources/map/map_" + currentLevel + ".png");
                }
            }
        }
        if (fadeFrames > 0) {
            g.setColor(new Color(0, 0, 0, fadeFrames));
            g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
        }
    }

    public static void gameOver(){
        Game.gameState = "GAME_OVER";
    }

    //Limpar elementos e gerar nova fase
    public void restartGame(String levelPath) {
        World.blocks.clear();
        World.dirtBlocks.clear();
        World.items.clear();
        World.enemies.clear();
        World.mummies.clear();
        World.exitX = -1;
        World.exitY = -1;
        world = new World(levelPath);
    }

    public void renderMenu(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Zelda Mini Clone", 80, 150);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Pressione ENTER para começar", 65, 190);
    }

    public void renderGameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("GAME OVER", 100, 150);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.setColor(Color.white);
        g.drawString("Pressione ENTER para reiniciar", 85, 180);
    }
}
