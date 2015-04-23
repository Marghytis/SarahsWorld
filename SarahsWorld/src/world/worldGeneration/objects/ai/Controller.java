package world.worldGeneration.objects.ai;



public abstract class Controller extends AiPlugin {

	AiPlugin[] children;
	
	/**
	 * 
	 * @param t
	 * @param children Only to make it possible to render them
	 */
	public Controller(Thing t, AiPlugin... children){
		super(t);
		this.children = children;
	}

	public void partRender(){
		for(AiPlugin plug : children){
			plug.partRender();
		}
	}

	public String save() {
		return null;
	}

	public void load(String save) {
		
	}
	
}
