package ca.polymtl.inf4410.tp2.server;

import java.io.InputStream;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ca.polymtl.inf4410.tp2.client.AbstractClient.TaskRunnable;
import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

abstract public class AbstractServer implements ServerInterface {

	protected static Properties prop = new Properties();
	protected static InputStream input = null;
	protected static final int MAX_PERCENT = 100;
	protected static final int RMI_REGISTRY_PORT = 5002;
	
	@Override
	public int execute(int a, int b) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	public static void main(String[] args) {
	}

	public AbstractServer() {
	}
	
	private void parseArgs(String [] args) {
	}


	protected void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry(RMI_REGISTRY_PORT);
			
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}

	}

	
	public int calcul(List<ItemOperation> obj) {
		int result = 0;
		for(ItemOperation op : obj) {
				result += op.operation();
		}
		return result;
	}
	

	
	protected class TaskRunnable implements Runnable {
		private ScheduledExecutorService scheduler;

		private  List<ItemOperation> listOperation;
		private int returnValue;
		private Thread t;

		TaskRunnable(List<ItemOperation> listOps) {
			this.listOperation = listOps;
			this.scheduler = Executors.newScheduledThreadPool(1);
		}

		
		public void start(){
			
			if (t == null) {
		         t = new Thread (this);
		         t.start ();
		    }
			scheduler.schedule(new  Runnable() {
						@Override
						public void run() {
							
							
						}
				    },2,TimeUnit.SECONDS);
		}


		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
}
