package be.kpoint.pictochat.util;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public abstract class WeakReferenceHandler<T> extends Handler
{
    private WeakReference<T> reference;

    public WeakReferenceHandler(T reference)
    {
        this.reference = new WeakReference<T>(reference);
    }

    @Override
    public void handleMessage(Message msg)
    {
    	T reference = this.reference.get();
        if (reference == null)
            return;
        
        handleMessage(reference, msg);
    }

    protected abstract void handleMessage(T reference, Message msg);
}
