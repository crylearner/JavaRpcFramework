package rpc.component;

import org.json.JSONException;
import org.json.JSONObject;

import rpc.json.RpcResponse;
import rpc.util.RpcLog;
import rpc.util.RpcTools;

/**
 * @author 23683
 *
 */
public class Client extends RpcComponent {
    public static final String NAME = "global";
    private final boolean DEBUG = RpcTools.DEBUG;
    private boolean mLogin = false;
    private int mTimeout = 25;//one minute
    private String mRealm = null;
	private KeepAliveThread mKeepAliveThread = new KeepAliveThread();
    
    public static final String METHOD_LOGIN = NAME+".login";
    public static final String METHOD_LOGOUT = NAME+".logout";
    private static final String METHOD_KEEPALIVE = NAME+".keepAlive";
    
    public Client() {
    }
    
    @Override
	public boolean instance(JSONObject params) {
    	// 全静态类，所有方法不依赖于object
    	return true;
	}

	@Override
	public boolean destroy(JSONObject params) {
		mKeepAliveThread.interrupt();
		try {
			mKeepAliveThread.join();
		} catch (InterruptedException e) {
			
		}
		return true;
	}
	
	/**
	 * 
	 * @param name
	 * @param password 未做过任何加密的原始明文密码
	 * @return
	 */
    public boolean login(String name, String password) {
    	try {
    		RpcResponse rsp = internalLogin(name, password);
			if (rsp.isSuccessful()) {
				mLogin = true;
				mKeepAliveThread.setName("KeepAliveThread");
				mKeepAliveThread.setDaemon(true); // 当Client销毁时，保活线程也同步销毁
	        	mKeepAliveThread.start();
			}
			return rsp.isSuccessful();
        } catch (JSONException e) {
        	RpcLog.w(NAME , "JSONException: login()");
        }
    	return false;
    }
    
    public boolean logout(){
        if(!mLogin){
            return false;
        }
    	try {
    		mKeepAliveThread.interrupt();
			mKeepAliveThread.join();
		} catch (InterruptedException e) {
		}
    	
        RpcResponse result = static_call(METHOD_LOGOUT, null, DEFAULT_WAIT_TIME);
        if (result == null || !result.isSuccessful()) {
        	RpcLog.e(TAG, "Logout failed\n");
        }
        return result.isSuccessful();
    }

    /**
     * 
     * @param keepalive_timeout
     * @return -1 means 保活失败。 其他，服务器返回的最大保活周期
     */
    public int keepAlive(int keepalive_timeout) {
    	if (!mLogin){
            return -1;
        }
    	JSONObject params = new JSONObject();
        try {
			params.put("timeout", keepalive_timeout);
			RpcResponse result = static_call(METHOD_KEEPALIVE, params, DEFAULT_WAIT_TIME);
			if (result.isSuccessful()) {
				return ((JSONObject)result.getResult()).getInt("timeout");
			} else {
				RpcLog.e(TAG, "keep alive failed");
				return -1;
			}
    	} catch (JSONException e) {
			e.printStackTrace();
		}
        return -1;
    }
    
    private RpcResponse internalLogin(String name, String password) {
        try {
            JSONObject params = new JSONObject();
            params.put("userName", name);
            params.put("password", password);
            params.put("ipAddr", "127.0.0.1");
            RpcResponse result = static_call(METHOD_LOGIN, params, DEFAULT_WAIT_TIME);
            return result;
        } catch (JSONException e) {
            RpcLog.w(NAME , "JSONException: login()");
        }
        return null;
    }
    
    private class KeepAliveThread extends Thread {
    	@Override
    	public void run() {
    		int timeout = 20; //客户端预想的保活时间，实际还是得取决于服务器返回的保活时间
			while(mLogin) {
				timeout = keepAlive(timeout);
				if (timeout == -1) {
					RpcLog.d(TAG, "keep alive failed, so session will be destroyed");
					// FIXME::不直接在这里销毁session，而是由Session自己去发现保活失败
					break;
				}
				try {
					Thread.sleep(timeout * 1000/2 ); // 保证一个保活周期内必然有一个保活包 
				} catch (InterruptedException e) {
					break;
				} 
			}
		} 
    }
    
    
    @Override
    protected void initMethods() {
        mMethods.add(METHOD_KEEPALIVE);
        mMethods.add(METHOD_LOGIN);
        mMethods.add(METHOD_LOGOUT);
    }
}
