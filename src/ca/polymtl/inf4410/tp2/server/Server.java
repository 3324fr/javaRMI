
package ca.polymtl.inf4410.tp2.server;

import java.io.FileInputStream;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.Pell;
import ca.polymtl.inf4410.tp2.shared.Prime;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class Server extends AbstractServer {


	
	public static void main(String[] args) {
		parseArgs(args);
		checkArgs();
		Server server = new Server();
		server.run();
	
	}
	
	public Server() {
                super();
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}
	
	private static void checkArgs() {
		// TODO Auto-generated method stub
		if(m_ressource == null) {
			System.err.print("Missing args");
			System.exit(-1);
		}
	}

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	@Override
	public int execute(int a, int b) throws RemoteException {
		return a + b;
	}

	@Override
	public int receiveOperation(ItemOperation[] ops)  throws RemoteException{
		// TODO Auto-generated method stub
		if(checkRessource(ops.length + 1)) {
			throw new RemoteException("ops size bigger than ressources");
		}
		return calcul(new ArrayList(Arrays.asList(ops)));
	}

	/**
	 * parse args
	 */
	private static void parseArgs(String[] args){
		String arg = null;
		for(int i = 0; i < args.length; i++){
			arg = args[i];
			//RESSOURCE
			if(arg.startsWith("-r")){
				m_ressource = Integer.valueOf(args[++i]);
			}
		}
		if(args.length <= 1){
                    System.out.println("no args");
		}
	}
}
