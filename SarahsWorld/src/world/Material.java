package world;

import render.TexFile;
import render.Texture;
import util.Color;

public enum Material {
	GRASS(Color.GREEN, 400f, new Texture("res/materials/Grass.png")),
	EARTH(Color.BROWN, 300f, new Texture("res/materials/Earth.png")),
	STONE(Color.GRAY, 300f, new Texture("res/materials/Stone.png")),
	SANDSTONE(Color.YELLOW, 300f, new Texture("res/materials/Sandstone.png")),
	SAND(Color.YELLOW, 300f, new Texture("res/materials/Sand.png")),
	WATER(Color.BLUE, 300f, new Texture("res/materials/Water.png")),
	CANDY(Color.BLUE, 400f, new Texture("res/materials/Candy.png")),
	AIR(new Color(1, 1, 1, 0), 150f, TexFile.emptyTex),
	SNOW(new Color(0.8f, 0.8f, 0.8f), 400f, TexFile.emptyTex),
	WOOD(Color.BROWN, 300f, TexFile.emptyTex),
	NO(Color.BLACK, 1, TexFile.emptyTex);
	
	public Color color = new Color();
	public Texture tex;
	public float deceleration;
	
	Material(Color color, float deceleration, Texture tex){
		this.color.set(color);
		this.deceleration = deceleration;
		this.tex = tex;
	}
	
}
