package rpc.framework.client;

import rpc.component.RpcComponent;
import rpc.framework.INotificationListener;
import rpc.framework.IResultListener;
import rpc.framework.connection.IRpcChannel;
import rpc.json.RpcRequest;
import rpc.json.RpcResponse;


public interface IRpcClientSession {

	/**
	 * 同步阻塞调用。如果调用失败，则约定RpcResponse为null
	 *  
	 * 1.底下socket出错， 对应的处理是销毁session
	 * 2.消息处理过程中出错，如果格式不对等。 对应的处理是本次调用失败，返回null。session依旧有效
	 * 3.执行过程中被中断，如线程销毁或者超时。 对应的处理是销毁session
	 * 其中第二点如何判断, 就根据RpcResponse==null来判断
	 * @param request
	 * @param timeout  超时时间
	 * @return
	 * @throws InterruptedException 
	 * @note 可能的异常
	 */
	public abstract RpcResponse request(RpcRequest request,
			RpcComponent component, int timeout) throws InterruptedException;

	/**
	 * 异步非阻塞调用。如果调用成功，则会自动回调listener
	 * @param request
	 * @param listener
	 * @throws InterruptedException 
	 */
	public abstract boolean asyncrequest(RpcRequest request,
			RpcComponent component, IResultListener listener)
			throws InterruptedException;

	
	/** 
	 * 注册来自Rpc服务端的主动推送事件的监听
	 * @param listener
	 * @return
	 */
	public abstract int regEventListener(INotificationListener listener);

	
	/**
	 * 取消监听
	 * @param listener
	 * @return
	 */
	public abstract int unregEventListener(INotificationListener listener);
	
	
	/**
	 * 获取listener对应的整形CID
	 */
	public abstract int getCallid(INotificationListener listener);
	
	/**
	 * 获取session id
	 */
	public abstract int getSessionId();
	
	/** 
	 * 设置session id, 只有Client组件可以调用，其他组件禁止调用，否则会出现意外结果。
	 * @param sessionId。 Client组件login成功后，服务端会返回一个session id。
	 */
	public abstract void setSessionId(int sessionId);

	/**
	 * 获取请求id
	 * @return
	 */
	public abstract int getRequestId();

	
	/**
	 * 销毁session,具体用户可以重载onDestroy这个接口实现一些监听工作。
	 */
	public abstract void onDestroy();
	
	public abstract void start(IRpcChannel channel);
	public abstract void run();
	public abstract void stop();
}