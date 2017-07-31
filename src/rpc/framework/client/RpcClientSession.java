package rpc.framework.client;


import java.util.concurrent.atomic.AtomicBoolean;

import rpc.component.RpcComponent;
import rpc.framework.INotificationListener;
import rpc.framework.IResultListener;
import rpc.framework.RpcClientInvokerHandler;
import rpc.framework.RpcInvoker;
import rpc.framework.RpcListenerManager;
import rpc.framework.connection.IRpcChannel;
import rpc.framework.message.RpcMessage;
import rpc.json.RpcNotification;
import rpc.json.RpcRequest;
import rpc.json.RpcResponse;
import rpc.util.IDGenerator;
import rpc.util.RpcLog;
import rpc.util.RpcTools;

/**
 * 相当于Session的抽象，提供消息发送，提供通知订阅，自我异常管理
 * @author 23683
 *
 */
public class RpcClientSession implements IRpcClientSession {

    public static final String TAG = "RpcClientSession";
    public static final boolean DEBUG = RpcTools.DEBUG;
	private AtomicBoolean mStart = new AtomicBoolean(false);
    private int mSessionId = 0;
    private IDGenerator mRequestIDGen = new IDGenerator();
    private RpcClientInvokerHandler mInvokerHandler = new RpcClientInvokerHandler();
    private RpcListenerManager mListenerManager = new RpcListenerManager();

	public RpcClientSession() {
    }
	
	
    /* (non-Javadoc)
     * @see rpc.framework.client.IRpcClientSession#request(rpc.json.RpcRequest, rpc.component.RpcComponent, int)
     */
    @Override
	public RpcResponse request(RpcRequest request, RpcComponent component, int timeout) throws InterruptedException {
    	RpcInvoker invoker = new RpcInvoker(request, component, null);
		RpcInvoker result = mInvokerHandler.invoke(invoker);
		if (!result.waitingResponse(timeout)) { // FIXME::检查返回值？
			RpcLog.e("TAG", "waiting response timeout: " + request);
		}
		return (RpcResponse)result.getResponse();
    }
    
    @Override
	public boolean asyncrequest(RpcRequest request, RpcComponent component, IResultListener listener) throws InterruptedException {
    	RpcInvoker invoker = new RpcInvoker(request, component, listener);
		mInvokerHandler.invoke(invoker);
		return true;
    }

    @Override
	public int regEventListener(INotificationListener listener) {
    	return mListenerManager.register(listener);
    }

    @Override
	public int unregEventListener(INotificationListener listener) {
    	return mListenerManager.unregister(listener);
    }
    
    @Override
    public int getCallid(INotificationListener listener) {
    	return mListenerManager.calcuCallid(listener);
    }
    
    @Override
	public int getSessionId() {
        return mSessionId;
    }

    @Override
    public void setSessionId(int sessionId) {
        this.mSessionId = sessionId;
    }

	@Override
	public int getRequestId() {
		return mRequestIDGen.getRequestId();
	}
    
	@Override
	public synchronized void start(IRpcChannel channel) {
		if (mStart.get()) { return; }
		RpcLog.d(TAG, "rpc client session is started");
        mStart.set(true);
        mInvokerHandler.bindChannel(channel);
        mInvokerHandler.startup();
	}
	
	@Override
    public void run() {
        while(mStart.get()) {
			try {
				RpcInvoker result = mInvokerHandler.retrieve();
				if (result!=null && !consumeResponse(result)) {
					RpcLog.e(TAG, "Can't operate the response!");
					continue; //FIXME:: stop thread ?
				};
			} catch (InterruptedException e) {
				RpcLog.e(TAG, "retieve fail, stop client session");
				RpcClientSession.this.stop();
			}
		}
        onDestroy();
    }
	
	@Override
	public synchronized void stop() {
		mStart.set(false);
		mInvokerHandler.shutdown();
    	mListenerManager.clear();
        mSessionId = 0;
		mRequestIDGen.reset();
		RpcLog.d(TAG, "rpc client session is stop");
	}
    
    @Override
    public synchronized void onDestroy() {
    	RpcLog.d(TAG, "rpc client session has been released");
    }
    
    
    private boolean consumeResponse(RpcInvoker invoker) {
        RpcMessage message = invoker.getRequest();
        RpcMessage response = invoker.getResponse();
        
        if ((message instanceof RpcRequest) && (response instanceof RpcResponse)) {
        	// 请求
        	//System.out.println("consumeResponse request");
        	processResponse((RpcRequest)message, (RpcResponse)invoker.getResponse(), invoker.getListener());
        
        } else if ((message instanceof RpcNotification)) {
        	// 通知	
        	//System.out.println(" consumeResponse notify");
        	processEvent((RpcNotification)message);
        	
        } else if ((message instanceof RpcRequest) && (response instanceof RpcNotification)) {
        	// 通知    这种情况也有可能发生。
        	// 如果attach之后，在没有response之前，有与该attach对应的notification，则会出现这种情况
        	//System.out.println(" consumeResponse notify");
        	processEvent((RpcNotification)response);
        	
        } else {
        	RpcLog.e(TAG, "unknown invoker when consume response");
        	return false;
        }
        return true;
    }


	private boolean processResponse(RpcRequest request, RpcResponse response, IResultListener listener) {
		if (response != null && request != null && listener != null) {
		    listener.onResult(request.getMethod(), response);
            return true;
        }
		//RpcLog.e(TAG, "Receive a response but no one can deal with it:\n" + response);
        return false;
		
	}

	private boolean processEvent(RpcNotification event) {
		INotificationListener listener = mListenerManager.getListener(event.getCallback());
		if (listener == null) {
			RpcLog.e(TAG, "Receive a notification but no listener can deal with it:\n" + event);
			return false;
		}
        return listener.onNotify(event);
    }
    

}
