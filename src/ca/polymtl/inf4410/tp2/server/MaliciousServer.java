package ca.polymtl.inf4410.tp2.server;

import java.io.FileInputStream;
import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Random;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;
import ca.polymtl.inf4410.tp2.shared.ServerInterface;

public class MaliciousServer extends AbstractServer {
	
	private static Integer m_defect;
	private static Integer m_ressource;
	protected static final int MAX_ERROR = 4000;
	
	public static void main(String[] args) {
		parseArgs(args);
		checkArgs();
		MaliciousServer server = new MaliciousServer();
		server.run();
	}
	
	private static void checkArgs() {
		// TODO Auto-generated method stub
		if(m_defect == null || m_ressource == null) {
			System.err.print("Missing args");
			System.exit(-1);
		}
	}

	@Override
	public int receiveOperation(List<ItemOperation> ops) {
		// TODO Auto-generated method stub
		Random trueResult = new Random();
		if(trueResult.nextInt(MAX_PERCENT) > m_defect) {
			return calcul(ops);
		} else {
			return trueResult.nextInt(Integer.MAX_VALUE%MAX_ERROR);
		}
	}
	
	/**
	 * parse args
	 */
	private static void parseArgs(String[] args){
		String arg = null;
		for(int i = 0; i < args.length; i++){
			arg = args[i];
			//DEFECT
			if(arg.startsWith("-d")){
				m_defect = Integer.valueOf(args[++i]);
			}
			//RESSOURCE
			if(arg.startsWith("-r")){
				m_ressource = Integer.valueOf(args[++i]);
			}
		}
	}
}
