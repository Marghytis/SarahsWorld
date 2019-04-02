package things;

import base.entities.Attribute;

public abstract class AiPlugin {
	
	public String recon;
	
	public static final String s = ",";
	
	public String name;
	
	public void remove(Entity t) {}
	
	public abstract Attribute createAttribute(Entity entity);
	
}
