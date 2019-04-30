package effects.particleEffects;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import render.Render;
import render.Texture;
import render.VBO;
import render.VBO.VAP;
import util.Color;
import util.math.Vec;


public class Particle {
	public Particle nextFree;
	public int indexInPool;
	
	public Vec pos = new Vec();
	public Vec vel = new Vec();//per second
	public Color col = new Color();
	/**Range : 0 <= rot < 1 */
	public float rot;
	public float rad;
	public float lived;//sec
	public boolean justSpawned = true;
	public Vec someVec;
	public float someFloat;
	
	public Particle(int index){
		this.indexInPool = index;
	}
	
	public static class ParticleType {
		public Texture tex;
		public VBO vbo;
		
		public ParticleType(Texture tex){
			this.tex = tex;
			
//			short[] data = {
//					(short)-100, (short)-100, (short)0, (short)1,
//					(short)100, (short)-100, (short)1, (short)1,
//					(short)100, (short)100, (short)1, (short)0,
//					(short)-100, (short)100, (short)0, (short)0
//			};

			//create VBO
//			ShortBuffer buffer = Render.createBuffer(
//													(short)-100, (short)-100, (short)0, (short)1,
//													(short)100, (short)-100, (short)1, (short)1,
//													(short)100, (short)100, (short)1, (short)0,
//													(short)-100, (short)100, (short)0, (short)0);
//			ByteBuffer buffer2 = BufferUtils.createByteBuffer(4*(2*Integer.BYTES + (2*Short.BYTES)));
//			buffer2.putInt(tex.pixelCoords[0])
			ShortBuffer buffer = Render.createBuffer(
					(short)tex.pixelCoords[0], (short)tex.pixelCoords[1], (short)(Short.MAX_VALUE*tex.texCoords[0]), (short)(Short.MAX_VALUE*tex.texCoords[3]), 
					(short)tex.pixelCoords[2], (short)tex.pixelCoords[1], (short)(Short.MAX_VALUE*tex.texCoords[2]), (short)(Short.MAX_VALUE*tex.texCoords[3]), 
					(short)tex.pixelCoords[2], (short)tex.pixelCoords[3], (short)(Short.MAX_VALUE*tex.texCoords[2]), (short)(Short.MAX_VALUE*tex.texCoords[1]), 
					(short)tex.pixelCoords[0], (short)tex.pixelCoords[3], (short)(Short.MAX_VALUE*tex.texCoords[0]), (short)(Short.MAX_VALUE*tex.texCoords[1]));
			
			vbo = new VBO(buffer, GL15.GL_STATIC_DRAW, 4*Short.BYTES,
					new VAP(2, GL11.GL_SHORT, false, 0),
					new VAP(2, GL11.GL_SHORT, true, 2*Short.BYTES));
		}
	}
}
