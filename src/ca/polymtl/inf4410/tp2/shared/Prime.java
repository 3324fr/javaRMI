package ca.polymtl.inf4410.tp2.shared;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.Operations;
public class Prime extends ItemOperation{

	public Prime(String parts) {
		super(parts);
	}

	@Override
	public int operation() {
		return Operations.prime(this.value);
	}
	
}
