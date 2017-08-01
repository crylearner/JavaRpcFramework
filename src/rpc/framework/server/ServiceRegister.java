package rpc.framework.server;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 给定一个method，找到对应的对象，并执行该方法
 * 原理：建立method与object的映射关系
 * @author 23683
 *
 */
/**
 * @author 23683
 *
 */
/**
 * @author 23683
 *
 */
public class ServiceRegister {
	private static final String TAG="ServiceRegister";
	
	private ConcurrentHashMap<String/*service name*/, RpcServiceInterface> mServiceMap = new ConcurrentHashMap<String, RpcServiceInterface>();
	
	
	/**
	 * 获取与服务方法method对应的服务
	 * @param name
	 * @return 服务object
	 */
	public RpcServiceInterface getService(String name) {
		return mServiceMap.get(name);
	}
	
	/**
	 * 以字符串形式，列出所有已经注册的服务
	 * @return
	 */
	public String listServices() {
		StringBuffer buffer = new StringBuffer();
		for (Entry<String, RpcServiceInterface> entry : mServiceMap.entrySet()) {
			buffer.append(entry.getValue() + " : " + entry.getKey() + "\n");
		}
		return buffer.toString();
	}
	
	/**register rpc method of servcieName
	 * @param serviceName e.g. xxxx.add
	 * @param service
	 */
	public void addService(String serviceName, RpcServiceInterface service) {
		mServiceMap.put(serviceName, service);
	}

	/**register all rpc method in the methods 
	 * @param service
	 * @param methods e.g.  {"add", "minus", "divide"}
	 */
	public void addService(RpcServiceInterface service, String[] methods) {
		String className = getClassLastName(service);
		for (String m : methods) {
			addService(className + "." + m, service);
		}
	}
	
	
	/**register all rpc method in service.lis()
	 * @param service 
	 */
	public void addService(RpcServiceInterface service) {
		for (String name:service.list()) {
			addService(name, service);
		}
	}
	
	/**删除与服务方法method对应的rpc服务
	 * @param method 服务方法名 e.g.   MyService.myMethod
	 */
	public void removeService(String method) {
		mServiceMap.remove(method);
	}
	
	/**
	 * 删除所有服务
	 */
	public void removeAllService() {
		mServiceMap.clear();
	}
	

	String getClassLastName(Object object) {
		String className = object.getClass().getName();
		if (className.lastIndexOf('.') != -1) {
			className = className.substring(className.lastIndexOf('.')+1);
		}
		return className;
	}

}
