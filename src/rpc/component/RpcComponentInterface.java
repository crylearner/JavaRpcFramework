package rpc.component;

import org.json.JSONObject;

import rpc.framework.INotificationListener;
import rpc.framework.IResultListener;
import rpc.json.message.RpcResponse;


/**
 * RPC 组件实现规范。  所有的RPC组件必须继承RpcComponentInterface。 必须实现instance和destroy方法。
 * 对非静态方法，必须是instance之后才能调用
 * instance方法必须是阻塞的，也就是其他非静态方法执行时，instance必然已经完成。
 * 遵循与远程组件一样的对象管理。也就是如果被代理的远程组件是单例，那么这里也应该是单例，如果远程组件时多实例，那么这里也是多实例。
 * 完了强制使用以上规范，要求统一使用工厂类去初始化组件。
 * @author sunshyran
 * 
 */
public interface RpcComponentInterface {
//	public RpcComponentInterface createRpcComponent(RpcDVRClient client, Class<RpcComponentInterface> cls);
	
	/**
	 * instance 必须是同步阻塞的实现
	 * @param params
	 * @return
	 */
	public boolean instance(JSONObject params);
	
	
	/**
	 * instance 必须是同步阻塞的实现
	 * @param params
	 * @return
	 */
	public boolean destroy(JSONObject params);
	
	
	/**
	 * call 非静态方法的同步调用。 所谓非静态，就是内部依赖于object，必须instance之后才能调用
	 * 返回值RpcResponse永远非空
	 * @param method rpc方法
	 * @param params rpc参数，特指params
	 * @param timeout 超时时间，单位是ms
	 * @return RpcResponse 永远非空
	 */
	public RpcResponse call(String method, JSONObject params, int timeout);
	
	
	/**
	 * static_call 静态方法的同步调用。可以在instance之前调用。包括instance本身实际也是static_call
	 * 返回值RpcResponse永远非空
	 * @param method rpc方法
	 * @param params rpc参数，特指params
	 * @param timeout 超时时间，单位是ms
	 * @return
	 */
	public RpcResponse static_call(String method, JSONObject params, int timeout);
	
	
	/**
	 * async_call 异步非阻塞调用。目前只支持非静态方法。
	 * @param method rpc方法
	 * @param params rpc参数，特指params
	 * @param listener 异步返回时，需要调用的回调
	 * @return
	 */
	public boolean async_call(String method, JSONObject params, IResultListener listener);
	
	
	/**
	 * 增加回调订阅
	 * @param method rpc方法
	 * @param listener 回调监听
	 * @param params  rpc参数，但params：proc不用填，该方法会自动填充这个proc字段
	 * @param timeout 调用超时
	 * @return 订阅成功，返回true
	 */
	public boolean add_subscription(String method, INotificationListener listener, JSONObject params, int timeout);
	
	
	/**
	 * 取消回调订阅
	 * @param method  rpc方法
	 * @param listener 要取消的回调监听
	 * @param params  rpc参数，但params：proc不用填，该方法会自动填充这个proc字段
	 * @param timeout 调用超时
	 * @return 取消订阅成功，返回true
	 */
	public boolean cancel_subscription(String method, INotificationListener listener, JSONObject params, int timeout);


}
