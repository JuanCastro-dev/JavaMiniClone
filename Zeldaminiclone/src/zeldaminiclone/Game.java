package zeldaminiclone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable, KeyListener {

    private JFrame frame;
    public static final int  WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;

    private Thread thread;
    private boolean isRunning = true;

    static Player player;
    static World world;

    public Game(){
        thread = new Thread(this);
        player = new Player(0,0);
        world = new World("map.png");

        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();
        this.addKeyListener(this);

        thread.start();
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
    public void tick(){
        player.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null){
            this.createBufferStrategy(3);
            return;
        }

        //Pinta o quadro
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH*SCALE,HEIGHT*SCALE);

        //Pinta o mapa
        world.render(g);
        //Pinta o player
        player.render(g);

        g.dispose();
        bs.show();
    }

    //Jogo em execução
    @Override
    public void run() {
        while (isRunning){
            tick();
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
}
