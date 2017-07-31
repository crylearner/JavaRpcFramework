package rpc.framework.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.json.JSONArray;

import rpc.json.RpcRequest;
import rpc.json.RpcResponse;
import rpc.util.RpcLog;

public final class RpcServiceAdapter implements RpcServiceInterface {
	private final static String TAG = "RpcServiceAdapter";
	private Method mMethod;
	private Object mObject;
	private String mServiceName;

	public static  RpcServiceAdapter adapt(Object object, String method, Class<?>[] argsType){
		Method m = null;
		try {
			m = object.getClass().getMethod(method, argsType);
		} catch (NoSuchMethodException | SecurityException e) {
			RpcLog.e(TAG, "object " + object + " has no such method: " + method);
			e.printStackTrace();
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
		RpcServiceAdapter[] services = new RpcServiceAdapter[methods.length];
		
		for (Method m: object.getClass().getMethods()) {
			for (String name: methods) {
				if (name == m.getName()) {
					adapt(object, name, m.getParameterTypes());
					break;
				}
			}
		}
		return services;
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
		} else {
			RpcLog.e(TAG, "Request params only support json array now");
			return null;
		}
		
		try {
			Object result = mMethod.invoke(mObject, args);
			return new RpcResponse(request.getId(), result, true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
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
		System.out.println(service.list());
	}

	
	
}
