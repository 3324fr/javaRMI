package ca.polymtl.inf4410.tp2.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class Client extends AbstractClient{

	private static int result = 0;

	public Client(ArrayList<ItemOperation> listOperation, ArrayList<String> hostnames) {
		super(listOperation, hostnames);
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
                System.out.println("Task size : " + Tasks.size());
		work(Tasks);
		return this.result;
	}
			
	private static void work(List<TaskRunnable> tasks){
		ArrayList<Thread> threads = new ArrayList<>();
		for(TaskRunnable task : tasks){
			Thread thread = new Thread(task);
			thread.start();
			threads.add(thread);
		}
		List<TaskRunnable> listTask = new ArrayList<>();
		for(Thread t : threads){ 
			try {
				t.join(TIMEOUT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(TaskRunnable t : tasks) {
			if(!t.getIsValidResult()) {
				listTask.add(t);
			} else {
				result = (result+t.getReturnValue())%4000;
			}
		}
		if(tasks.size() != 0) {
			work(listTask);
		}
	}
}
