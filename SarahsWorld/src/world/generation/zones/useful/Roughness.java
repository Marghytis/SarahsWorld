package world.generation.zones.useful;

public class Roughness {
	
	private Modulation[] modulations;
	double sum;
	
	public Roughness(Modulation... modulations){
		this.modulations = modulations;
	}

	public double next(){
		sum = 0;
		for(Modulation m: modulations){
			sum += m.next();
		}
		return sum;
	}
}
