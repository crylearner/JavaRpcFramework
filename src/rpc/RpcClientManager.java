package rpc;

import java.util.HashMap;

import org.json.JSONObject;

import rpc.component.Client;
import rpc.component.RpcComponent;
import rpc.component.RpcComponentFactory;
import rpc.component.RpcComponentInterface;
import rpc.framework.client.RpcClientSession;
import rpc.framework.connection.IRpcChannel;
import rpc.framework.connection.RpcConnector;
import rpc.util.RpcLog;
import rpc.util.RpcTools;

/**
 * 负责统筹管理session，component，以及登入 保活 等工作。外部客户使用，只能直接与该类打交道，不要直接去创建session和client
 * 使用说明：
 * login 登入Rpc服务端，建立一条唯一的连接。如果已登入，则直接返回。
 * logout 登出Rpc服务端
 * onClientSessionDestroyed  当Rpc连接断开后，会调用此方法。
 * getComponent 获取组件。只有登入服务端后才能获取到，否则直接返回null
 * 
 * 内部原理：
 * RpcClientManager与IClientSession一一对应，属于组合关系。RpcClientManager负责维护IClientSession对象的生命周期。
 * 当IClientSession被销毁时，RpcClientManager无需销毁，只要通过login()重新获取新的session就可以再次使用。
 * @author crylearner
 *
 */
public class RpcClientManager {
    public static final String TAG = "RpcClientManager";
    public static final boolean DEBUG = RpcTools.DEBUG;
    public RpcClientSession mSession = null;
    private Thread mClientThread = null;
	
	public static final HashMap<String, Class<? extends RpcComponentInterface>> sComponentClassMap = new HashMap<String, Class<? extends RpcComponentInterface>>() {
		{
		put(Client.NAME, Client.class);
	
		}
	};
	
	private boolean mIsLogin = false;
	
	public RpcClientManager() {
	}
	
	public synchronized boolean isLogin() {
       return mIsLogin;
    }

	
	/**
	 * @param username
	 * @param password  密码明文
	 * @param ip
	 * @param port
	 * @return
	 */
	public synchronized boolean login(String username,
            String password, String ip, int port) {
		if (mIsLogin) {
			RpcLog.i(TAG, "Rpc login already");
			return true; 
		}

		IRpcChannel channel = RpcConnector.aquireChannel(ip, port);
		if (channel == null) {
			RpcLog.e(TAG, "create rpc channel failed");
			return false;
		}

		startupRpcClient(channel);

		mIsLogin = true;
        if (mIsLogin) {
        	RpcLog.d(TAG, "login success with session=" + mSession.getSessionId());
        } else {
        	mSession.onDestroy();
        }
        return mIsLogin;
	}

	private void startupRpcClient(IRpcChannel channel) {
		mSession = new RpcClientSession() {
			@Override
			public synchronized void onDestroy() {
				// 销毁单件的组件实例
				RpcComponentFactory.destroyInstance(mSession); // 必须在session销毁之前，不然session就无效了
				mIsLogin = false;
				
				super.onDestroy();
				onClientSessionDestroyed();
			}
		};
		mSession.start(channel);
		
		mClientThread = new Thread("ClientSessionRunningThread") {
			@Override
	    	public void run() {
				mSession.run();
				RpcLog.d(TAG, "ClientSessionRunningThread is run out");
			}
		};
		mClientThread.setDaemon(true);
		mClientThread.start();
	}
	
	public synchronized boolean logout() {
		if (!mIsLogin) { return true; }
        mSession.stop();
        //等待线程执行完毕
        try {
        	RpcLog.d(TAG, "waiting client thread run out");
			mClientThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        mIsLogin = false;
        return true;
	}
	
	/**
	 * 用户可以重载这个接口，在session结束后，做一些自己的清理工作
	 * 但请不要再这个方法内直接调用本对象的其他方法，可能会导致嵌套调用和死机。
	 */
    protected void onClientSessionDestroyed() {
    	RpcLog.d(TAG, "client session has been destroyed");
	}
    
	/**
	 * 不要使用，保持对session的封装
	 */
	@Deprecated
	public RpcClientSession getSession() {
		return mSession;
	}
	
	
    public RpcComponent getComponent(String name) {
        return getComponent(name, null);
    }
    
    public RpcComponent getComponent(String name, JSONObject params) {
    	Class<RpcComponentInterface> cls = (Class<RpcComponentInterface>) RpcClientManager.sComponentClassMap.get(name);
        if (cls == null) {
        	RpcLog.e(TAG, "component of " + name + " is not found");
        	return null;
        }
        
    	//RpcLog.i(TAG, "get instance of component " + name);
    	return (RpcComponent)RpcComponentFactory.instance(cls, mSession, params);
    }
    
    
    public static void main(String[] args) {
    	RpcClientManager climgr = new RpcClientManager();
    	climgr.login("crylearner", "123456", "127.0.0.1", 12345);
    	Client cli = (Client)climgr.getComponent(Client.NAME, null);
    	cli.login("crylearner", "123456");
    	System.out.println("success");
    	cli.logout();
    }
}
