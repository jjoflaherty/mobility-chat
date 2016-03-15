package be.kpoint.pictochat.network;


public abstract class AsyncReceiverPostProcessor implements IAsyncPostProcessor
{
	protected AbstractRestResultReceiver receiver;

	public AsyncReceiverPostProcessor(AbstractRestResultReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void addFinishedListener(IFinishedListener listener) {
		receiver.addFinishedListener(listener);
	}
	@Override
	public void removeFinishedListener(IFinishedListener listener) {
		receiver.removeFinishedListener(listener);
	}
}
