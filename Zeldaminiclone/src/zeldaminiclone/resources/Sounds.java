package zeldaminiclone.resources;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class Sounds {

    private Clip clip;

    public Sounds (String path){
        try {
            AudioInputStream ais =
                    AudioSystem.getAudioInputStream(new File(path));
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Não foi possível carregar o áudio: "+path);
        }
    }

    public void play(){
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }


    public void loop(){
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop(){
        if (clip != null) {
            clip.stop();
        }
    }
}
