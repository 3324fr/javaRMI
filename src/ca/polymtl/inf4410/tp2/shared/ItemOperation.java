package ca.polymtl.inf4410.tp2.shared;

import java.io.Serializable;

abstract public class  ItemOperation implements Serializable{
	public int value;
    public ItemOperation(int val){
        this.value = val;
    }
    public ItemOperation(String val){
        this.value = Integer.valueOf(val);
    }
    public abstract int operation();
    
}
