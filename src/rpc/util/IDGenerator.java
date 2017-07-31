package rpc.util;

public class IDGenerator {
	private int mRequestID = 0;

	public synchronized void reset() {
		mRequestID = 0;
	}

	public synchronized int getRequestId() {
	    return ++mRequestID;
	}

}
