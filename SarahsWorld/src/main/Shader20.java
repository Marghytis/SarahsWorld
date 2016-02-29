package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL20;

public enum Shader20 {
	AURA("res/shader/AuraShader.frag"),
	OUTLINE("res/shader/OutlineShader.frag");
	
	public void bind(){
		GL20.glUseProgram(handle);
	}
	
	public void release(){
		GL20.glUseProgram(0);
	}
	
	public static void bindNone(){
		GL20.glUseProgram(0);
	}
	
	public int handle;

	Shader20(String srcName){
		this(srcName, null);
	}
	Shader20(String srcName, String vertex){
		//create program
		int program = GL20.glCreateProgram();
		
		//create shader
		int fShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		int vShader = -1;
		if(vertex != null)
			 vShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		
		//add Source and compile shader
		try {
			GL20.glShaderSource(fShader, readFile(srcName));
			GL20.glCompileShader(fShader);
			System.out.println("SHADER INFO LOG: " + srcName);
			System.out.println(GL20.glGetShaderInfoLog(fShader, 1000));
			
			if(vShader != -1){
				GL20.glShaderSource(vShader, readFile(vertex));
				GL20.glCompileShader(vShader);
				System.out.println("SHADER INFO LOG: " + srcName);
				System.out.println(GL20.glGetShaderInfoLog(vShader, 1000));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//add shader to program and delete the object
		GL20.glAttachShader(program, fShader);
		GL20.glDeleteShader(fShader);
		
		if(vShader != -1){
			GL20.glAttachShader(program, vShader);
			GL20.glDeleteShader(vShader);
		}
		
		//link program
		GL20.glLinkProgram(program);
		
		handle = program;
	}

	public String readFile(String name) throws IOException{
		File f = new File(name);
		FileReader fReader = new FileReader(f);
		BufferedReader reader = new BufferedReader(fReader);
		String line = reader.readLine();
		
		String output = "";
		while(line != null){
			output += line;
			line = reader.readLine();
		}
		
		reader.close();
		return output;
	}
}
