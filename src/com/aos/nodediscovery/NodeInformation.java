package com.aos.nodediscovery;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class acts as a bean to store the information sent/received through the
 * network. It stores the information about the sender and destination nodes along with
 * the list of sender's known nodes.
 * 
 * @author Rahul
 *
 */
public class NodeInformation implements Serializable {

	private static final long serialVersionUID = -5939082414891168405L;
	private ArrayList<Node> nodes;
	private Node node;
	private Node source;
	
	public NodeInformation(Node source, Node node, ArrayList<Node> nodes){
		this.source = source;
		this.node = node;
		this.nodes = nodes;
	}

	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	public Node getSource() {
		return source;
	}
	public void setSource(Node source) {
		this.source = source;
	}
}
