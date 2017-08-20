package rpc.framework.server;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rpc.exception.RpcException;
import rpc.exception.RpcInternalError;
import rpc.exception.RpcInvalidRquest;
import rpc.framework.RpcInvoker;
import rpc.framework.RpcServerInvokerHandler;
import rpc.framework.connection.IRpcChannel;
import rpc.framework.connection.RpcConnector;
import rpc.json.message.RpcErrorResponse;
import rpc.json.message.RpcNotification;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;
import rpc.util.RpcLog;

public class RpcServer {
	private static final String TAG = "RpcServer";
	private boolean mIsRunning = false;
	private RpcServerInvokerHandler mHandler = null;
	ServiceRegister mServiceRegister = new ServiceRegister();
	ExecutorService mThreadPool = Executors.newCachedThreadPool();

	class RequestProcessor implements Callable<RpcResponse> {
		private RpcServiceInterface mService = null;
		private RpcRequest 	mRequest = null;
		
		public RequestProcessor(RpcServiceInterface service,
				RpcRequest request) {
			super();
			this.mService = service;
			this.mRequest = request;
		}

		@Override
		public RpcResponse call() throws Exception {
			if (mService == null) { return null; } // FIXME:: if properly
			RpcLog.d(TAG, "RequestProcessor is callings");
			try {
				if (mRequest.getMethod() == null || mRequest.getMethod().isEmpty()) {
					throw new RpcInvalidRquest("method is empty in request of " + mRequest.toString());
				}
				return mService.execute(mRequest);
			
			} catch (RpcException e) {
				return new RpcErrorResponse(mRequest.getId(), e.toError());
			}
		}
		
	}
	
	/**注册服务
	 * @param service
	 */
	public void registerService(RpcServiceInterface service) { 
		mServiceRegister.addService(service);
		
	}
	
	
	/** 启动服务程序，会一直阻塞
	 * @param ip
	 * @param port
	 */
	public void serve(String ip, int port) {
		RpcLog.i(TAG, mServiceRegister.listServices());
		while(true) {
			IRpcChannel channel = RpcConnector.acceptChannel(ip, port);
			if (channel == null) {
				RpcLog.e(TAG, "aquire rpc channel failed");
				continue;
			}
			mIsRunning = true;
			mHandler = new RpcServerInvokerHandler();
			mHandler.bindChannel(channel);
			run();
		}
	}
	
	public void onNotify(RpcNotification message) {
		while (mIsRunning) {
			if (message == null) { continue; }
			try {
				mHandler.invoke(new RpcInvoker(message, null, null));
			} catch (InterruptedException e) {
				RpcLog.e(RpcServer.TAG, "server is stopped because of exception:" + e);
			}
		}
	}
	
	
	public void run() {
		mHandler.startup();
		while (mIsRunning) {
			try {
				RpcInvoker invoker = mHandler.retrieve();
				RpcRequest request = (RpcRequest)invoker.getRequest();
				RpcServiceInterface service = (RpcServiceInterface)mServiceRegister.getService(request.getMethod());
				RpcLog.d(TAG, "ask request: " + request.toString());
				RpcResponse response = null;
				if (service == null) {
					RpcLog.e(TAG, "NO service can handle this method:" + request.getMethod());
					response = null;
				} else {
					try {
						response = mThreadPool.submit(new RequestProcessor(service, request)).get();
					} catch (ExecutionException | InterruptedException | CancellationException e) {
						RpcLog.e(TAG, e);
						response = new RpcResponse(request.getId(), (new RpcInternalError(e.toString())).toError(), false);
					}
				}
				mHandler.invoke(new RpcInvoker(response, null, null));
			} catch (InterruptedException e) {
				RpcLog.e(RpcServer.TAG, "server is stopped because of exception:" + e);
				RpcServer.this.stop();
			}
		}
		onDestroy();
	}
	
	public void stop() {
		mIsRunning = false;
	}
	
	public void onDestroy() {
		mHandler.shutdown();
	}
}
