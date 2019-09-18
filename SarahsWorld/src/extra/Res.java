package extra;

import java.awt.Font;

import basis.ResourceDatabase;
import render.Animation;
import render.Shader;
import util.Color;
import util.MultiSoundSource;
import util.Sound;
import util.TrueTypeFont;

public class Res extends ResourceDatabase {
	
	public static TrueTypeFont menuFont = new TrueTypeFont(new Font("Times New Roman", 0, 35), true);
	public static Color menuFontColor = new Color(0.9f, 0.8f, 0.1f);

	public static Sound coinSound = new Sound("res/sound/coins_quick_movement_in_hand.ogg");
	public static Sound musicStart = new Sound("res/sound/Sarahs Welt Anfang.ogg");
	public static Sound musicLoop = new Sound("res/sound/Sarahs Welt Loop.ogg");
	public static Sound deathSong = new Sound("res/sound/FuneralMarch.ogg");
	public static MultiSoundSource coinSoundSource = new MultiSoundSource(10, coinSound);

	public static Shader landscapeShader = Shader.create("res/shader/material.vert", "res/shader/material.frag", "in_Position", "in_TextureCoords", "in_Alphas", "in_TransAlpha");
	public static Shader thingShader = Shader.withGeometry("res/shader/thing.vert", "res/shader/thing.geom", "res/shader/thing.frag", "in_position", "in_rotation", "in_texCoords", "in_mirror", "in_color", "in_z", "in_size", "in_box", "in_texWH");
	public static Shader thingOutlineShader = Shader.withGeometry("res/shader/thing.vert", "res/shader/thing.geom", "res/shader/thingOutline.frag", "in_position", "in_rotation", "in_texCoords", "in_mirror", "in_color", "in_z", "in_size", "in_box", "in_texWH");
	public static Shader thingBoxShader = Shader.withGeometry("res/shader/thing.vert", "res/shader/thingBox.geom", "res/shader/thingBox.frag", "in_position", "in_rotation", "in_texCoords", "in_mirror", "in_color", "in_z", "in_size", "in_box", "in_texWH");
	public static Shader darknessShader = Shader.create("res/shader/color.vert", "res/shader/color.frag", "in_Position", "in_Color");
	public static Shader backgroundShader = Shader.create("res/shader/colorBackground.vert", "res/shader/colorBackground.frag", "in_Position");
	public static Shader usualShader = Shader.create("res/shader/usual.vert", "res/shader/usual.frag", "in_position", "in_texCoords");

//	static {readTexTable(Main.TEX_ATLAS_TABLE_PATH);}
	
	public static Animation death;
	
	public static void init() {
		readTexTable(Main.TEX_ATLAS_TABLE_PATH);
		//these animations can only be created here, when their TexAtlas has been loaded
		death = new Animation("death", Res.getAtlas("sarah_death"), 4, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
	}
}
