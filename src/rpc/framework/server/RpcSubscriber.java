package rpc.framework.server;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import rpc.exception.RpcException;
import rpc.exception.RpcMethodNotFound;
import rpc.framework.INotificationListener;
import rpc.json.message.RpcNotification;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.IDGenerator;
import rpc.util.RpcLog;
import server.json.Hello;
import server.json.HelloRpcWrapper;
import server.json.HelloService;

/**
 * @author sunshyran
 *
 */
/**
 * @author sunshyran
 *
 */
public class RpcSubscriber implements RpcServiceInterface, Cloneable {
	private static final String TAG = "RpcSubscriber";
	private INotificationListener mListener;
	private IDGenerator mSIDGen = new IDGenerator();
	private ConcurrentHashMap<String/*service name*/, Object> mServicesMap = new ConcurrentHashMap<String, Object>();
	private ConcurrentHashMap<Integer/*SID*/, Object/*listener*/> mListenerMap = new ConcurrentHashMap<Integer, Object>();
	
	
	public static RpcSubscriber BaseRpcSubscriber = new RpcSubscriber();
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		RpcSubscriber obj = (RpcSubscriber) super.clone();
		// enough, every thing except mListener canbe share. 而mListener后面总是会被重新设置
		return obj; 
	}
	
	
	@Override
	public String[] list() {
		Set<String> set = mServicesMap.keySet();
		return set.toArray(new String[]{});
	}

	@Override
	public RpcResponse execute(RpcRequest request) throws RpcException {
		String method = request.getMethod();
		if ("HelloService.listenGrowUp".equals(method)) {
			return listenGrowUp(request);
		} else if ("HelloService.unlistenGrowUp".equals(method)) { 
			return unlistenGrowUp(request);
	    } else if ("Hello.observeAge".equals(method)) {
	    	return observeAge(request);
	    } else {
			RpcLog.e(TAG, "unsupport method: " + method);
			return null;
		}
	}
	
	/**注册订阅接口
	 * @param subject  服务类对象
	 * @param sericename     服务名，e.g. helloservice.observeAge
	 */
	public void registerSubject(String servicename, Object subject) {
		RpcLog.d(TAG, "register " + servicename);
		mServicesMap.put(servicename, subject);
	}
	
	public String listServices() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<String, Object> entry : mServicesMap.entrySet()) {
			buffer.append(entry.getValue() + " : " + entry.getKey() + "\n");
		}
		return buffer.toString();
	}
	
	void onConnect(INotificationListener listener) {
		mListener = listener;
	}
	
	void onDisconnect() {
		mListener = null;
	}
	
	private void safeNotify(RpcNotification noti) {
		if (mListener != null) {
			mListener.onNotify(noti);
		}
	}
	
	RpcResponse listenGrowUp(RpcRequest request) throws RpcException {
		String methodname = request.getMethod();

		HelloService service = (HelloService) mServicesMap.get(methodname);
		if (service == null) {
			throw new RpcMethodNotFound(methodname);
		}
		
		int sid = mSIDGen.getRequestId();
		Hello.AgeListener listener = new Hello.AgeListener() {
			public void onChanged(int age) {
				RpcNotification noti = new RpcNotification();
				JSONObject agejson = new JSONObject();
				agejson.put("age", age);
				agejson.put("SID", sid);
				noti.setMethod("agechanged");
				noti.setParams(agejson);
				safeNotify(noti);
			}
		};
		JSONObject args = (JSONObject)request.getParams();
		String type = args.getString("Type");
		service.listenGrowUp(type, listener);
		
		mListenerMap.put(sid, listener);
		
		JSONObject result = new JSONObject();
		result.put("SID", sid);
		return new RpcResponse(request.getId(), result, true); 
	}

	
	RpcResponse unlistenGrowUp(RpcRequest request) throws RpcException {
		String methodname = request.getMethod();
		HelloService service = (HelloService) mServicesMap.get(methodname);
		if (service == null) {
			throw new RpcMethodNotFound(methodname);
		}
		
		int sid = ((JSONObject) request.getParams()).getInt("SID");
		Object listener = mListenerMap.get(sid);
		JSONObject args = (JSONObject)request.getParams();
		String type = args.getString("Type");
		service.unlistenGrowUp(type, (Hello.AgeListener)listener);
		return new RpcResponse(request.getId(), null, true); 
	}
	
	RpcResponse observeAge(RpcRequest request) throws RpcException {
		String methodname = request.getMethod();

		HelloRpcWrapper service = (HelloRpcWrapper) mServicesMap.get(methodname);
		if (service == null) {
			throw new RpcMethodNotFound(methodname);
		}
		
		int sid = mSIDGen.getRequestId();
		Hello.AgeListener listener = new Hello.AgeListener() {
			public void onChanged(int age) {
				RpcNotification noti = new RpcNotification();
				JSONObject agejson = new JSONObject();
				agejson.put("age", age);
				agejson.put("SID", sid);
				noti.setMethod("agechanged");
				noti.setParams(agejson);
				safeNotify(noti);
			}
		};
		JSONObject args = (JSONObject)request.getParams();
		String type = args.getString("Type");
		service.observeAge(type, listener);
		
		mListenerMap.put(sid, listener);
		
		JSONObject result = new JSONObject();
		result.put("SID", sid);
		return new RpcResponse(request.getId(), result, true); 
	}
}
