package rpc.framework;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import rpc.util.RpcTools;

public class RpcClientInvokerHandler extends RpcInvokerHandler {
	private static final String TAG = "RpcInvokerHandler";
	private static final boolean DEBUG = RpcTools.DEBUG;
	private ConcurrentHashMap<Integer, RpcInvoker> mWaitResultInvokerList = new ConcurrentHashMap<Integer, RpcInvoker>();
	

    @Override
	public synchronized boolean shutdown() {
		boolean ret =  super.shutdown();
		mWaitResultInvokerList.clear();
		return ret;
	}
    
    @Override
    public RpcInvoker invoke(RpcInvoker invoker) throws InterruptedException {
		if (invoker.needWaitingResponse()) {
            mWaitResultInvokerList.put(invoker.getId(), invoker);
		}
		super.invoke(invoker);
        return invoker;
	}
    
    @Override
    public RpcInvoker retrieve() throws InterruptedException {
    	RpcInvoker invoker = super.retrieve();
    	if (invoker == null) { return null; }
		RpcInvoker waitingInvoker = null;
		if (mWaitResultInvokerList.containsKey(invoker.getId())) {
			waitingInvoker = mWaitResultInvokerList.get(invoker.getId());
			mWaitResultInvokerList.remove(invoker.getId());
		}
		if (waitingInvoker != null) {
			waitingInvoker.notifyResponse(invoker.getRequest());
			return waitingInvoker;
		} else {
			return invoker;
		}
	}
}
