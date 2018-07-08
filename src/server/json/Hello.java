package server.json;

import rpc.util.RpcLog;

public class Hello {
	public interface AgeListener {
		void onChanged(int age);
	}
	
	private AgeListener mAgeListener;
	String mName = "no one";
	int 	mAge = 0;
	
	public String sayName() {
		return mName;
	}
	
	public int add(int a, int b) {
		return a+b;
	}
	
	public void giveName(String name) {
		mName = name;
	}
	
	public int growUp() {
		++mAge;
		if (mAgeListener!=null) { 
			RpcLog.i("Hello", "age is changed");
			mAgeListener.onChanged(mAge); 
		}
		return mAge;
	}
	
	public void observeAge(String type, AgeListener listener) {
		//if (type == "growup") {
		RpcLog.i("Hello", "observeAge");
			mAgeListener = listener;
		//}
	}
	
	public void unobserveAge(String type, AgeListener listener) {
		//if (type == "growup") {
			mAgeListener = null;
		//}
	}
	
}
