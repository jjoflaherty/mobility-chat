package be.kpoint.pictochat.network;


public interface IAsyncPostProcessor 
{
	public abstract boolean doPostProcessing();
	
	public abstract void addFinishedListener(IFinishedListener listener);
	public abstract void removeFinishedListener(IFinishedListener listener);
}
