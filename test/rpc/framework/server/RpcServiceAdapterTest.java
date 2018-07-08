package rpc.framework.server;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rpc.exception.RpcException;
import rpc.framework.server.annotation.RpcMethod;
import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;

class RpcServiceAdapterTestHelper  {
	
	@RpcMethod(params={"x"})
	public int s1(int x) {
		return x;
	}
	
	@RpcMethod
	public int s2() {
		return 0;
	}
}

public class RpcServiceAdapterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testAdaptObjectStringClassOfQArray() {
		
	}

	@Test
	public final void testAdaptObjectStringArray() {
		
	}

	@Test
	public final void testAdaptObject() {
		RpcServiceInterface s = RpcServiceAdapter.adapt(new RpcServiceAdapterTestHelper());
		try {
		assertEquals(s.execute(new RpcRequest(1, "RpcServiceAdapterTestHelper.s1", new JSONArray("[1]"))).getResult(), 
					1);
		assertEquals(s.execute(new RpcRequest(1, "RpcServiceAdapterTestHelper.s1", new JSONObject("{'x':1}"))).getResult(), 
				1);
		
		assertEquals(s.execute(new RpcRequest(1, "RpcServiceAdapterTestHelper.s2", new JSONObject("{'x':1}"))).getResult(), 
				0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public final void testExecute() {
		
	}

}
