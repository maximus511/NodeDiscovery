package com.aos.nodediscovery;
import java.io.*;
import java.net.*;
import com.sun.nio.sctp.*;
import java.nio.*;
public class SctpClient 
{
	public static final int MESSAGE_SIZE = 100;
	public void go()
	{
		//Buffer to hold messages in byte format
		ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
		String message = "Hello from client";
		try
		{
			//Create a socket address for  server at net01 at port 5000
			SocketAddress socketAddress = new InetSocketAddress("net01.utdallas.edu",5003);
			//Open a channel. NOT SERVER CHANNEL
			SctpChannel sctpChannel = SctpChannel.open();
			//Bind the channel's socket to a local port. Again this is not a server bind
			sctpChannel.bind(new InetSocketAddress(5000));
			//Connect the channel's socket to  the remote server
			sctpChannel.connect(socketAddress);
			//Before sending messages add additional information about the message
			MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
			//convert the string message into bytes and put it in the byte buffer
			byteBuffer.put(message.getBytes());
			//Reset a pointer to point to the start of buffer 
			byteBuffer.flip();
			//Send a message in the channel (byte format)
			sctpChannel.send(byteBuffer,messageInfo);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		SctpClient SctpClientObj = new SctpClient();
		SctpClientObj.go();
	}
}
