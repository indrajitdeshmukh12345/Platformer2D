package game2D;

import java.io.*;
import javax.sound.sampled.*;

public class SoundEcho extends Thread {

    String filename;	// The name of the file to play
    boolean finished;	// A flag showing that the thread has finished

    public SoundEcho(String fname) {
        filename = fname;
        finished = false;
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
            AudioFormat	format = stream.getFormat();
            AudioInputStream Echo = applyEchoFilter(stream,1000,0.5f);
            //EchoFilterStream filtered = new EchoFilterStream(stream,100000,0.5f);
            AudioInputStream f = new AudioInputStream(Echo,format,stream.getFrameLength());
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip clip = (Clip)AudioSystem.getLine(info);
            clip.open(f);
            clip.start();
            Thread.sleep(100);
            while (clip.isRunning()) { Thread.sleep(100); }
            clip.close();
        }
        catch (Exception e) {	}
        finished = true;

    }


    public static AudioInputStream applyEchoFilter(AudioInputStream inputStream, int delay, float decay) throws IOException {
        AudioFormat format = inputStream.getFormat();
        byte[] inputData = inputStream.readAllBytes();
        int length = inputData.length;
        byte[] outputData = new byte[length];
        System.arraycopy(inputData, 0, outputData, 0, length);

        for (int p = 0; p < length - 2; p += 2) {
            short amp = getSample(inputData, p);
            int delayed = p + delay;

            if (delayed < length - 2) {
                short val = getSample(outputData, delayed);
                short echoed = (short) (val + (amp * decay));

                if (echoed > Short.MAX_VALUE) {
                    echoed = Short.MAX_VALUE;
                } else if (echoed < Short.MIN_VALUE) {
                    echoed = Short.MIN_VALUE;
                }

                setSample(outputData, delayed, echoed);
            }
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(outputData);
        return new AudioInputStream(bais, format, outputData.length / format.getFrameSize());
    }

    private static short getSample(byte[] buffer, int position) {
        return (short) (((buffer[position + 1] & 0xff) << 8) |
                (buffer[position] & 0xff));
    }

    private static void setSample(byte[] buffer, int position, short sample) {
        buffer[position] = (byte) (sample & 0xFF);
        buffer[position + 1] = (byte) ((sample >> 8) & 0xFF);
    }

}
