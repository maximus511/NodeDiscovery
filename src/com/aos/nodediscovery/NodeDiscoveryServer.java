package com.aos.nodediscovery;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

/**
 * This class acts as the server for the current node.
 * Any request from other nodes in the network is handled by the server code.
 * This also acts as the main class of the project.
 * 
 * @author Rahul
 *
 */
public class NodeDiscoveryServer {
	public static String port;
	public static String hostName;
	public static HashMap<Integer, Node> knownNeighbors = new HashMap<Integer, Node>();
	public static final int MESSAGE_SIZE = 1000;

	public static void main(String args[]) {

		if(args.length != 1)
		{
			System.out.println ("Invalid number of arguments!!");
			return;
		}
		ArrayList<Node> nodeList = new ArrayList<Node>();
		Integer currentNodeId = Integer.valueOf(args[0]);
		readFromConfig(nodeList, currentNodeId);
		ByteBuffer byteBuffer = ByteBuffer.allocate(MESSAGE_SIZE);
		NodeInformation nodeInfo;
		try
		{
			//Create server channel
			SctpServerChannel sctpServerChannel = SctpServerChannel.open();
			InetSocketAddress serverAddr = new InetSocketAddress(Integer.valueOf(port));
			sctpServerChannel.bind(serverAddr);
			
			//Count is used to track whether the current node has connected with all the known nodes
			int count = 0;
			
			//Send current node information to all the initial set of known nodes
			for(Map.Entry<Integer,Node> knownEntries: knownNeighbors.entrySet())
			{
				if(!knownEntries.getValue().getIsVisited() && !knownEntries.getValue().getHostName().equals(hostName))
				{
					//Once node information is sent for a known node, mark it as visited
					//to avoid sending duplicate messages
					knownEntries.getValue().setIsVisited(true);
					Thread.sleep(4000);
					Thread t1 = new Thread(new NodeDiscoveryClient(currentNodeId,hostName, port, knownEntries.getValue(), nodeList));
					t1.start();
					count++;
					//Thread.sleep(6000);
				}
			}
			//Server will be active till all the nodes are identified
			while(true)
			{
				Thread.sleep(2000);
				SctpChannel sctpChannel = sctpServerChannel.accept();
				byteBuffer.clear();
				@SuppressWarnings("unused")
				MessageInfo messageInfo = sctpChannel.receive(byteBuffer,null,null);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(byteBuffer.array());
				ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
				nodeInfo = (NodeInformation) objInputStream.readObject();	
				System.out.println("Message: "+nodeInfo.getSource().getHostName()+" "+nodeInfo.getSource().getPort());
				//Check if the received message is from a known node
				if(knownNeighbors.containsKey(nodeInfo.getSource().getNodeId()))
				{
					count--;
					//Check if the incoming node information is already present in knownNeighbors map
					for(Node incomingNode : nodeInfo.getNodes())
					{
						if(!knownNeighbors.containsKey(incomingNode.getNodeId()))
						{
							incomingNode.setIsVisited(false);
							knownNeighbors.put(incomingNode.getNodeId(), incomingNode);
						}
					}
				}
				else {
					//If its a new node, then mark it as visited and add it to the knownNeighbors map
					Node newNode = nodeInfo.getSource();
					newNode.setIsVisited(true);
					knownNeighbors.put(newNode.getNodeId(), newNode);
					for(Node incomingNode : nodeInfo.getNodes())
					{
						if(!knownNeighbors.containsKey(incomingNode.getNodeId()))
						{
							incomingNode.setIsVisited(false);
							knownNeighbors.put(incomingNode.getNodeId(), incomingNode);
						}
					}
					Thread t2 = new Thread(new NodeDiscoveryClient(currentNodeId,hostName, port, newNode, nodeList));
					t2.start();
				}
				//Initiate send request to all the nodes in the map which have not been Visited yet
				for(Map.Entry<Integer,Node> knownEntries: knownNeighbors.entrySet())
				{
					if(!knownEntries.getValue().getIsVisited() && !knownEntries.getValue().getHostName().equals(hostName))
					{
						knownEntries.getValue().setIsVisited(true);
						Thread t = new Thread(new NodeDiscoveryClient(currentNodeId,hostName, port, knownEntries.getValue(), nodeList));
						t.start();
						count++;
						//Thread.sleep(6000);
					}
				}
				//Count is zero when all the nodes have been identified
				if(count==0)
				{
					/**
					 * Save the node information onto a file
					 */
					String fileName = hostName+".txt";
					PrintWriter writer = new PrintWriter(fileName, "UTF-8");
					writer.println("Node Count : "+knownNeighbors.size());
					for(Map.Entry<Integer, Node> entry : knownNeighbors.entrySet())
					{
						Node node = entry.getValue();
						System.out.println(node.getNodeId()+"	"+node.getHostName()+"	 "+node.getPort());
						writer.println(node.getNodeId()+"	"+node.getHostName()+"	 "+node.getPort());
					}
					writer.close();
					//Exit the program when done
					System.exit(0);
				}

			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * Method to read from config file.
	 * This method reads the relevant information for the current Node.
	 * @param nodeList
	 * @param currentNodeId
	 */
	private static void readFromConfig(ArrayList<Node> nodeList,
			Integer currentNodeId) {
		String currentLine;
		HashMap<Integer, String> portMap = new HashMap<Integer, String>();
		String[] neighbors = null;
		BufferedReader br = null;
		
		try 
		{
			FileInputStream in = new FileInputStream("config.txt");
			br = new BufferedReader(new InputStreamReader(in));

			while ((currentLine = br.readLine()) != null) 
			{
				if(currentLine.startsWith("#") || currentLine.equals("") || currentLine.startsWith("N"))
				{
					continue;
				}

				String[] nodeDetails = currentLine.split("\t");
				if(nodeDetails.length>3)
				{
					portMap.put(Integer.valueOf(nodeDetails[0].trim()), nodeDetails[1].trim()+"-"+nodeDetails[2].trim());
					
					if(nodeDetails[0].equals(currentNodeId.toString()))
					{
						hostName = nodeDetails[1]+".utdallas.edu";
						port = nodeDetails[2];
						neighbors=nodeDetails[3].split("-");
					}
				}

			}
			for(String nodeId: neighbors)
			{
				Integer iNodeId = Integer.valueOf(nodeId.trim());
				String[] neighborHost= portMap.get(iNodeId).split("-");
				Node node = new Node(iNodeId,neighborHost[0].trim()+".utdallas.edu",neighborHost[1].trim(), false);
				knownNeighbors.put(iNodeId,node);
				nodeList.add(node);
			}

		} catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
