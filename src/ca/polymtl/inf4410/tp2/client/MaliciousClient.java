package ca.polymtl.inf4410.tp2.client;

import java.io.IOException;
import java.util.ArrayList;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;

public class MaliciousClient extends AbstractClient{


	public MaliciousClient(ArrayList<ItemOperation> listOperation, ArrayList<String> hostnames) {
		super(listOperation,hostnames);
	}

	public static void main(String[] args) {

		String operationFile = null;
		if (args.length > 0) {
			operationFile = args[0];
		}
		ArrayList<ItemOperation> listOperation = null;
		ArrayList<String> hostnames = null;
		try {
			listOperation = readOperationFile(operationFile);
			hostnames = readIPFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		MaliciousClient client = new MaliciousClient(listOperation,hostnames);
		client.run();

	}

	@Override
	protected int appelRMIDistant() {
		// TODO Auto-generated method stub
		return 0;
	}


}
