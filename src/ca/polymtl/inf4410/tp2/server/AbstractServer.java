package ca.polymtl.inf4410.tp2.server;

import java.io.InputStream;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Random;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

abstract public class AbstractServer implements ServerInterface {

	protected static Properties prop = new Properties();
	protected static InputStream input = null;
	protected static final int MAX_PERCENT = 100;
	protected static final int RMI_REGISTRY_PORT = 5002;
	protected static Random rand;
        protected static Integer m_ressource;
	
	@Override
	public int execute(int a, int b) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
	public static void main(String[] args) {
	}

	public AbstractServer() {
            rand = new Random();
	}
	
	private void parseArgs(String [] args) {
	}


	protected void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
			System.out.println("security found");
		}
		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);
                        System.out.println("Stub found");
			Registry registry = LocateRegistry.getRegistry(RMI_REGISTRY_PORT);
			System.out.println("registry found");
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

	
	public int calcul(ArrayList<ItemOperation> obj) {
		int result = 0;
		for(ItemOperation op : obj) {
                        result += (op.operation()%4000);
		}
		return result;
	}
	
		
	protected boolean checkRessource(int size){
            int randNumber = rand.nextInt(MAX_PERCENT); 
            float checkin = (((size - this.m_ressource)*MAX_PERCENT)/(5*this.m_ressource)) ;
            boolean check = ((((size - this.m_ressource)*MAX_PERCENT)/(5*this.m_ressource)) > randNumber);
            System.out.println("Server rand is " + randNumber +  "  " + " and qi check is : " + checkin + "     torf" +check + " size " + size);
            
            return ((((size - this.m_ressource)*MAX_PERCENT)/(5*this.m_ressource)) > randNumber);
	}
	
}
