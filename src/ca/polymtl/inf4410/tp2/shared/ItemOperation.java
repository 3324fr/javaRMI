package ca.polymtl.inf4410.tp2.shared;


abstract public class  ItemOperation{
	public int value;
    public ItemOperation(int val){
        this.value = val;
    }
    public ItemOperation(String val){
        this.value = Integer.valueOf(val);
    }
    public abstract int operation();
    
}
