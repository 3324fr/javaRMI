package ca.polymtl.inf4410.tp2.shared;

import ca.polymtl.inf4410.tp2.shared.Operations;
import ca.polymtl.inf4410.tp2.shared.ItemOperation;

public class Pell extends ItemOperation{

	public Pell(String parts) {
		super(parts);
	}
	
	@Override
	public int operation() {
		return Operations.pell(this.value);
	}
	
}
