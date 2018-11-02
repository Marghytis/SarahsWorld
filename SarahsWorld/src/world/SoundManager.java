package world;

import core.Updater;
import main.Music;
import main.Res;
import menu.Settings;

public class SoundManager implements Updater {

	Music music;
	
	public SoundManager() {
		this.music = new Music(Res.musicStart, Res.musicLoop);
	}
	
	public Music getMusic() {
		return music;
	}
	
	public boolean update(double delta) {
		
		music.update(delta);
		
		if(Settings.SOUND && Settings.MUSIC && !music.isMusicRunning()){
			music.startMusic();
		} else if((!Settings.SOUND || !Settings.MUSIC) && music.isMusicRunning()) {
			music.pauseMusic();
		}
		
		return false;
	}
	
	public String debugName() {
		return "World Sound";
	}
	
}
