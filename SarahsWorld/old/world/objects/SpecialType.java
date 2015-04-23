package world.objects;

import java.util.Random;

import render.Texture;

public class SpecialType {
	
	ThingType[] types;
	Texture[][] texs;

	public SpecialType(ThingType[] types, Texture[][] texs){
		this.types = types;
		this.texs = texs;
	}
	
	public int getRandomIndex(ThingType type, Random rand){
		for(int i = 0; i < types.length; i++){
			if(types[i].equals(type)){
				return rand.nextInt(texs[i].length);
			}
		}
		return -1;
	}
	
	public Texture getTex(ThingType type, int tex){
		for(int i = 0; i < types.length; i++){
			if(types[i].equals(type)){
				return texs[i][tex];
			}
		}
		return null;
	}
}
