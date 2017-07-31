package rpc.json;

public class ByteSequence {
	public byte[] mBytesData = null; 
	
	
	public void append(byte[] data) {
		if (data == null || data.length == 0) { return; }
		if (mBytesData == null || mBytesData.length == 0) {
			mBytesData = data;
			return;
		}
		byte[] newdata = new byte[mBytesData.length + data.length];
		System.arraycopy(mBytesData, 0, newdata, 0, mBytesData.length);
		System.arraycopy(data, 0, newdata, mBytesData.length, data.length);
		mBytesData = newdata;
	}
	
	public void pop(int count) {
		if (mBytesData.length <= count) {
			mBytesData = null;
			return;
		}
		
		byte[] newdata = new byte[mBytesData.length-count];
		System.arraycopy(mBytesData, count, newdata, 0, mBytesData.length - count);
		mBytesData = newdata;
	}
}