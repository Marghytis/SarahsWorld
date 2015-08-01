package menu;

import render.TexFile;
import render.Texture;
import util.Color;
import util.Render;
import util.math.Vec;
import core.Window;

public class Element {

	public double relX1, relY1, relX2, relY2;
	public int x1O, y1O, x2O, y2O;
	public int x1, y1, x2, y2, w, h;
	public Color color;
	public Texture tex;
	
	public Element(double relX1, double relY1, double relX2, double relY2, int x1, int y1, int x2, int y2, Color color, Texture tex){
		this.relX1 = relX1;
		this.relY1 = relY1;
		this.relX2 = relX2;
		this.relY2 = relY2;
		this.x1O = x1;
		this.y1O = y1;
		this.x2O = x2;
		this.y2O = y2;
		this.color = color;
		this.tex = tex;
		setCoords();
	}
	
	public void setCoords(){
		this.x1 = (int)(Window.WIDTH*relX1) + x1O;
		this.y1 = (int)(Window.HEIGHT*relY1) + y1O;
		this.x2 = (int)(Window.WIDTH*relX2) + x2O;
		this.y2 = (int)(Window.HEIGHT*relY2) + y2O;
		this.w = x2 - x1;
		this.h = y2 - y1;
	}
	
	public boolean contains(Vec vec){
		return vec.x >= x1 && vec.x <= x2 && vec.y >= y1 && vec.y <= y2;
	}
	
	public void update(double delta){}
	public void render(){
		boolean noColor = color == null;
		if(!noColor){
			color.bind();
		} else {
			Color.WHITE.bind();
		}
		if(tex != null){
			tex.file.bind();
			Render.quad(x1, y1, x2, y2, tex);
		} else if(!noColor){
			TexFile.bindNone();
			Render.quad(x1, y1, x2, y2);
		}
	}
	
	public void pressed(int button, Vec mousePos){}
	
	public boolean released(int button, Vec mousePos, Vec pathSincePress){return false;}
	
	public void keyPressed(int key){}
}
