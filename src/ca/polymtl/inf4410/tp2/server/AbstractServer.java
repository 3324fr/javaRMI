package ca.polymtl.inf4410.tp2.server;

import java.io.InputStream;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Properties;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.Pell;
import ca.polymtl.inf4410.tp2.shared.Prime;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

abstract public class AbstractServer implements ServerInterface {

	protected static Properties prop = new Properties();
	protected static InputStream input = null;
	protected static final int MAX_PERCENT = 100;
	
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

			Registry registry = LocateRegistry.getRegistry();
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

	
	public int calcul(List<ItemOperation> obj) throws Exception {
		int result = 0;
		for(ItemOperation op : obj) {
			int value = op.value;
			if(op instanceof Pell) {
				result+= Operations.pell(value);
			} else if (op instanceof Prime){
				result += Operations.prime(value);
			} else {
				throw new Exception("Invalid operation");
			}
		}
		return result;
	}
}
