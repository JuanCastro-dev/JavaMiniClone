package zeldaminiclone;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {

    private JFrame frame;
    public static final int  WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;

    private Thread thread;
    private boolean isRunning = true;

    public Game(){
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();

        start();
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

    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null){
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH*SCALE,HEIGHT*SCALE);
        g.dispose();
        bs.show();
    }

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
}
