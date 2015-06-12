package world;

import render.TexFile;
import render.Texture;
import util.Color;

public enum Material {
	GRASS(Color.GREEN, 400f, new Texture("res/materials/Grass.png"), true),
	EARTH(Color.BROWN, 300f, new Texture("res/materials/Earth.png"), true),
	STONE(Color.GRAY, 300f, new Texture("res/materials/Stone.png"), true),
	SANDSTONE(Color.YELLOW, 300f, new Texture("res/materials/Sandstone.png"), true),
	SAND(Color.YELLOW, 300f, new Texture("res/materials/Sand.png"), true),
	WATER(Color.BLUE, 300f, new Texture("res/materials/Water.png"), false),
	CANDY(Color.BLUE, 400f, new Texture("res/materials/Candy.png"), true),
	SNOW(new Color(0.8f, 0.8f, 0.8f), 400f, TexFile.emptyTex, true),
	WOOD(Color.BROWN, 300f, TexFile.emptyTex, true),
	AIR(new Color(0.8f, 0.8f, 1, 0), 100f, TexFile.emptyTex, false);
	
	public Color color = new Color();
	public Texture tex;
	public float deceleration;
	public boolean solid;
	
	Material(Color color, float deceleration, Texture tex, boolean solid){
		this.color.set(color);
		this.deceleration = deceleration;
		this.tex = tex;
		this.solid = solid;
	}
	
}
