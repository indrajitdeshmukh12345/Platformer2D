package game2D;

import java.io.*;
import javax.sound.sampled.*;

public class SoundLoop extends Thread {

    String filename;    // The name of the file to play
    boolean finished;   // A flag showing that the thread has finished
    boolean loop;       // A flag to determine if the sound should loop
    Clip clip;          // The clip that will play the sound

    public SoundLoop(String fname) {
        filename = fname;
        finished = false;
        loop = true;
    }

    /**
     * Sets whether the sound should loop continuously.
     * @param loop true to loop the sound, false to play it once.
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * Sets the volume of the sound.
     * @param volume The volume level, typically between 0.0 (silent) and 1.0 (full volume).
     */
    public void setVolume(float volume) {
        if (clip == null || !clip.isOpen()) return; // Ensure clip is initialized
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return; // Ensure gain control is supported

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // Convert volume to decibels
        float minVolume = -80.0f; // Silent
        float maxVolume = 6.0f;   // Loudest
        float dB = Math.max(minVolume, Math.min(maxVolume, volume)); // Clamp volume

        gainControl.setValue(dB);
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
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);


            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

            while (loop || clip.isRunning()) { Thread.sleep(100); }
            clip.close(); // Only close when the sound is done playing
        } catch (Exception e) { e.printStackTrace(); }
        finished = true;
    }
    public void restart() {
        if (clip != null) {
            clip.setFramePosition(0); // Reset sound to the beginning
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        }
    }


}
