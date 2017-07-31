package rpc.component;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import rpc.RpcNoSuchMethodException;
import rpc.framework.INotificationListener;
import rpc.framework.IResultListener;
import rpc.framework.client.IRpcClientSession;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;
import rpc.util.RpcTools;

/**
 * 
 * @author crylearner
 *
 */
/**
 * @author sunshyran
 *
 */
public abstract class RpcComponent implements RpcComponentInterface {
    public static final String TAG = "RpcComponent";
    private static final boolean DEBUG = RpcTools.DEBUG;
    protected static final int DEFAULT_WAIT_TIME = 5000; // 单位ms
    private int mObjectId = 0;
    private IRpcClientSession mClientSession = null;
	
    protected Set<String> mMethods = new HashSet<String>();

    public RpcComponent() {
    	initMethods();
    }
    
	
	/* (non-Javadoc)
	 * @see rpc.component.RpcComponentInterface#call(java.lang.String, org.json.JSONObject, int)
	 */
	@Override
	public RpcResponse call(String method, JSONObject params, int timeout) {
		// RpcLog.i(TAG, "call " + method + " with params " + params);
		RpcResponse response = new RpcResponse(
				mClientSession.getRequestId(),
				null,
				false);
		
		if (mObjectId == 0) {
			RpcLog.e(TAG, "component is not yet instanced for object is 0");
			return response;
		}
		
		RpcRequest request = new RpcRequest(
				mClientSession.getRequestId(),
				method,
				params
				);
		
		try {
			RpcResponse res = mClientSession.request(request, this, timeout);
			return res==null ? response : res; 
		} catch (InterruptedException e) {
			RpcLog.e(TAG, "call failed: " + e);
			// FIXME:: 
			//mClientSession.destroy();
		}
		return response;
	}

	/* (non-Javadoc)
	 * @see rpc.component.RpcComponentInterface#static_call(java.lang.String, org.json.JSONObject, int)
	 */
	@Override
	public RpcResponse static_call(String method, JSONObject params, int timeout) {
		// RpcLog.i(TAG, "static_call " + method + " with params " + params);
		RpcResponse response = new RpcResponse(
				mClientSession.getRequestId(),
				null,
				new Boolean(false));
		
		RpcRequest request = new RpcRequest(
				mClientSession.getRequestId(),
				method,
				params
				);
		
		try {
			RpcResponse res = mClientSession.request(request, this, timeout);
			return res==null ? response : res;
		} catch (InterruptedException e) {
			RpcLog.e(TAG, "static_call failed: " + e);
			// TODO:: 异常出错处理 统一由创建ClientSession的线程处理
			//mClientSession.destroy();
		}
		return response;
	}

	
	/* (non-Javadoc)
	 * @see rpc.component.RpcComponentInterface#async_call(java.lang.String, org.json.JSONObject, rpc.framework.IResultListener)
	 */
	@Override
	public boolean async_call(String method, JSONObject params,
			IResultListener listener) {
		// RpcLog.i(TAG, "async_call " + method + " with params " + params + " and listener " + listener);
		RpcRequest request = new RpcRequest(
				mClientSession.getRequestId(),
				method,
				params
				);
		try {
			boolean result = mClientSession.asyncrequest(request, this, listener);
			return result;
		} catch (InterruptedException e) {
			RpcLog.e(TAG, "async_call failed: " + e);
			// TODO:: 异常出错处理
//			mClientSession.destroy();
		}
		return false;
	}
	
   
    /* (non-Javadoc)
     * @see rpc.component.RpcComponentInterface#add_subscription(java.lang.String, rpc.framework.INotificationListener, org.json.JSONObject, int)
     */
    @Override
	public boolean add_subscription(String method, INotificationListener listener, JSONObject params, int timeout) {
    	try {
    		int callid = mClientSession.getCallid(listener);
    		if (params == null) { params = new JSONObject(); }
			params.put("proc", callid);
			RpcResponse response = call(method, params, DEFAULT_WAIT_TIME);
			if (response.isSuccessful()) {
				getRpcClient().regEventListener(listener);
			}
			return response.isSuccessful();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see rpc.component.RpcComponentInterface#cancel_subscription(java.lang.String, rpc.framework.INotificationListener, org.json.JSONObject, int)
	 */
	@Override
	public boolean cancel_subscription(String method, INotificationListener listener, JSONObject params, int timeout) {
		try {
			int callid = mClientSession.getCallid(listener);
			if (params == null) { params = new JSONObject(); }
			params.put("proc", callid);
			RpcResponse response = call(method, params, DEFAULT_WAIT_TIME);
			if (response.isSuccessful()) {
				getRpcClient().unregEventListener(listener);
			}
			return response.isSuccessful();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int getObjectId() {
        return mObjectId;
    }

    protected void setObjectId(int mObjectId) {
        this.mObjectId = mObjectId;
    }

    public boolean hasInstance() {
        return mObjectId != 0;
    }
    
    public Set<String> getMethods() {
        return mMethods;
    }

    public boolean hasMethods(String method) {
        return mMethods.contains(method);
    }

    protected abstract void initMethods();

    /**
     * use async_call instead
     * @param method
     * @param param
     * @param listener
     * @return
     * @throws RpcNoSuchMethodException
     */
    @Deprecated
    public synchronized final int execMethod(String method, JSONObject param,
            IResultListener listener) throws RpcNoSuchMethodException {
        if (hasMethods(method)) {
            boolean result = async_call(method, param, listener);
			int requestid = result ? 1 : 0;
            System.out.println("Exec method: id=" + requestid + ", listener=" + listener);
            return requestid;
        } else {
            throw new RpcNoSuchMethodException(method + " is not exist!");
        }
    }

    protected IRpcClientSession getRpcClient() {
		return mClientSession;
	}

	void setRpcClient(IRpcClientSession rpcManager) {
		mClientSession = rpcManager;
	}

	protected boolean defaultInstanceMethod(String instanceMethod, JSONObject params) {
//		RpcResponse response = static_call(instanceMethod, params, DEFAULT_WAIT_TIME);
//		if (response.isSuccessful()) {
//			setObjectId(response.getObject());
//			return true;
//		} else {
//			RpcLog.e(TAG, instanceMethod + " failed");
//			return false;
//		}
		return true;
	}

	protected boolean defaultDestroyMethod(String destroyMethod, JSONObject params) {
//		RpcResponse response = call(destroyMethod, params, 100);
//		if (response.isSuccessful()) {
//			setObjectId(0);
//		}
//		return response.isSuccessful();
		return true;
	}

	/**
	 * use destroy() instead
	 */
	@Deprecated
	public void release() {
	    destroy(null);
	}

}
