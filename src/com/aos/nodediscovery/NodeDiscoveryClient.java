package com.aos.nodediscovery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

/**
 * This class is executed as thread to pass the node information of the current
 * node to the server of other known nodes. All send activities from the node are
 * done through this class.
 * 
 * @author Rahul
 *
 */
public class NodeDiscoveryClient extends Thread {
	public String port;
	public String hostName;
	public Node node;
	public static final int MESSAGE_SIZE = 1000;
	private ArrayList<Node> nodeList;
	private Integer nodeId;

	public NodeDiscoveryClient(Integer nodeId, String hostName, String port, Node node, ArrayList<Node> nodeList) {
		this.nodeId = nodeId;
		this.hostName= hostName;
		this.port = port;
		this.node = node;
		this.nodeList = nodeList;
	}

	public void run() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
		NodeInformation message = new NodeInformation(new Node(nodeId, hostName,port,true),node,nodeList);
		try
		{
			/**
			 * Send node information of the current node to the other known node
			 */
			SocketAddress socketAddress = new InetSocketAddress(node.getHostName(),Integer.valueOf(node.getPort()));
			SctpChannel sctpChannel = SctpChannel.open();
			//sctpChannel.bind(new InetSocketAddress(Integer.valueOf(port)));
			sctpChannel.connect(socketAddress);
			MessageInfo messageInfo = MessageInfo.createOutgoing(null,0);
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			ObjectOutputStream objOutStream = new ObjectOutputStream(byteOutStream);
			objOutStream.writeObject(message);
			byteBuffer.put(byteOutStream.toByteArray());
			byteBuffer.flip();
			sctpChannel.send(byteBuffer,messageInfo);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

}
