package rpc.framework.connection;

import java.io.IOException;

public interface IRpcChannel {
	/**
	 * 接收数据，返回字节串
	 * 抛出异常，即表示Channel不可用
	 * @return
	 * @throws IOException
	 */
	public byte[] recv() throws IOException;
	
	/**
	 * 抛出异常，即表示Channel不可用
	 * @param data
	 * @throws IOException
	 */
	public void send(byte[] data) throws IOException;
	
	
	/**
	 * 关闭通道
	 * 重复调用是安全的。
	 * 当close之后，再调用recv和send时，会抛出IOException异常
	 */
	public void close();
}
