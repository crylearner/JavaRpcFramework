package rpc.framework.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.NotificationListener;

import org.json.JSONArray;
import org.json.JSONObject;

import rpc.exception.RpcException;
import rpc.exception.RpcInternalError;
import rpc.exception.RpcInvalidParameters;
import rpc.exception.RpcMethodNotFound;
import rpc.exception.RpcPermissionDenied;
import rpc.framework.server.annotation.RpcAttachEvent;
import rpc.framework.server.annotation.RpcMethod;
import rpc.framework.server.annotation.RpcService;
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
	private ConcurrentHashMap<String/*service name*/, Method> mEventsMap = new ConcurrentHashMap<String, Method>();
	private Object mObject; // all method share the same object
	private NotificationListener mListener = null;
	

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
			RpcMethod an = m.getAnnotation(RpcMethod.class);
			if (an != null) {
				if (an.params().length > 0 && an.params().length != m.getParameterTypes().length) {
					RpcLog.e(TAG, "method has an inconsistent Rpc annotation: " + m.getName());
					continue;
				}
				if (!an.subject()) {
					methodsArray.add(m);
				} else {
					RpcSubscriber.BaseRpcSubscriber.registerSubject(getMainServiceName(object) + '.' + m.getName(), object);
				}
			} else if (m.getAnnotation(RpcAttachEvent.class) != null) {
				m.getAnnotation(RpcAttachEvent.class);
				//TODO
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
	
	private static String getMainServiceName(Object object) {
		RpcService an = object.getClass().getAnnotation(RpcService.class);
		if (an != null && !an.name().isEmpty()) {
			return an.name();
		} else {
			// FIXME:: internal class name is properly?
			return getClassLastName(object);
		}
	}
	
	private void addService(Object serviceObject, Method method) {
		mObject = serviceObject;
		mMethodsMap.put(getMainServiceName(serviceObject) + '.' + method.getName(), method);
	}
	
	
	@Override
	public String[] list() {
		Set<String> set = mMethodsMap.keySet();
		return set.toArray(new String[]{});
	}
	
	@Override
	public RpcResponse execute(RpcRequest request) throws RpcException {
		String methodname = request.getMethod();
		Method m = mMethodsMap.get(methodname);
		if (m == null) {
			throw new RpcMethodNotFound(methodname);
		}
		
		Object[] args = null;
		
		Object params = request.getParams();
		if (params == null || params == JSONObject.NULL) {
			args = null;
			
		} else if (params instanceof JSONArray) {
			args = fillArgs(m, (JSONArray)params);
					
		} else if (params instanceof JSONObject) {
			args = fillArgs(m, (JSONObject)params);
		} else {
			throw new RpcInvalidParameters(params.toString());
		}
		
		return invoke(request, m, args);
	}


	private Object[] fillArgs(Method mMethod, JSONObject params) throws RpcException {
		Object[] args = new Object[mMethod.getParameterTypes().length]; // getParametersCount in java1.6 is not available

		if (params.length() != args.length) {
			throw new RpcInvalidParameters("parameters's count is inconsistent");
		}
		
		// params是按照函数参数的 key-value键值对，由于java1.8以下，不能直接读取形参名，所以只能使用annotation
		RpcMethod rpc = mMethod.getAnnotation(RpcMethod.class);
		if (rpc == null) {
			RpcLog.e(TAG, "Rpc annotation is not found for method: " + mMethod.getName());
			throw new RpcInternalError("method " + mMethod.getName() + " annotate error");
		}
		
		String[] paramsName = rpc.params();
		// FIXME:: in fact, we no need to check rpc.params here, we have done it in adapt()
		if (paramsName.length != args.length) {
			RpcLog.e(TAG, "Rpc annotation has invaid params for method: " + mMethod.getName());
			throw new RpcInternalError("method " + mMethod.getName() + " annotate error");
		}
		
		for (int i=0; i<args.length; ++i) {
			args[i] = params.get(paramsName[i]);
			if (args[i] == JSONObject.NULL) {
				args[i] = null; // 统一为null，方便处理。 因为JSONObject.NULL并非是JSONObject对象
			}
		}
		
		return args;
	}

	private Object[] fillArgs(Method mMethod, JSONArray params) throws RpcException {
		Object[] args = new Object[mMethod.getParameterTypes().length]; // getParametersCount in java1.6 is not available
		if (args.length != params.length()) {
			throw new RpcInvalidParameters("parameters's count is inconsistent");
		}
		// params是按照函数参数的定义顺序，依次排列的数组
		for (int i = 0; i < args.length; ++i) {
			args[i] = params.get(i);
			if (args[i] == JSONObject.NULL) {
				args[i] = null; // 统一为null，方便处理。 因为JSONObject.NULL并非是JSONObject对象
			}
		}
		return args;
	}

	private RpcResponse invoke(RpcRequest request, Method mMethod, Object[] args) throws RpcException {
		Object result = null;
		try {
			result = mMethod.invoke(mObject, args);
		} catch (IllegalArgumentException e) {
			RpcLog.e(TAG, e);
			throw new RpcInvalidParameters(request.getParams().toString());
		} catch (IllegalAccessException e) {
			RpcLog.e(TAG, e);
			throw new RpcPermissionDenied(e.getMessage());
		} catch (InvocationTargetException e) {
			RpcLog.e(TAG, e);
			throw new RpcInternalError(e.getMessage());
		} catch (SecurityException e) {
			RpcLog.e(TAG, e);
			throw new RpcPermissionDenied(e.getMessage());
		} catch(Exception e) {
			RpcLog.e(TAG, e);
			throw new RpcInternalError(e.getMessage());
		}
		
		return new RpcResponse(request.getId(), result, true);
	}

	
	public static void main(String[] args) {
		class X {
			public int addone(int i, int j) { return i+j; }
		}
		X x = new X();
		Class<?>[] types = new Class<?>[] {int.class, int.class};
		RpcServiceAdapter service = RpcServiceAdapter.adapt(x, "addone", types);
		RpcRequest request = new RpcRequest(1, "RpcServiceAdapter$1X.addone", new JSONArray("[1, 2]"));
		try {
			System.out.println(service.execute(request));
		} catch (RpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Arrays.toString(service.list()));
	}
	
}
