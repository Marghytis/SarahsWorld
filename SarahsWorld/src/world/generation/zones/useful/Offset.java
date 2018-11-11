package world.generation.zones.useful;

public class Offset extends Modulation {

	public Offset(double amplitude) {
		super(null, -1, amplitude);
	}

	public double next() {
		return amplitude;
	}

}
