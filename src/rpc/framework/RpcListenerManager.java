package rpc.framework;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import rpc.util.RpcLog;


/**
 * 专门用于负责事件通知的回调管理
 * @author 23683
 *
 */
public class RpcListenerManager {
	private static final String TAG = "RpcListenerManager";
	private ConcurrentHashMap<Integer, INotificationListener> mListenerMap = new ConcurrentHashMap<Integer, INotificationListener>();
	
	public int register(INotificationListener listener) {
		int callid = calcuCallid(listener);
		if (mListenerMap.containsKey(callid)) {
			RpcLog.w(TAG, "listener is already registered: " + listener);
		} else {
			mListenerMap.put(callid, listener);
		}
		return callid;
	}
	
	public int unregister(INotificationListener listener) {
		int callid = calcuCallid(listener);
		if (!mListenerMap.containsKey(callid)) {
			RpcLog.w(TAG, "listener is not yet registered: " + listener);
		} else {
			mListenerMap.remove(callid);
		}
		return callid;
	}
	
	
	public INotificationListener getListener(int callid) {
		return mListenerMap.get(callid);
	}
	
	public void clear() {
		mListenerMap.clear();
	}
	
	public static int calcuCallid(INotificationListener listener) {
		return listener.hashCode();
	}
	
	public String listListener() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<Integer, INotificationListener> entry : mListenerMap.entrySet()) {
			buffer.append(entry.getValue() + " : " + entry.getKey() + "\n");
		}
		return buffer.toString();
	}
	
}
