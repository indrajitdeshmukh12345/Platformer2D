package game2D;

import java.io.*;
import javax.sound.sampled.*;

public class SoundLoop extends Thread {

    String filename;    // The name of the file to play
    boolean finished;    // A flag showing that the thread has finished
    boolean loop;
    Clip clip;

    public SoundLoop(String fname, Boolean isloop) {
        filename = fname;
        finished = false;
        loop = isloop;
    }

    /**
     * run will play the actual sound but you should not call it directly.
     * You need to call the 'start' method of your sound object (inherited
     * from Thread, you do not need to declare your own). 'run' will
     * eventually be called by 'start' when it has been scheduled by
     * the process scheduler.
     */
    public void run() {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("Sound file not found: " + filename);
                return;
            }
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
            Thread.sleep(100);
            while (clip.isRunning()) {
                Thread.sleep(100);
            }
            clip.close();
        } catch (Exception e) {
        }
        finished = true;

    }

    public void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
            finished = true;
        }

    }
}