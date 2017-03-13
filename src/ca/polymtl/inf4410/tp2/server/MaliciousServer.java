package ca.polymtl.inf4410.tp2.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;

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

	public MaliciousServer() {
		super();
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}

	private static void checkArgs() {
		if(m_defect == null || m_ressource == null) {
			System.err.print("Missing args");
			System.exit(-1);
		}
	}

	@Override
	public int receiveOperation(ItemOperation[] ops) {
		Random trueResult = new Random();
		if(trueResult.nextInt(MAX_PERCENT) > m_defect) {
			return calcul(new ArrayList<ItemOperation>(Arrays.asList(ops)));
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
		if(args.length <= 1){
			System.out.println("no args");
		}
	}
}
