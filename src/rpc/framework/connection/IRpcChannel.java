package rpc.framework.connection;

import java.io.IOException;

import rpc.framework.RpcInvoker;
import rpc.framework.message.RpcMessage;
import rpc.json.message.RpcPackage;
import rpc.util.RpcLog;

public interface IRpcChannel {
	/**
	 * 接收数据，返回invoker
	 * 抛出异常，即表示Channel不可用
	 * @return
	 * @throws IOException
	 */
	public RpcInvoker recv() throws IOException;
	/**
	 * 抛出异常，即表示Channel不可用
	 * @param invoker
	 * @throws IOException
	 */
	public void send(RpcInvoker invoker) throws IOException;
	
	/**
	 * 关闭通道
	 * 重复调用是安全的。
	 * 当close之后，再调用recv和send时，会抛出IOException异常
	 */
	public void close();
}
