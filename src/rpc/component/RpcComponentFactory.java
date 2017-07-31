package rpc.component;

import java.util.HashMap;

import org.json.JSONObject;

import rpc.framework.client.IRpcClientSession;
import rpc.util.RpcLog;

public class RpcComponentFactory {
	private static final String TAG = "RpcComponentFactory";
	
	private static  HashMap<IRpcClientSession, HashMap<Class<?>, RpcComponentInterface>> mComponentCollector = new HashMap<IRpcClientSession, HashMap<Class<?>, RpcComponentInterface>>(); 
	
	
	/**
	 * 创建单例的组件。释放时，必须主动调用destroy释放。
	 * @param cls
	 * @param client
	 * @param params
	 * @return
	 */
	public static RpcComponentInterface instance(Class<? extends RpcComponentInterface> cls,
			IRpcClientSession client,
			JSONObject params) {
		synchronized (mComponentCollector) {
			HashMap<Class<?>, RpcComponentInterface> map = mComponentCollector.get(client);
			if (map == null) {
				map = new HashMap<Class<?>, RpcComponentInterface>();
				mComponentCollector.put(client, map);
			}
			RpcComponentInterface cmp = map.get(cls);
			if (cmp == null) {
				cmp = create(cls, client, params);
				if (cmp != null) { 
					map.put(cls, cmp); 
				}
			}
			return cmp;
		}
	}
	
	public static void destroyInstance(IRpcClientSession client) {
		HashMap<Class<?>, RpcComponentInterface> map = null;
		synchronized (mComponentCollector) {
			map = mComponentCollector.remove(client);
		}
		if (map!=null) {
			for (RpcComponentInterface component :map.values()) {
				RpcLog.d(TAG, "destroy component" + component);
				component.destroy(null);
			}
			map.clear();
		}
	}
	
	
	/**
	 * 创建多实例的组件。无需主动销毁
	 * @param cls
	 * @param client
	 * @param params
	 * @return
	 */
	public static RpcComponentInterface create(Class<? extends RpcComponentInterface> cls,
			IRpcClientSession client,
			JSONObject params) {
		RpcComponent component = null;
		try {
			component = (RpcComponent)cls.newInstance();
			component.setRpcClient(client);
			if (!component.instance(params)) {
				RpcLog.e(TAG, "instance component" + cls + " failed");
				return null;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		RpcLog.i(TAG, "component of " + cls + " is created");
		return component;
	}
	

	
	
}
