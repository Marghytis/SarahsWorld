package basis.effects.particleEffects;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;

import basis.effects.particleEffects.Particle.ParticleType;
import render.Render;
import render.Shader;
import render.TexFile;
import render.VAO;
import render.VBO;
import render.VBO.VAP;
import util.math.UsefulF;
import util.math.Vec;

public abstract class ParticleEmitter {

	public float interval;//interval between two emits
	public ParticleType type;
	public boolean emitting = true;
	protected float lifeSpan;
	boolean externalKill = true;;
	
	/**
	 * 
	 * @param particleAmount
	 * @param spawnRate spawns per second
	 * @param type
	 * @param lifeSpan in seconds
	 */
	public ParticleEmitter(int particleAmount, int spawnRate, ParticleType type, float lifeSpan, boolean externalKill){
		particles = new Particle[particleAmount];
		for(int i = 0; i < particles.length; i++){
			particles[i] = new Particle(i);
			particles[i].lived = lifeSpan;//To make it dead
			if(i > 0){
				particles[i].nextFree = particles[i-1];
			}
		}
		firstFree = particles[particles.length-1];
		if(spawnRate > 0){
			this.interval = 1.0f/spawnRate;
		} else {
			emitting = false;
			this.interval = Integer.MAX_VALUE;
		}
		
		this.type = type;
		this.lifeSpan = lifeSpan;
		this.externalKill = externalKill;
		initVAO();
	}
	public ParticleEmitter(int particleAmount, int spawnRate, ParticleType type, float lifeSpan){
		this(particleAmount, spawnRate, type, lifeSpan, false);
	}

	public Particle[] particles;
	public Particle firstFree;
	public Random random = new Random();
	
	int index;
	public int emittParticle(float age){
		Particle p;
		if(externalKill){
			if(firstFree == null){
				System.err.println("Not enough space in particle pool of path " + type.tex.file.path);
				return -1;
			}
			p = firstFree;
			firstFree = p.nextFree;
			p.nextFree = null;
		} else {
			p = particles[index];
			index = (index+1)%particles.length;
		}
		
		makeParticle(p);
		p.lived = 0;
		p.justSpawned = true;
		tickParticle(p, age);
		return p.indexInPool;
	}
	public void destroy(Particle p){
		killParticle(p);
		if(externalKill){
			p.nextFree = firstFree;
			firstFree = p;
		}
		p.lived = lifeSpan;
	}
	
	//To get overridden
	private void killParticle(Particle p){};
	protected abstract void makeParticle(Particle p);
	protected void velocityInterpolator(Particle p, float delta){};
	protected void colorInterpolator(Particle p, float delta){};
	protected void rotationInterpolator(Particle p, float delta){};
	protected void radiusInterpolator(Particle p, float delta){};
	
	float overFlow;
	public void tick(float delta){
		if(emitting){
			float timePassed = delta+overFlow;
			int count = (int)(timePassed/interval);
			for(int i = 0; i < count; i++){
				emittParticle(timePassed - (i*interval));
			}
			overFlow = timePassed%interval;
		}
		for(Particle p : particles){
			if(p.justSpawned){
				p.justSpawned = false;
			} else if(p.lived < lifeSpan){
				tickParticle(p, delta);
			}
		}
	}
	
	public void tickParticle(Particle p, float delta){
		p.lived += delta;//gets used by the interpolators
		velocityInterpolator(p, delta);
		colorInterpolator(p, delta);
		rotationInterpolator(p, delta); p.rot -= (float)Math.floor(p.rot);//"normalize" to 1
		radiusInterpolator(p, delta);
		p.pos.shift(p.vel, delta);
		if(p.lived >= lifeSpan){
			destroy(p);
		}
	}
	
	VAO vao;
	ByteBuffer draw;
	ShortBuffer positions;
	ShortBuffer rotations;
	FloatBuffer sizes;
	ByteBuffer colors;
	int[] offsets = new int[5];
	public static Vec offset = new Vec();
	public static Shader particleShader = Shader.create("res/shader/particle.vert", "res/shader/particle.frag", "pos1", "in_texCoords", "draw", "pos2", "rot", "size", "in_color");
	public void initVAO(){
		//boolean draw
		//vec2 rotation (cos(phi), sin(phi))
		//vec2 position
		//float size
		//vec4 color
		int[] sizes = {1*Byte.BYTES, 2*Short.BYTES, 2*Short.BYTES, 1*Float.BYTES, 4*Byte.BYTES};
		int bytesPerQuad = 0;
		offsets = new int[sizes.length];
		for(int i = 0; i < sizes.length; i++) {
			offsets[i] = bytesPerQuad;
			bytesPerQuad += sizes[i];
		}
		VBO vbo = createVBO(GL15.GL_STREAM_DRAW, 1, particles.length,
				new int[] {		1,				2,				2,				1,				4},
				new int[] {		GL11.GL_BYTE,	GL11.GL_INT,	GL11.GL_SHORT,	GL11.GL_FLOAT,	GL11.GL_BYTE},
				new boolean[] {	false,			false,			true,			false,			true});
		//						draw			position		rotation		size			color
		buffer = vbo.getBuffer();
		
		vao = new VAO(
				new VBO(Render.standardIndex, GL15.GL_STATIC_READ),
				type.vbo,
				vbo
				);
	}
	
	public VBO createVBO(int usage, int divisor, int size, int[] amounts, int[] dataTypes, boolean[] normalized) {
		int totalByteOffset = 0;
		VAP[] vaps = new VAP[amounts.length];
		
		for(int i = 0; i < amounts.length; i++) {
			vaps[i] = new VAP(amounts[i], dataTypes[i], normalized[i], totalByteOffset);
			totalByteOffset += amounts[i]*getBytes(dataTypes[i]);
		}

		ByteBuffer buffer = BufferUtils.createByteBuffer(size*totalByteOffset);
		
		return new VBO(buffer, usage, totalByteOffset, divisor, vaps);
	}
	
	public int getBytes(int dataType) {
		switch(dataType) {
		case GL11.GL_BYTE : return Byte.BYTES;
		case GL11.GL_SHORT : return Short.BYTES;
		case GL11.GL_FLOAT : return Float.BYTES;
		case GL11.GL_INT : return Integer.BYTES;
		default: return -1;
		}
	}
	
	ByteBuffer buffer;
	
	public void render(float scaleX, float scaleY){
		buffer.clear();
		
		for(int i = 0; i < particles.length; i++){
			if(particles[i].lived < lifeSpan){
				buffer.put((byte)1);
			} else {
				buffer.put((byte)0);
			}
			buffer.putInt((int)particles[i].pos.x);
			buffer.putInt((int)particles[i].pos.y);
			
			buffer.putShort((short)(Short.MAX_VALUE*UsefulF.cos100[(int)(particles[i].rot*100)%100]));
			buffer.putShort((short)(Short.MAX_VALUE*UsefulF.sin100[(int)(particles[i].rot*100)%100]));
			
			buffer.putFloat(particles[i].rad);

			buffer.put((byte)(Byte.MAX_VALUE*particles[i].col.r));
			buffer.put((byte)(Byte.MAX_VALUE*particles[i].col.g));
			buffer.put((byte)(Byte.MAX_VALUE*particles[i].col.b));
			buffer.put((byte)(Byte.MAX_VALUE*particles[i].col.a));
		}
		buffer.flip();
//		buffer.put((byte)1);
//		buffer.putShort((short)0);
//		buffer.putShort((short)0);
//		buffer.putShort((short)Short.MAX_VALUE);
//		buffer.putShort((short)0);
//		buffer.putFloat(5);
//		buffer.put(new byte[]{Byte.MAX_VALUE, 0, 0, Byte.MAX_VALUE});
//		buffer.flip();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vao.vbos[1].handle);
			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
//			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offsets[0], draw);
//			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offsets[1], positions);
//			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offsets[2], rotations);
//			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offsets[3], sizes);
//			GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offsets[4], colors);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		particleShader.bind();
		type.tex.file.bind();
		particleShader.set("scale", scaleX, scaleY);
		particleShader.set("offset", (float)offset.x, (float)offset.y);
		vao.bindStuff();
			GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0, particles.length);
		vao.unbindStuff();
		TexFile.bindNone();
		Shader.bindNone();
	}
	
	public void terminate(){
		GL15.glDeleteBuffers(type.vbo.handle);
	}
	
	public double getLifeSpan() {
		return lifeSpan;
	}
}
