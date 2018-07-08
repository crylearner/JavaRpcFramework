//package rpc.framework.server;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.json.JSONObject;
//
//import rpc.framework.RpcInvoker;
//import rpc.framework.RpcServerInvokerHandler;
//import rpc.json.message.RpcNotification;
//import rpc.util.IDGenerator;
//import rpc.util.RpcLog;
//
///**
// * @author sunshyran
// *
// */
//public class EventCenter implements EventListenerInterface {
//	private static final String TAG="EventCenter";
//	private RpcServerInvokerHandler mHandler = null;
//	private IDGenerator mSIDGen = new IDGenerator();  // TODO:: reuse id generator temporally
//	public static final long INVALID_SID = -1;
//	private HashSet<EventSourceInterface> mSources = new HashSet<EventSourceInterface>();
//	
//	private class EventListener implements EventListenerInterface {
//		private long mSID = -1;
//		private EventListenerInterface mListener = null;
//		public EventListener(long SID, EventListenerInterface listener) {
//			mSID = SID;
//			mListener = listener;
//		}
//		@Override
//		public void onNotify(Event event, Object data) {
//			mListener.onNotify(event, data);
//		}
//		
//	}
//	private ConcurrentHashMap<Event, LinkedList<EventListener>> mListeners = new ConcurrentHashMap<Event, LinkedList<EventListener>>();
//
//	/**add event source into event center
//	 * @param source
//	 */
//	public void addEventSource(EventSourceInterface source) {
//		synchronized (mSources) {
//			if (mSources.contains(source)) {
//				RpcLog.w(TAG, "source " + source + " is already added into event center!");
//			} else {
//				RpcLog.i(TAG, "add source " + source + " into event center");
//				mSources.add(source);
//			}
//		}
//	}
//	
//	/**remove  source from event center
//	 * @param source
//	 */
//	public void removeEventSource(EventSourceInterface source) {
//		synchronized (mSources) {
//			if (!mSources.contains(source)) {
//				RpcLog.w(TAG, "source " + source + " is not in event center!");
//			} else {
//				RpcLog.i(TAG, "remove source " + source + " from event center");
//				mSources.remove(source);
//			}
//		}
//	}
//	
//	public long addEventListener(Event event, EventListener listener) {
//		boolean isAcceptable = false;
//		synchronized (mSources) {
//			for (EventSourceInterface source:mSources) {
//				if (source.acceptEvent(event)) {
//					if (source.subscribe(event, this)) {
//						isAcceptable = true;
//					} else {
//						RpcLog.e(TAG, "subscribe event of source failed: " + event + ", " + source);
//					}
//				}
//			}
//		}
//		
//		if (!isAcceptable) {
//			RpcLog.e(TAG, "no source accept event " + event);
//			return INVALID_SID;
//		}
//		
//		long sid = aquireSID();
//		LinkedList<EventListener> listeners = mListeners.getOrDefault(event, new LinkedList<EventListener>());
//		synchronized (listeners) {  // FIXME:: thread safe is enough?
//			listeners.add(new EventListener(sid, listener));
//			mListeners.put(event, listeners);
//		}
//		return sid;
//	}
//	
//	public void removeEventListener(Event event, EventListener listener) {
//		boolean isAcceptable = false;
//		synchronized (mSources) {
//			for (EventSourceInterface source:mSources) {
//				if (source.acceptEvent(event)) {
//					source.unsubscribe(event, this); 
//				}
//			}
//		}
//		
//		
//	}
//	
//	
//	
//	private long aquireSID() {
//		return mSIDGen.getRequestId();
//	}
//	
//	
//	@Override
//	public void onNotify(Event event, Object data) {
//		System.out.println("onNotify");
//		HashSet<EventListener> listeners = mListeners.get(event);
//		if (listeners == null) { return; }
//		ArrayList<EventListener> listeners = new ArrayList<EventListener>(listeners.);
//		
//		RpcNotification notification = new RpcNotification();
//		notification.setMethod(event.method);
//		JSONObject params = new JSONObject();
//		params.put("SID", sid);
//		params.put("data", data);
//		notification.setParams(params);
//		try {
//			notify(notification);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private long getRemoteEventListener(Event event) {
//		returen 0
//	}
//	
//	private void notify(RpcNotification notification) throws InterruptedException {
//		mHandler.invoke(new RpcInvoker(notification, null, null));
//	}
//}
