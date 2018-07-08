//package rpc.framework.server;
//
//public class Event {
//	public String ID;
//	
//	public Event(String iD) {
//		super();
//		ID = iD;
//	}
//
//	@Override
//	public int hashCode() {
//		return ID.hashCode(); // FIXME:: same content string but not the same object?
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof Event) {
//			return this.ID == ((Event) obj).ID;
//		} else {
//			return super.equals(obj);
//		}
//	}
//	
//	@Override
//	public String toString() {
//		return ID;
//	}
//}
