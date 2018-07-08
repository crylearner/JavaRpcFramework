package rpc.framework.server;

import rpc.exception.RpcException;
import rpc.framework.INotificationListener;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;

public interface RpcServiceInterface {
	public static final String INVALID_PARAM_ERROR = "invalid parameters";
	
	/**列出当前服务的所有可注册的方法
	 * @return 
	 */
	public String[] list();
	
	/**服务调度。根据RPC协议，Request中包含Method字段，指定调用的方法名。
	 * @param request
	 * @param listener always can be null unless attach
	 * @return 请求执行结果
	 */ 
	public RpcResponse execute(RpcRequest request) throws RpcException;  
	
}


