package com.aos.nodediscovery;
import java.io.*;
import java.net.*;
import com.sun.nio.sctp.*;
import java.nio.*;
public class SctpServer 
{
	public static final int MESSAGE_SIZE = 100;
	public void go()
	{
		//Buffer to hold messages in byte format
		ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
		String message;
		try
		{
			//Open a server channel
			SctpServerChannel sctpServerChannel = SctpServerChannel.open();
			//Create a socket addess in the current machine at port 5000
			InetSocketAddress serverAddr = new InetSocketAddress(5000);
			//Bind the channel's socket to the server in the current machine at port 5000
			sctpServerChannel.bind(serverAddr);
			//Server goes into a permanent loop accepting connections from clients			
			while(true)
			{
				//Listen for a connection to be made to this socket and accept it
				//The method blocks until a connection is made
				//Returns a new SCTPChannel between the server and client
				SctpChannel sctpChannel = sctpServerChannel.accept();
				//Receive message in the channel (byte format) and store it in buf
				//Note: Actual message is in byre format stored in buf
				//MessageInfo has additional details of the message
				MessageInfo messageInfo = sctpChannel.receive(byteBuffer,null,null);
				//Just seeing what gets stored in messageInfo
				System.out.println(messageInfo);
				//Converting bytes to string. This looks nastier than in TCP
				//So better use a function call to write once and forget it :)
				message = byteToString(byteBuffer);
				//Finally the actual message
				System.out.println(message);
			}

		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public String byteToString(ByteBuffer byteBuffer)
	{
		byteBuffer.position(0);
		byteBuffer.limit(MESSAGE_SIZE);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}

	public static void main(String args[])
	{
		
		SctpServer SctpServerObj = new SctpServer();
		SctpServerObj.go();
	}

}
