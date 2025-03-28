package game2D;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class BackgroundMusic implements Runnable {

    private String midiFilePath;  // Path to the MIDI file
    private Sequencer sequencer;  // MIDI sequencer to play the music
    private boolean loop;         // Flag to determine if the music should loop
    private boolean isPlaying;    // Flag to check if the music is currently playing
    private Synthesizer synthesizer; // Synthesizer to control volume
    private MidiChannel[] channels; // MIDI channels to adjust volume

    /**
     * Constructor for BackgroundMusic.
     *
     * @param midiFilePath The path to the MIDI file.
     * @param loop         Whether the music should loop.
     */
    public BackgroundMusic(String midiFilePath, boolean loop) {
        this.midiFilePath = midiFilePath;
        this.loop = loop;
        this.isPlaying = false;
    }

    /**
     * Initializes the MIDI sequencer and loads the MIDI file.
     *
     * @throws MidiUnavailableException If the MIDI system is unavailable.
     * @throws InvalidMidiDataException If the MIDI file is invalid.
     * @throws IOException              If there is an error reading the MIDI file.
     */
    private void initializeSequencer() throws MidiUnavailableException, InvalidMidiDataException, IOException {
        sequencer = MidiSystem.getSequencer();  // Get the default sequencer
        sequencer.open();  // Open the sequencer

        File midiFile = new File(midiFilePath);  // Load the MIDI file
        Sequence sequence = MidiSystem.getSequence(midiFile);
        sequencer.setSequence(sequence);  // Set the sequence to the sequencer

        if (loop) {
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);  // Loop the music indefinitely
        }

        // Initialize synthesizer and channels for volume control
        if (sequencer instanceof Synthesizer) {
            synthesizer = (Synthesizer) sequencer;
        } else {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
        }
        channels = synthesizer.getChannels();
    }

    /**
     * Starts playing the background music.
     */
    public void play() {
        if (sequencer == null || !sequencer.isOpen()) {
            try {
                initializeSequencer();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (!isPlaying) {
            sequencer.start();  // Start playing the music
            isPlaying = true;
        }
    }

    /**
     * Pauses the background music.
     */
    public void pause() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();  // Stop the sequencer
            isPlaying = false;
        }
    }

    /**
     * Stops the background music and closes the sequencer.
     */
    public void stop() {
        if (sequencer != null) {
            if (sequencer.isRunning()) {
                sequencer.stop();  // Stop the sequencer
            }
            sequencer.close();  // Close the sequencer
            isPlaying = false;
        }
        if (synthesizer != null && synthesizer.isOpen()) {
            synthesizer.close();  // Close the synthesizer
        }
    }

    /**
     * Checks if the music is currently playing.
     *
     * @return True if the music is playing, false otherwise.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Changes the volume of the background music.
     *
     * @param volume The volume level (0 to 127).
     */
    public void setVolume(int volume) {
        if (channels != null) {
            for (MidiChannel channel : channels) {
                if (channel != null) {
                    channel.controlChange(7, volume);  // 7 is the controller number for volume
                }
            }
        }
    }

    /**
     * The run method is called when the thread starts.
     * It plays the background music.
     */
    @Override
    public void run() {
        play();
    }
}
