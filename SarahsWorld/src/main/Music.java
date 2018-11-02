package main;

import core.Updater;
import util.Sound;
import util.SoundSource;

public class Music implements Updater {

	boolean startingMusic = false;;
	SoundSource musicStart;
	SoundSource musicLoop;
	
	public Music(Sound start, Sound loop) {
		musicStart = new SoundSource();
		musicStart.loadSound(start);
		musicLoop = new SoundSource();
		musicLoop.loadSound(loop, true);
	}
	
	public boolean isMusicRunning() {
		return musicStart.isPlaying() || musicLoop.isPlaying();
	}
	
	public void startMusic() {
		startingMusic = true;
		musicLoop.stop();
		musicStart.play();
	}
	
	public void pauseMusic() {
		musicStart.pause();
		musicLoop.pause();
	}
	
	public void stopMusic() {
		musicStart.stop();
		musicLoop.stop();
		startingMusic = false;
	}

	public boolean update(double delta) {
		
		if(startingMusic && !musicStart.isPlaying()) {
			startingMusic = false;
			musicLoop.play();
		}
		return false;
	}
	
	public String debugName() {
		return "Music";
	}
	
}
