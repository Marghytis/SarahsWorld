package main;

public interface Savable {

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

	public String save();
	
	public void load(String save);
	
}
