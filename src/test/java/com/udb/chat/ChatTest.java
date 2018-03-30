package com.udb.chat;
import org.junit.Test;

import com.udb.client.ClientStart;
import com.udb.protocol.ChatAction;

import io.netty.buffer.ByteBuf;

public class ChatTest {
	
	@Test
	public void login()
	{
		try {
			new ClientStart().setUser("afa005de990a0c3e1a92144fe05c1e00","e10adc3949ba59abbe56e057f20f883e").init().doConnect();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	

}
