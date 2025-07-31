package core;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Music {
    private Clip music;
    private Clip loseEffect;

    public Clip loadMusicFile(String filePath) {
        Clip clip = null;
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioFile.getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInput);
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("Error finding music: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return clip;
    }
    public void loadMusic(String musicPath) {
        music = loadMusicFile(musicPath);
    }

    public void loadSoundEffect(String soundPath) {
        loseEffect = loadMusicFile(soundPath);
    }

    public void play() {
        if (music != null) {
            music.start();
            music.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.err.println("Can't find music file");
        }
    }

    public void playSound() {
        if (loseEffect != null) {
            if (music != null && music.isRunning()) {
                music.stop();
            }
            loseEffect.start();
            loseEffect.loop(0);
        } else {
            System.err.println("Sound can't be loaded");
        }
    }

    public void stop() {
        if (music != null && music.isRunning()) {
            music.stop();
        }
    }

    public void close() {
        if (music != null) {
            music.close();
        }
        if (loseEffect != null) {
            loseEffect.close();
        }
    }
}
