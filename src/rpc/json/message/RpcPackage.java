package rpc.json.message;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import rpc.framework.message.RpcMessage;
import rpc.util.RpcLog;
import rpc.util.RpcTools;


/**
 * @author 23683
 * format: RPC@[message size][message]#rpc
 */
public class RpcPackage {

    private static final String TAG = "RpcPackage";
    private static final boolean DEBUG = RpcTools.DEBUG;
    public static final int LEST_LENGTH = 12;
    public static final int MAX_PACKAGE_SIZE = 32*1024;

    static final byte[] sHEAD = {'R','P','C','@'};
    static final byte[] sTAIL = {'#','r','p','c'};
    String mMessage = null; // request or repsonse message 
    private static Serializer mSerializer = new Serializer();
    private static Deserializer mDeserializer = new Deserializer();

    /**
     * 构造一个空包
     */
    public RpcPackage() {
    }
    
    
    public RpcPackage(RpcMessage message) {
    	mMessage = mSerializer.serialize(message);
    }
    
	public static RpcPackage decodeFromBytes(ByteSequence bytesData) {
		byte[] content = bytesData.mBytesData;
		if (content == null || content.length < LEST_LENGTH) {
			// head + size +tail = 12 bytes
			return null; 
		}
		
		if (!(content[0] == sHEAD[0] && content[1] == sHEAD[1] 
				&& content[2] == sHEAD[2] && content[3] == sHEAD[3])) {
			RpcLog.e(TAG, "invalid header");
			bytesData.pop(content.length);
			return null;
		}
		
		
		int length = RpcTools.byte4ToIntDown(content,4);
		if (content.length < length + LEST_LENGTH) {
			return null;
		}
		
		int i = 8 + length;
		if (!(content[i+0] == sTAIL[0] && content[i+1] == sTAIL[1] 
				&& content[i+2] == sTAIL[2] && content[i+3] == sTAIL[3])) {
			RpcLog.e(TAG, "invalid tail");
			bytesData.pop(content.length);
			return null;
		}
        
		RpcPackage rpcpackage = new RpcPackage();
		byte[] message = new byte[length];
		System.arraycopy(content, 8, message, 0, length);
		try {
			rpcpackage.mMessage = new String(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			RpcLog.e(TAG, e.getMessage());
			rpcpackage = null;
		} finally {
			bytesData.pop(length + LEST_LENGTH);
		}
        return rpcpackage;
	}
	
	public static byte[] encodeToBytes(RpcPackage rpcPackage) {
		byte[] message = rpcPackage.mMessage.getBytes();
		byte[] content = new byte[message.length + LEST_LENGTH];
		System.arraycopy(sHEAD, 0, content, 0, sHEAD.length);
	    RpcTools.intToByte4Down(content, 4, message.length);
	    System.arraycopy(message, 0, content, 8, message.length);
	    System.arraycopy(sTAIL, 0, content, 8+message.length, sTAIL.length);
        return content;
	}
    
	public RpcMessage toRpcMessage() {
		if (mMessage == null) { return null; }
		return mDeserializer.deserialize(mMessage);
	}
   
	public static void main(String[] args) {
		RpcRequest request = new RpcRequest(123, "test", "[1,2,3]");
		System.out.println(Arrays.toString(new byte[]{'R','P','C','@'}));
		byte[] data = RpcPackage.encodeToBytes(new RpcPackage(request));
		System.out.println(Arrays.toString(data));
		ByteSequence x = new ByteSequence();
		x.append(data);
		System.out.println(RpcPackage.decodeFromBytes(x).toRpcMessage());
	}
}
