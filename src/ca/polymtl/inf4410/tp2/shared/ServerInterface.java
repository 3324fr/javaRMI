package ca.polymtl.inf4410.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import ca.polymtl.inf4410.tp2.shared.ItemOperation;

public interface ServerInterface extends Remote {
	int execute(int a, int b) throws RemoteException;
	int receiveOperation(ItemOperation[]obj) throws RemoteException;
	//int receiveOperation(ArrayList<ItemOperation> obj) throws RemoteException;
}
