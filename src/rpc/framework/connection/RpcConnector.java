package rpc.framework.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import rpc.util.RpcLog;


public class RpcConnector {
	public static final int TCP_TIMEOUT = 2000;
	private static final String TAG = "RpcConnector";
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @return if fail return null
	 */
	public static IRpcChannel aquireChannel(String ip, int port) {
		Socket mSocket = new Socket();
		InetSocketAddress addr = new InetSocketAddress(ip, port);
		try {
			RpcLog.i(TAG, "startConnect():" + addr.toString());
			mSocket.setReuseAddress(true);
			mSocket.connect(addr, TCP_TIMEOUT);
			if (mSocket.isConnected()) {
				RpcLog.d(TAG, "startConnect() sucessfully!:" + addr.toString());
			} else {
				RpcLog.d(TAG, "startConnect() failed!:" + addr.toString());
				mSocket.close();
				return null;
			}
			RpcChannel channel = new RpcChannel(mSocket);
			return channel;
			
		} catch (IOException e) {
			RpcLog.e(TAG, "aquireChannel failed");
		} 
		return null;
	}
	
	public static void closeChannel(IRpcChannel channel) {
		RpcLog.d(TAG, "close rpc channel");
		channel.close();
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @return if fail return null
	 */
	public static IRpcChannel acceptChannel(String ip, int port) {
		ServerSocket mSocket = null;
		try {
			RpcLog.i(TAG, "start to listen");
			mSocket = new ServerSocket(port);
		} catch (IOException e) {
			RpcLog.e(TAG, "listen failed");
			return null;
		}
		
		Socket client = null;
		try {
			client = mSocket.accept();
			RpcLog.d(TAG, "accept a client:" + client);
		} catch (IOException e) {
			RpcLog.e(TAG, "accept a client failed");
			return null;
		} finally {
			try {
				mSocket.close();
			} catch (IOException e1) {
				RpcLog.e(TAG, "stop server socket failed");
			}
		}
		
		RpcChannel channel = null;
		try {
			channel = new RpcChannel(client);
			RpcLog.d(TAG, "acceptChannel success");
		} catch (IOException e) {
			RpcLog.e(TAG, "acceptChannel failed");
		} 
		return channel;
	}
	
}
