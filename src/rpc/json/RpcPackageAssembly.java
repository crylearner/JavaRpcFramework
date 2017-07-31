package rpc.json;

import java.util.ArrayList;
import java.util.List;

import rpc.util.RpcTools;

/**
 * @author 23683
 *
 */
public class RpcPackageAssembly {

	private List<RpcPackage> mListContent = new ArrayList<RpcPackage>();
	private ByteSequence mBytesData= new ByteSequence(); ///网络过来的字节串
	private boolean DEBUG = RpcTools.DEBUG;
	private static final String TAG = "RpcPackageAssembly";
	
	public byte[] pack(RpcPackage rpcPackage) {
		return RpcPackage.encodeToBytes(rpcPackage);
	}

	public RpcPackage unpack(byte[] indexContent) {
		mBytesData.append(indexContent);
		return RpcPackage.decodeFromBytes(mBytesData);
	}



}
