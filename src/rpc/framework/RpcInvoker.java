package rpc.framework;

import rpc.component.RpcComponent;
import rpc.framework.message.RpcMessage;

public class RpcInvoker {

	private int Id = 0;
	private RpcMessage request = null;
	private RpcMessage response = null;
	private IResultListener resultlistener = null;
	private Object mResultLocker = new Object();
	
	public RpcInvoker(RpcMessage request, RpcComponent component, IResultListener listener) {
		Id = request!=null ? request.getId() : -1;
		this.request = request;
		resultlistener = listener;
	}
	
	/**
	 * 
	 * @param timeout
	 * @return false 说明是超时了
	 * @throws InterruptedException
	 */
	public boolean waitingResponse(int timeout) throws InterruptedException {
		synchronized (mResultLocker) {
			long start = System.currentTimeMillis ();
			while (response == null) {
				mResultLocker.wait(timeout);
				if (System.currentTimeMillis() - start >= timeout) { 
					// 如果超时，抛出TimeoutException异常
					// FIXME: 要不要禁掉listener
					resultlistener = null; // 防止后面又收到回调，引起混乱
				    return false;   
				}
			}
		}
		return true;
	}
	
	public void notifyResponse(RpcMessage response) {
		synchronized (mResultLocker) {
			mResultLocker.notify();
			this.response = response;
		}
		if (resultlistener!=null) {
			// FIXME::  为了兼容过去的框架，这里统一不处理，由客户端的setResponse统一处理。
			//resultlistener.onResult(response); 
		}
	}
	
	
	public RpcMessage getRequest() {
		return request;
	}
	
	public IResultListener getListener() {
		return resultlistener;
	}
	public boolean needWaitingResponse() {
		//return resultlistener != null;
		// FIXME:暂时总是异步，这是为了兼容过去的代码框架，每次收到服务器的回复后，都必须调用测一次setResponse。
		return true;  
	}
	
	public RpcMessage getResponse() {
		synchronized (mResultLocker) {
			return response;
		}
	}

	public Integer getId() {
		return Id;
	}
	
}
