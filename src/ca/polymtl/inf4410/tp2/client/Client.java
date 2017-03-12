package ca.polymtl.inf4410.tp2.client;

import java.util.HashMap;
import java.util.List;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class Client extends AbstractClient{

	private int result = 0;

	public Client(List<ItemOperation> listOperation, List<String> hostnames) {
		super(listOperation, hostnames);
	}

	@Override
	protected int appelRMIDistant() {


		return 0;
	}
			
	private void work(List<TaskRunnable> tasks, HashMap<String,ServerInterface> servers){
		
//		ArrayList<Thread> threads = new ArrayList<>();
//		Iterator<Entry<String, ServerInterface>> it = servers.entrySet().iterator();
//		for(TaskRunnable task : tasks){
//			if(it.hasNext()){
//				Map.Entry<String,ServerInterface> entry = it.next();
//			}
//			
//				
//			task.hostname = i;
//			Thread thread = new Thread(task);
//			thread.start();
//			threads.add(thread);
//		}
//
//		for(int i = 0 ;i <threads.size();i++){ 
//			try {
//				threads.get(i).join();				
//				if( Tasks.get(i)){
//					result =+  Tasks.get(i).getReturnValue();
//				}
//				else{
//					
//				}
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}


		



}
