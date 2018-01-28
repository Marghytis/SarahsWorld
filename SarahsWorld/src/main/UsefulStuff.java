package main;

import util.Color;

public class UsefulStuff {

	public static Color colorFromHue(double hue, Color out){
		hue *= 3;
		out.r = (float)hueFunction(hue);
		out.g = (float)hueFunction(hue + 2);
		out.b = (float)hueFunction(hue + 4);
		return out;
	}
	
	static double hueFunction(double h){
		h %= 3;
		if(h < 1) return Math.min(2*h, 1);
		else if(h < 2) return Math.min(2*(2-h), 1);
		else return 0;
	}
}
