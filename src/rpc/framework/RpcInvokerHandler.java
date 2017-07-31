package rpc.framework;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import rpc.framework.connection.IRpcChannel;
import rpc.framework.message.RpcMessage;
import rpc.json.message.RpcPackage;
import rpc.json.message.RpcPackageAssembly;
import rpc.json.message.RpcRequest;
import rpc.util.RpcLog;
import rpc.util.RpcTools;


/**
 * 使用规范: 不要重用,一旦状态不合法,则直接析构掉。
 * 状态判断：调用startup（）启动handler，
 *           当调用invoke 或者 retieve出现异常的时候，表明出现无法恢复的错误，
 *           当出现无法恢复错误时，调用shutdown()关闭handler
 * 异常处理：内部有两个独立的线程RecvThread 和 SendThread，当任何一个线程异常时，就会标识 mHasException=true.
 * 			这时外部调用retrieve 或者 invoke时，就会触发异常。
 * @author 23683
 *
 */
public class RpcInvokerHandler {
	private static final String TAG = "RpcInvokerHandler";
	private static final boolean DEBUG = RpcTools.DEBUG;
	private BlockingQueue<RpcInvoker> mSendQueue = new LinkedBlockingQueue<RpcInvoker>();
	private BlockingQueue<RpcInvoker> mRecvQueue = new LinkedBlockingQueue<RpcInvoker>();
	private RpcPackageAssembly mPackageAssembly = new RpcPackageAssembly();
	private boolean mHasException = false; 
	private boolean mIsRunning = false;
	IRpcChannel mChannel = null;
	private Thread mSendThread = new Thread("SendThread") {
		@Override
		public void run() {
			while(mIsRunning && !mHasException) {
				try {
					sendInvoker();
				} catch (InterruptedException e) {
					mHasException = true;
					RpcLog.e(TAG, "sendInvoker fail: " + e);
				} catch (IOException e) {
					mHasException = true;
					RpcLog.e(TAG, "sendInvoker fail: " + e);
				}
				if (mHasException) {
					// 防止retrieve时阻塞
					addEmptyInvokerToRecvQueue();
				}
			}
			RpcLog.d(TAG, getName() + " is quit");
		}
		
	};
	
	private Thread mRecvThread = new Thread("RecvThread") {
		@Override
		public void run() {
			while(mIsRunning && !mHasException) {
				try {
					receiveInvoker();
				} catch (InterruptedException e) {
					mHasException = true;
					RpcLog.e(TAG, "receiveInvoker fail: " + e);
				} catch (IOException e) {
					mHasException = true;
					RpcLog.e(TAG, "receiveInvoker fail: " + e);
				}
				if (mHasException) {
					// 防止retrieve时阻塞
					addEmptyInvokerToRecvQueue();
				}
			}
			RpcLog.d(TAG, getName() + " is quit");
		}
		
	};
	
	/**
	 * 需要考虑避免重入， 直接简单使用synchronized保护
	 * @param channel
	 * @return
	 */
	public synchronized boolean startup() {
		if (mIsRunning) { return true; }
		RpcLog.d(TAG, "startup");
		mIsRunning = true;
		mHasException = false;
		mSendThread.setDaemon(true);
		mSendThread.start();
		mRecvThread.setDaemon(true);
		mRecvThread.start();
		return true;
	}
	
	/**
	 * 需要考虑避免重入， 直接简单使用synchronized保护
	 * @return
	 */
	public synchronized boolean shutdown() {
		if (!mIsRunning) { return true; }
		RpcLog.d(TAG, "shutdown");
		mIsRunning = false;
		// NOTE::在SendThread和RecvThread之前就close channnel，防止interrupt时遇到IO中断，而无法解除中断
		closechannel(); 
		mSendThread.interrupt();
		mRecvThread.interrupt();
		try {
			mSendThread.join();
			mRecvThread.join();
		} catch (InterruptedException e) {
			RpcLog.e(TAG, "Interrupt when wait shutdown." + e);
		}
		
		mSendQueue.clear();
		mRecvQueue.clear();
		addEmptyInvokerToRecvQueue();
		
		return true;
	}

	private void addEmptyInvokerToRecvQueue() {
		try {
			// recvqueue添加一个空对象，保证即使handler不再有效了，retrieve仍不会卡住。
			mRecvQueue.put(new RpcInvoker(null, null, null));
		} catch (InterruptedException e) {
		}
	}
	
	public boolean isAlive() {
		return mIsRunning;
	}
	
	/**
	 * 非阻塞请求。 但如果内部消息缓冲已满，则会阻塞。
	 * @param invoker
	 * @return invoker的结果，实际只是一个主动对象，里面的结果还是为null。
	 * @throws InterruptedException
	 */
	public RpcInvoker invoke(RpcInvoker invoker) throws InterruptedException {
		if (!isAlive()) { throw new InterruptedException("RpcInvokerHandler is dead\n"); }
		mSendQueue.put(invoker);
		return invoker;
	}
	
	/**
	 * 
	 * @return 当返回InterruptedException异常时，表示handler坏掉了，应该需要shutdownhandler。
	 * @throws InterruptedException
	 */
	public RpcInvoker retrieve() throws InterruptedException {
		if (!isAlive()) { throw new InterruptedException("RpcInvokerHandler is dead\n"); }
		RpcInvoker invoker = mRecvQueue.take();
		if (invoker.getId() == -1) {
			// 这个表示无效的invoker, 为了发生异常时防止这里堵住，故意推入的。
			// 因此，收到这个无效invoker，就意味着handler异常
			throw new InterruptedException("RpcInvokerHandler is dead\n");
		}
		return invoker;
	}
	
	private void sendInvoker() throws InterruptedException, IOException {
		RpcInvoker invoker = mSendQueue.take();
        RpcMessage request = invoker.getRequest();
        RpcPackage rpcpackage = new RpcPackage(request);
        if(DEBUG)RpcLog.d(TAG, "<<Messsage>>" + request.toString());
        byte[] contents = mPackageAssembly.pack(rpcpackage);
        System.out.println(contents.toString());
        mChannel.send(contents);
        mSendQueue.remove(invoker);
	}
	
	private void receiveInvoker() throws InterruptedException, IOException {
		byte[] respBytes = null;
		respBytes = mChannel.recv();
        if (respBytes != null && respBytes.length > 0) {
        	// 必须考虑粘包的问题，一次可能有多个pacakge
        	while(true) {
	            RpcPackage result =  mPackageAssembly.unpack(respBytes);
	            if (result == null) { return; }
	            RpcMessage message = result.toRpcMessage();
	            if (message == null) { return; }
				mRecvQueue.put(new RpcInvoker(message, null, null));
				respBytes = null;
        	}
        }
	}
	
	public void bindChannel(IRpcChannel channel) {
		if (mChannel != null) {
			RpcLog.w(TAG, "channel is alreay binded.");
		}
		mChannel = channel;
	}
	
	private void closechannel() {
		mChannel.close();
		// mChannel = null; //无需重置，避免空指针错误
	}
}
