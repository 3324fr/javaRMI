package ca.polymtl.inf4410.tp2.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.Pell;
import ca.polymtl.inf4410.tp2.shared.Prime;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

abstract public class AbstractClient {

	public AbstractClient(List<ItemOperation> listOperation, List<String> hostnames ) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		distantServerStub = new HashMap<>();
		for(String host : hostnames){
			if(!host.isEmpty()){
				ServerInterface stub = loadServerStub(host);
				distantServerStub.put(host,stub);
			}
		}

		Tasks = new ArrayList<>();
		int size = distantServerStub.size();
		int chunk = listOperation.size()/size;
		int i = 1;
		while(i < (size-chunk)){
			TaskRunnable task = new TaskRunnable(listOperation.subList(i, (i+=chunk)-1));
			Tasks.add(task);
			i++;
		}

		if(i < size){
			Tasks.add(new TaskRunnable(listOperation.subList( i,size)));
		}

	}
	

	private final static String DISTANTHOSTNAMEFILE = "hostname.txt";
	private final static int PORT = 5002;
	
	protected HashMap<String,ServerInterface> distantServerStub = null;
	protected ArrayList<TaskRunnable> Tasks = null;
	
	private Iterator<Map.Entry<String, ServerInterface>> iterator;
	
	private  Map.Entry<String,ServerInterface> getDistantServerStub(){
		if(iterator != null && iterator.hasNext()){
			   Entry<String, ServerInterface> entry = iterator.next();
			   return entry;
		}
		else{
			iterator = distantServerStub.entrySet().iterator();
			return  getDistantServerStub();
		}
	}

	
	
	
	public class TaskRunnable implements Runnable {
		public String hostname;
		public ServerInterface stub;
		private ScheduledExecutorService scheduler;

		private  List<ItemOperation> listOperation;
		private int returnValue;
		private Thread t;

		TaskRunnable(List<ItemOperation> listOps) {
			this.listOperation = listOps;
			this.scheduler = Executors.newScheduledThreadPool(1);
			Map.Entry<String,ServerInterface> entry = getDistantServerStub(); 
			this.stub =  entry.getValue();
			this.hostname = entry.getKey();
		}

		public int getReturnValue(){
			return this.returnValue;
		}
		
		public TaskRunnable split(){
			int halfsize = listOperation.size()/2;
			if(halfsize > 2){
			TaskRunnable task = new TaskRunnable(this.listOperation.subList(0, halfsize));
			listOperation = listOperation.subList(halfsize, listOperation.size());
			return task;
			}
			return null;
		
		}

		public void run() {	
			try {
				returnValue =  distantServerStub.get(hostname).receiveOperation(listOperation);
				scheduler.shutdown();
			} catch (RemoteException e) {
				System.out.println("Erreur: "  + e.getMessage());
				t.interrupt();
			}

		}
		
		public void start(){
			
			if (t == null) {
		         t = new Thread (this);
		         t.start ();
		      }
			scheduler.schedule(new  Runnable() {
						@Override
						public void run() {
							if(!checkServerBreakdown(hostname));{
								t.interrupt();
							}
							
						}
				    },2,TimeUnit.SECONDS);
		}
	}
	
	

	public void run() {	
		long start = System.nanoTime();
		int result = appelRMIDistant();
		long end = System.nanoTime();

		System.out.println("Temps écoulé appel RMI distant: "
				+ (end - start) + " ns");
		System.out.println("Résultat appel RMI distant: " + result);	

	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
			+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private Boolean checkServerBreakdown(String hostName){
			try {
				return distantServerStub.get(hostName).execute(4, 7) ==7;
			} catch (RemoteException e) {
				System.out.println("Erreur: "  + e.getMessage());
			}
			return false;
	}
	

	abstract protected int appelRMIDistant();
	abstract protected void stopRunnable(int i);

	protected static List<String> readIPFile() throws IOException {
		ArrayList<String> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(DISTANTHOSTNAMEFILE));
		try{
			String line = br.readLine();
			while (line != null) {   
				list.add(line);
				line = br.readLine();
			}
		}
		finally{
			br.close();
		}
		return list;
	}
	protected static List<ItemOperation> readOperationFile(String filename) throws IOException {
		ArrayList<ItemOperation> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {

			String line = br.readLine();
			String[] parts = line.split(" ");
			while (line != null) {   
				switch (parts[0]) {
				case "prime":  list.add(new Prime(parts[0]));
				break;
				case "pell":  list.add(new Pell(parts[0]));
				break;
				default:
				}
				line = br.readLine();
			}
		} 
		finally {
			br.close();
		}
		return list;
	}
	

}
