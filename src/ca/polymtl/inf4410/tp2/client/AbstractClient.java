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
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.Pell;
import ca.polymtl.inf4410.tp2.shared.Prime;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

abstract public class AbstractClient {


	private final static String DISTANTHOSTNAMEFILE = "host.csv";
	private final static int PORT = 5002;

	protected static final int TIMEOUT = 9000;
	protected static HashMap<String,ServerStub> distantServerStubs = null;
	protected static ArrayList<TaskRunnable> Tasks = null;

	private Iterator<Entry<String, ServerStub>> iterator;


	public AbstractClient(ArrayList<ItemOperation> listOperation, ArrayList<String> hosts ) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		distantServerStubs = new HashMap<>();
		for(String host : hosts){
			String[] parts = host.split(" ");
			ServerInterface stub = loadServerStub(parts[0]);
			if(stub == null){
				System.err.print("Stub is null. EXITING");
				continue;
			}
			int qi = Integer.valueOf(parts[1]);
			distantServerStubs.put(host,new ServerStub(stub,parts[0],qi));
		}

		int size = listOperation.size();
		Tasks = new ArrayList<>();
		int i = 0;
		while(i < size){
			ServerStub serverStub = getDistantServerStub();
			int chunk = serverStub.qi*2;
			int j = (i + chunk);
			if(j > size)
				j = size;
			System.out.println("i : " + i);
			System.out.println("j : " + j);
			ArrayList<ItemOperation> test = new ArrayList<ItemOperation>(listOperation.subList(i,j));
			printList(test);
			TaskRunnable task = new TaskRunnable(test,serverStub);
			Tasks.add(task);
			i = j;                    
		}


	}

	private void printList(ArrayList<ItemOperation> list){
		for(ItemOperation op : list){
			System.out.println("In printing test : "  + op.value);
		}
	}

	private  ServerStub getDistantServerStub(){
		if(iterator != null && iterator.hasNext()){
			Map.Entry<String, ServerStub> entry = iterator.next();
			return entry.getValue();
		}
		else{
			iterator = distantServerStubs.entrySet().iterator();
			return  getDistantServerStub();
		}
	}
	protected static  ServerStub getDistantServerStub(int qi){
		for(Map.Entry<String, ServerStub> entry : distantServerStubs.entrySet()){
			System.out.println("entry.getValue().qi " + entry.getValue().qi + " qi " + qi);
			if(entry.getValue().qi >= qi)
				return entry.getValue();
		}
		return null;
	}

	protected class ServerStub{	
		public ServerStub(ServerInterface stub, String hostname, int qi) {
			this.stub = stub;
			this.hostname = hostname;
			this.qi = qi;
		}
		private final String hostname;
		private final ServerInterface stub;
		private final int qi;
		public String getHostname() {
			return hostname;
		}
		public ServerInterface getStub() {
			return stub;
		}
		public int getQi() {
			return qi;
		}
	}


	protected class TaskRunnable implements Runnable {
		public String hostname;
		private ScheduledExecutorService scheduler;
		private ArrayList<ItemOperation> listOperation;
		private int returnValue = 0;
		private Thread t;
		private ServerStub serverStub;
		private Boolean isValidResult = false;
		private int counter = 0;
		private int MAX_THREAD_SIZE = 4;
		private final int ALIVE_RESPONSE = 7;
		private int serverAliveResponse = ALIVE_RESPONSE;
		
		public Boolean getIsValidResult() {
			return isValidResult;
		}

		TaskRunnable(ArrayList<ItemOperation> listOps, ServerStub serverStub) {
			this.listOperation = listOps;
			this.serverStub = serverStub;
			this.scheduler = Executors.newScheduledThreadPool(1);
		}

		public int getReturnValue(){
			return this.returnValue;
		}

		private void start2(ArrayList<ItemOperation> listOperation){
			++counter;
			if(counter > MAX_THREAD_SIZE){
				System.out.println("Erreur: Impossible de trouver un serveur pour la tâche.");
				System.exit(-2);

			}
			System.out.println("In Start2 fired-----");
			int listOperationSize = listOperation.size();  
			this.serverStub = getDistantServerStub();
			if(this.serverStub.qi*5 > listOperationSize){
				this.run();
			}
			else{

				int halfsize =listOperationSize/2;
				ServerStub serverStub1 = getDistantServerStub();
				ServerStub serverStub2 = getDistantServerStub();

				ArrayList<ItemOperation> list1 = new ArrayList<ItemOperation>(listOperation.subList(0, halfsize));
				ArrayList<ItemOperation> list2 = new ArrayList<ItemOperation>(listOperation.subList(halfsize, listOperationSize));

				TaskRunnable task1 = new TaskRunnable(list1,serverStub1);
				TaskRunnable task2 = new TaskRunnable(list2,serverStub2);

				task1.start();
				task2.start();

				try {
					task1.t.join(TIMEOUT);
					calculValue(task1.getReturnValue());
				} catch (InterruptedException e) {
					task1.isValidResult = false;					
				}
				if(!task1.isValidResult)
					start2(task1.listOperation);
				try {
					task2.t.join(TIMEOUT);
					calculValue(task2.getReturnValue());
				} catch (InterruptedException e) {
					task2.isValidResult = false;					
				}

				if(!task2.isValidResult)
					start2(task2.listOperation);

			}

		}

		public void run() {

			scheduler.scheduleAtFixedRate(new  Runnable() {
				@Override
				public void run() {
					if(!(serverAliveResponse == ALIVE_RESPONSE)){
						System.out.println("There is a breakdown. Restarting op");
						t.interrupt();
						start2(listOperation);
					}
					serverAliveResponse = 0;
					String hostName = serverStub.getHostname();
					try {
						serverAliveResponse = distantServerStubs.get(hostName).getStub().execute(4, 3);
					} catch (RemoteException e) {
						System.out.println("Le serveur " + hostName + " ne repond pas : "  + e.getMessage());
					}
					
				}
			},1000, 1000,TimeUnit.MILLISECONDS);


			if( this.serverStub != null){
				try {
					System.out.println("In serverStub "+serverStub.hostname+". Execute fired-----");
					int reponse =  this.serverStub.getStub().receiveOperation(listOperation.toArray(new ItemOperation[listOperation.size()]));
					System.out.println("In serverStub "+serverStub.hostname+". Run result -----" + reponse);
					calculValue(reponse);

					isValidResult = true;
				} catch (RemoteException e) {
					isValidResult = false;
					System.out.println("Erreur: "  + e.getMessage());	
				} finally {

					if(!isValidResult){
						isValidResult = true;
						start2(listOperation);
					}
				}

			}

		}
		public void start(){

			if (t == null) {
				t = new Thread (this);
				System.out.println("In Thread t start fired-----");
				t.start ();
			}

		}

		private void calculValue(int value){
			System.out.println("in calcul: " + value + " fdsfsdf " + this.returnValue);
			this.returnValue = (this.returnValue+value)% 4000;
		}
	}

	public void run() {	
		long start = System.nanoTime();
		int result = appelRMIDistant();
		long end = System.nanoTime();

		System.out.println("Temps Ã©coulÃ© appel RMI distant: "
				+ (end - start) + " ns");
		System.out.println("RÃ©sultat appel RMI distant: " + result);	

	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname, PORT);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
			+ "' n'est pas dÃ©fini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	abstract protected int appelRMIDistant();

	protected static ArrayList<String> readIPFile() throws IOException {
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
	public static ArrayList<ItemOperation> readOperationFile(String filename) throws IOException {
		ArrayList<ItemOperation> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try {

			String line = br.readLine();
			while (line != null) {   
				String[] parts = line.split(" ");
				switch (parts[0]) {
				case "prime":  list.add(new Prime(parts[1]));
				break;
				case "pell":  list.add(new Pell(parts[1]));
				break;
				default:
					break;
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
