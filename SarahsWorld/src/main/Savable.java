package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Savable {

	String world = 		"World";
	String container =		"Container";
	String allPoints =			"AllPoints";
	String point =					"P";
	String columns =		"Columns";
	String column =				"Column";
	String field =					"F";
	String objects = 		"Objects";
	String objectList = 		"ObjectList";
	String object = 				"O";
	String generator =		"Generator";
	String layer =				"Layer";
	
	public Savable(DataInputStream input) {}

	public abstract void save(DataOutputStream output);
	
}
