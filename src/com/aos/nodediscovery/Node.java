package com.aos.nodediscovery;

import java.io.Serializable;

/**
 * This class acts a bean to store information of a Node.
 * 
 * @author Rahul
 */

public class Node implements Serializable {

	private static final long serialVersionUID = -3536888439038198041L;
	private Integer nodeId;
	private String hostName;
	private String port;
	//isVisited is used to avoid sending duplicate messages to the same node
	private Boolean isVisited;

	public Node(Integer nodeId, String hostName, String port, Boolean isVisited)
	{
		this.nodeId= nodeId;
		this.hostName= hostName;
		this.port = port;
		this.isVisited =isVisited;
	}

	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public Boolean getIsVisited() {
		return isVisited;
	}
	public void setIsVisited(Boolean isVisited) {
		this.isVisited = isVisited;
	}
	public Integer getNodeId() {
		return nodeId;
	}
	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}
}
