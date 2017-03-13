package ca.polymtl.inf4410.tp2.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf4410.tp2.client.AbstractClient.TaskRunnable;
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
			listOperation = AbstractClient.readOperationFile(operationFile);
			hostnames = readIPFile();

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		Client client = new Client(listOperation,hostnames);
		client.run();
		System.out.println("Done");
		System.exit(0);

	}

	@Override
	protected int appelRMIDistant() {
		return 0;
	}
			


}
