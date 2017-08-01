package rpc.framework.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

/**adapter none RpcServiceInterface to RpcServiceInterface like
 * @author sunshyran
 *
 */
public final class RpcServiceAdapter implements RpcServiceInterface {
	private final static String TAG = "RpcServiceAdapter";
	private Method mMethod;
	private Object mObject;
	private String mServiceName;

	/**adapt target method as RpcServiceInterface like
	 * @param object
	 * @param method
	 * @param argsType
	 * @return
	 */
	public static  RpcServiceAdapter adapt(Object object, String method, Class<?>[] argsType){
		Method m = null;
		try {
			m = object.getClass().getMethod(method, argsType);
		} catch (NoSuchMethodException | SecurityException e) {
			RpcLog.e(TAG, "object " + object + " has no such method: " + method);
			RpcLog.e(TAG, e);
			return null;
		}
		
		String className = getClassLastName(object);
		RpcServiceAdapter service = new RpcServiceAdapter();
		service.mMethod = m;
		service.mObject = object;
		service.mServiceName = className + '.' + m.getName();
		return service;
	}
	
	
	public static RpcServiceAdapter[] adapt(Object object, String[] methods) {
		ArrayList<RpcServiceAdapter> services = new ArrayList<RpcServiceAdapter>();
		
		Method[] methodsArray = object.getClass().getMethods();
		for (String name: methods) {
			boolean ok = false;
			for (Method m: methodsArray) {
				if (name.equals(m.getName())) {
					services.add(adapt(object, name, m.getParameterTypes()));
					ok = true;
					break;
				}
			}
			if (!ok) {	RpcLog.e(TAG, "object " + object + " has no such method: " + name);	}
		}
		
		return services.toArray(new RpcServiceAdapter[]{});
	}
	
	public static RpcServiceAdapter[] adapt(Object object) {
		ArrayList<Method> methodsArray = new ArrayList<Method>();
		for (Method m : object.getClass().getMethods()) {
			if (m.getAnnotation(Rpc.class) != null) {
				methodsArray.add(m);
			}
		}
		
		ArrayList<RpcServiceAdapter> services = new ArrayList<RpcServiceAdapter>();
		for (Method m : methodsArray) {
			services.add(adapt(object, m.getName(), m.getParameterTypes()));
		}
		return services.toArray(new RpcServiceAdapter[]{});
	}
	

	private static String getClassLastName(Object object) {
		String className = object.getClass().getName();
		if (className.lastIndexOf('.') != -1) {
			className = className.substring(className.lastIndexOf('.')+1);
		}
		return className;
	}
	
	@Override
	public String[] list() {
		return new String [] {mServiceName};
	}

	@Override
	public RpcResponse execute(RpcRequest request) {
		Object[] args = new Object[mMethod.getParameterCount()];
		Object params = request.getParams();
		if (params instanceof JSONArray) {
			for (int i = 0; i < args.length; ++i) {
				args[i] = ((JSONArray)params).get(i);
			}
		} else if (params instanceof JSONObject) {
			Rpc rpc = mMethod.getAnnotation(Rpc.class);
			if (rpc == null) {
				RpcLog.e(TAG, "Rpc annotation is not found for method: " + mMethod.getName());
				return new RpcResponse(request.getId(), "IllegalArgumentException", false);
			}
			String[] paramsName = rpc.params();
			if (paramsName.length != args.length) {
				RpcLog.e(TAG, "Rpc annotation has invaid params for method: " + mMethod.getName());
				return new RpcResponse(request.getId(), "IllegalArgumentException", false);
			}
			for (int i=0; i<args.length; ++i) {
				args[i] = ((JSONObject) params).get(paramsName[i]);
			}
		} else {
			RpcLog.e(TAG, "Request params only support json array or json object");
			return new RpcResponse(request.getId(), "IllegalArgumentException", false);
		}
		
		Object result = null;
		try {
			result = mMethod.invoke(mObject, args);
		} catch (IllegalArgumentException e) {
			RpcLog.e(TAG, e);
		} catch (IllegalAccessException e) {
			RpcLog.e(TAG, e);
		} catch (InvocationTargetException e) {
			RpcLog.e(TAG, e);
		} catch (SecurityException e) {
			RpcLog.e(TAG, e);
		} catch(Exception e) {
			RpcLog.e(TAG, e);
		}
		
		if (result != null) {
			return new RpcResponse(request.getId(), result, true);
		} else {
			return new RpcResponse(request.getId(), "failed request", false);
		}
	}

	
	public static void main(String[] args) {
		class X {
			public int addone(int i, int j) { return i+j; }
		}
		X x = new X();
		Class<?>[] types = new Class<?>[] {int.class, int.class};
		RpcServiceAdapter service = RpcServiceAdapter.adapt(x, "addone", types);
		RpcRequest request = new RpcRequest(1, "X.addone", new JSONArray("[1, 2]"));
		System.out.println(service.execute(request));
		System.out.println(Arrays.toString(service.list()));
	}

	
	
}
