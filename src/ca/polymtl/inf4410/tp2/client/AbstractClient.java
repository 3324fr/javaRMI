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
import java.util.LinkedList;
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

	public AbstractClient(List<ItemOperation> listOperation, List<String> hosts ) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		distantServerStubs = new HashMap<>();
		for(String host : hosts){
			String[] parts = host.split(" ");
			ServerInterface stub = loadServerStub(parts[0]);
			int qi = Integer.valueOf(parts[1]);
			distantServerStubs.put(host,new ServerStub(stub,parts[0],qi));
		}

		int size = listOperation.size();
		Tasks = new ArrayList<>();
		int i = 0;
		while(i < size){
			ServerStub serverStub = getDistantServerStub();
			int chunk = serverStub.qi;
			int j = (i + chunk-1)%size;
			TaskRunnable task = new TaskRunnable(listOperation.subList(i,j),serverStub);
			Tasks.add(task);
			i = j+1;
		}

	}

	private final static String DISTANTHOSTNAMEFILE = "host.csv";
	private final static int PORT = 5002;

	protected HashMap<String,ServerStub> distantServerStubs = null;
	protected ArrayList<TaskRunnable> Tasks = null;

	private Iterator<Entry<String, ServerStub>> iterator;

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
	private  ServerStub getDistantServerStub(int qi){
		for(Map.Entry<String, ServerStub> entry : distantServerStubs.entrySet()){
			if(entry.getValue().qi <= qi)
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
		private String hostname;
		private ServerInterface stub;
		private int qi;
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
		public ServerInterface stub;
		private ScheduledExecutorService scheduler;
		private  List<ItemOperation> listOperation;
		private int returnValue = 0;
		private Thread t;
		private ServerStub serverStub;
		private Boolean isValidResult = false;

		public Boolean getIsValidResult() {
			return isValidResult;
		}

		TaskRunnable(List<ItemOperation> listOps, ServerStub serverStub) {
			this.listOperation = listOps;
			this.scheduler = Executors.newScheduledThreadPool(1);
			this.serverStub = serverStub;
		}

		public int getReturnValue(){
			return this.returnValue;
		}

//		private void split(){
//			int listOperationSize = listOperation.size();
//			int halfsize =listOperationSize/2;
//			if(halfsize > 3){
//				ServerStub serverStub2 = getDistantServerStub(halfsize);
//				this.serverStub = getDistantServerStub(halfsize+1);
//				if(serverStub2 != null){
//					TaskRunnable task2 = new TaskRunnable(this.listOperation.subList(0, halfsize-1),serverStub2);
//					listOperation = listOperation.subList(halfsize, listOperation.size());
//					Thread thread2= new Thread(task2);
//					thread2.start();
//				}
//			}
//		}
//
//		private void start2()
//		{
//			if(!isValidResult){
//				TaskRunnable task2 = split();
//				if(task2 != null){
//					Thread thread2= new Thread(task2);
//					thread2.start();
//					this.run();
//					try {
//						thread2.join();
//						this.returnValue =+ task2.getReturnValue()% 4000;
//						task2.isValidResult = true;
//					} catch (InterruptedException e) {
//						task2.isValidResult = false;					
//					}
//					task2.start2();
//				}
//				this.run();
//			}
//		}
		
//		public void start2(){
//			this.serverStub = getDistantServerStub(listOperation.size());
//			if(serverStub2 != null){
//				
//			}
//			
//		}

		public void run() {
			try {
				returnValue =  serverStub.getStub().receiveOperation(listOperation);
				scheduler.shutdown();
				this.returnValue =+ this.getReturnValue()% 4000;
				isValidResult = true;
				return;
			} catch (RemoteException e) {
				isValidResult = false;
				System.out.println("Erreur: "  + e.getMessage());	
			}
			//start2();
		}
		public void start(){

			if (t == null) {
				t = new Thread (this);
				t.start ();
			}
			scheduler.schedule(new  Runnable() {
				@Override
				public void run() {
					if(!checkServerBreakdown(serverStub.getHostname())){
						//start2();
					}				
				}
			},5,TimeUnit.SECONDS);
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
			Registry registry = LocateRegistry.getRegistry(hostname, PORT);
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
			return distantServerStubs.get(hostName).getStub().execute(4, 3) ==7;
		} catch (RemoteException e) {
			System.out.println("Le serveur " + hostName + " ne r�pond pas : "  + e.getMessage());
		}
		return false;
	}


	abstract protected int appelRMIDistant();

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
