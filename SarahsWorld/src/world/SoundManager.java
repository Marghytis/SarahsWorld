package world;

import core.Updater;
import extra.Main;
import extra.Res;
import menu.MenuManager.MenuType;
import moveToLWJGLCore.Music;
import menu.Settings;
import util.SoundSource;

public class SoundManager implements Updater {

	Music music;
	SoundSource death;
	
	public SoundManager() {
		this.music = new Music(Res.musicStart, Res.musicLoop);
		this.death = new SoundSource();
		death.loadSound(Res.deathSong);
	}
	
	public Music getMusic() {
		return music;
	}
	
	public boolean update(double delta) {
		
		music.update(delta);
		
		if(Settings.getBoolean("SOUND") && Settings.getBoolean("MUSIC") && !music.isMusicRunning() && !death.isPlaying() && !Main.game().world.isGameOver()){
			music.startMusic();
		} else if((!Settings.getBoolean("SOUND") || !Settings.getBoolean("MUSIC")) && music.isMusicRunning()) {
			music.pauseMusic();
		}
		
		if(Main.game().world.isGameOver() && !death.isPlaying()) {
			Main.game().menu.setMenu(MenuType.MAIN);
		}
		
		return false;
	}
	
	public void playFuneralMarch() {
		music.stopMusic();
		death.play();
	}
	
	public String debugName() {
		return "World Sound";
	}
	
}
