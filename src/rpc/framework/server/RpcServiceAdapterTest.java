package rpc.framework.server;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rpc.json.message.RpcRequest;
import rpc.json.message.RpcResponse;

class RpcServiceAdapterTestHelper  {
	
	@Rpc(params={"x"})
	public int s1(int x) {
		return x;
	}
	
	@Rpc
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
		RpcServiceInterface[] s = RpcServiceAdapter.adapt(new RpcServiceAdapterTestHelper());
		assertTrue(s.length == 2);
		assertEquals(s[0].execute(new RpcRequest(1, "RpcServiceAdapterTestHelper.s1", new JSONArray("[1]"))).getResult(), 
				1);
		
		assertEquals(s[0].execute(new RpcRequest(1, "RpcServiceAdapterTestHelper.s1", new JSONObject("{'x':1}"))).getResult(), 
				1);
	}

	@Test
	public final void testExecute() {
		
	}

}
