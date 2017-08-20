package rpc.framework.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import rpc.framework.server.annotation.Rpc;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

/**adapter none RpcServiceInterface to RpcServiceInterface like
 * @author sunshyran
 *
 */
public final class RpcServiceAdapter implements RpcServiceInterface {
	private final static String TAG = "RpcServiceAdapter";
	private ConcurrentHashMap<String/*service name*/, Method> mMethodsMap = new ConcurrentHashMap<String, Method>();
	private Object mObject; // all method share the same object

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
		
		RpcServiceAdapter service = new RpcServiceAdapter();
		service.addService(object, m);
		return service;
	}
	
	
	public static RpcServiceAdapter adapt(Object object, String[] methods) {
		RpcServiceAdapter service = null;
		
		Method[] methodsArray = object.getClass().getMethods();
		for (String name: methods) {
			boolean ok = false;
			for (Method m: methodsArray) {
				if (name.equals(m.getName())) {
					if (service == null) {
						service = adapt(object, name, m.getParameterTypes());
					} else {
						service.addService(object, m);
					}
					ok = true;
					break;
				}
			}
			if (!ok) {	RpcLog.e(TAG, "object " + object + " has no such method: " + name);	}
		}
		
		return service;
	}
	
	public static RpcServiceAdapter adapt(Object object) {
		ArrayList<Method> methodsArray = new ArrayList<Method>();
		for (Method m : object.getClass().getMethods()) {
			if (m.getAnnotation(Rpc.class) != null) {
				methodsArray.add(m);
			}
		}
		
		RpcServiceAdapter service = null;
		for (Method m : methodsArray) {
			if (service == null) {
				service = adapt(object, m.getName(), m.getParameterTypes());
			} else {
				service.addService(object, m);;
			}
		}
		return service;
	}
	

	private static String getClassLastName(Object object) {
		String className = object.getClass().getName();
		if (className.lastIndexOf('.') != -1) {
			className = className.substring(className.lastIndexOf('.')+1);
		}
		return className;
	}
	
	private void addService(Object serviceObject, Method method) {
		mObject = serviceObject;
		mMethodsMap.put(getClassLastName(serviceObject) + '.' + method.getName(), method);
	}
	
	
	@Override
	public String[] list() {
		Set<String> set = mMethodsMap.keySet();
		return set.toArray(new String[]{});
	}
	
	@Override
	public RpcResponse execute(RpcRequest request) {
		Method m = mMethodsMap.get(request.getMethod());
		if (m == null) {
			return new RpcResponse(request.getId(), "Method not found", false);
		}
		return executeTheMethod(request, m);
	}

	public RpcResponse executeTheMethod(RpcRequest request, Method mMethod) {
		Object[] args = new Object[mMethod.getParameterTypes().length]; // getParametersCount in java1.6 is not available
		Object params = request.getParams();
		if (params instanceof JSONArray) {
			// params是按照函数参数的定义顺序，依次排列的数组
			for (int i = 0; i < args.length; ++i) {
				args[i] = ((JSONArray)params).get(i);
			}
			
		} else if (params instanceof JSONObject) {
			// params是按照函数参数的 key-value键值对，由于java1.8以下，不能直接读取形参名，所以只能使用annotation
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
			
		} else if (params == null || params == JSONObject.NULL) {
			args = null;
			
		} else {
			RpcLog.e(TAG, "Request params only support json array or json object");
			return new RpcResponse(request.getId(), "IllegalArgumentException", false);
		}
		
		return invoke(request, mMethod, args);
	}


	private RpcResponse invoke(RpcRequest request, Method mMethod, Object[] args) {
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
