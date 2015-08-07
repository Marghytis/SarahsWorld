package effects.particles;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import render.Texture;
import util.Color;
import util.math.Vec;


public class Particle {
	public Vec pos = new Vec();
	public Vec vel = new Vec();//per second
	public Color col = new Color();
	public float rot;
	public float rad;
	public float lived;//sec
	public boolean justSpawned = true;
	
	public static class ParticleType {
		public Texture tex;
		public int vbo;
		
		public ParticleType(Texture tex){
			this.tex = tex;

			float[] vertices = new float[]{
					(float)tex.texCoords[0], (float)tex.texCoords[3], (float)tex.pixelCoords[0], (float)tex.pixelCoords[1],
					(float)tex.texCoords[2], (float)tex.texCoords[3], (float)tex.pixelCoords[2], (float)tex.pixelCoords[1],
					(float)tex.texCoords[2], (float)tex.texCoords[1], (float)tex.pixelCoords[2], (float)tex.pixelCoords[3],
					(float)tex.texCoords[0], (float)tex.texCoords[1], (float)tex.pixelCoords[0], (float)tex.pixelCoords[3]};
			
			//create VBO
			FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
			buffer.put(vertices);
			buffer.flip();
			
			vbo = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
	}
}
