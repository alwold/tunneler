package com.alwold.tunneler;

/**
 *
 * @author alwold
 */
public class Tunnel {
	private String name;
	private String account;
	private Integer listenPort;
	private String targetHost;
	private Integer targetPort;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getListenPort() {
		return listenPort;
	}

	public void setListenPort(Integer listenPort) {
		this.listenPort = listenPort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public Integer getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(Integer targetPort) {
		this.targetPort = targetPort;
	}
}
