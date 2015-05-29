package com.aos.nodediscovery;
import java.io.*;
import java.net.*;
public class TCPSampleServer 
{
	public void go()
	{
		String message="Hello from server";
		try
		{
			//Create a server socket at port 5000
			ServerSocket serverSock = new ServerSocket(5000);
			//Server goes into a permanent loop accepting connections from clients			
			while(true)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket sock = serverSock.accept();
				//PrintWriter is a bridge between character data and the socket's low-level output stream
				PrintWriter writer = new PrintWriter(sock.getOutputStream());
				writer.println(message);
				writer.close();
			}

		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	public static void main(String args[])
	{
		
		TCPSampleServer SampleServerObj = new TCPSampleServer();
		SampleServerObj.go();
	}

}
