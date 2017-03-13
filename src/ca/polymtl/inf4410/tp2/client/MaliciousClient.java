package ca.polymtl.inf4410.tp2.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.polymtl.inf4410.tp2.client.AbstractClient.TaskRunnable;
import ca.polymtl.inf4410.tp2.shared.ItemOperation;

public class MaliciousClient extends AbstractClient{

	private static int result = 0;

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
		System.out.println("Task size : " + Tasks.size());
		List<MaliciousTask> maliciousTasks= new ArrayList<>();
		for(TaskRunnable task : Tasks){
			maliciousTasks.add(new MaliciousTask(task));
		}
		work(maliciousTasks);
		return MaliciousClient.result;
	}

	private static void work(List<MaliciousTask> tasks){
		if(!tasks.isEmpty()){
			ArrayList<Thread> threads = new ArrayList<>();
			List<MaliciousTask> tasksReturn= new ArrayList<>();
			for(MaliciousTask task : tasks){
				Thread thread = new Thread(task.task);
				thread.start();
				threads.add(thread);
			}
			for(Thread t : threads){ 
				try {
					t.join(TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for(MaliciousTask t : tasks) {
				int taskresult = t.task.getReturnValue();
				Boolean add = true;
				if(t.task.getIsValidResult()) {
					if(t.listResults.contains(taskresult)){
						MaliciousClient.result = (MaliciousClient.result + taskresult)%4000;
						add = false;
					}
					t.listResults.add(taskresult);
				}
				if(add){
					tasksReturn.add(t);
				}	
			}
			work(tasksReturn);
		}
	}


	private class MaliciousTask{
		public MaliciousTask(TaskRunnable t){
			this.listResults = new ArrayList<>();
			this.task =t;
		}
		public List<Integer> listResults;
		public TaskRunnable task;
	}


}
