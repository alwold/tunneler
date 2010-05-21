package com.alwold.tunneler;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Tunneler {
	private static Logger logger = Logger.getLogger(Tunneler.class);
	private Map<String, Account> accounts;
	private Map<String, Tunnel> tunnels;
	private Map<String, Session> sessions;

	public static void main(String[] args) throws IOException, ConfigurationException {
		new Tunneler().start();
	}

	public Tunneler() {
		accounts = new HashMap<String, Account>();
		tunnels = new HashMap<String, Tunnel>();
		sessions = new Hashtable<String, Session>();
	}
	
	public void start() throws IOException, ConfigurationException {
		readConfig();
		JSch jsch = new JSch();
		for (final Account account: accounts.values()) {
			try {
				Session session = jsch.getSession(account.getUsername(), account.getHost());
				UserInfo ui = new UserInfo() {

					public String getPassphrase() {
						return account.getPassword();
					}

					public String getPassword() {
						return account.getPassword();
					}

					public boolean promptPassword(String message) {
						return true;
					}

					public boolean promptPassphrase(String message) {
						return true;
					}

					public boolean promptYesNo(String message) {
						return true;
					}

					public void showMessage(String message) {
					}
				};
				session.setUserInfo(ui);
				session.connect();
				sessions.put(account.getName(), session);
			} catch (JSchException e) {
				logger.error("Error opening "+account.getName(), e);
			}
		}
		for (Tunnel tunnel: tunnels.values()) {
			Session session = sessions.get(tunnel.getAccount());
			try {
				session.setPortForwardingL(tunnel.getListenPort(), tunnel.getTargetHost(), tunnel.getTargetPort());
			} catch (JSchException e) {
				logger.error("Unable to open tunnel "+tunnel.getName(), e);
			}
		}
	}

	private void readConfig() throws IOException, ConfigurationException {
		Properties props = new Properties();
		props.load(Tunneler.class.getResourceAsStream("/config.properties"));
		for (String key: props.stringPropertyNames()) {
			logger.trace("key = "+key);
			try {
				if (key.startsWith("account.")) {
					String[] keyElements = key.split("\\.");
					Account account = accounts.get(keyElements[1]);
					if (account == null) {
						account = new Account();
						account.setName(keyElements[1]);
						accounts.put(keyElements[1], account);
					}
					logger.trace("account keyElements[2] = "+keyElements[2]);
					if (keyElements[2].equals("host")) {
						account.setHost(props.getProperty(key));
					} else if (keyElements[2].equals("username")) {
						account.setUsername(props.getProperty(key));
					} else if (keyElements[2].equals("password")) {
						account.setPassword(props.getProperty(key));
					} else {
						throw new ConfigurationException("Unrecognized key in config: "+key);
					}
				} else if (key.startsWith("tunnel.")) {
					String[] keyElements = key.split("\\.");
					Tunnel tunnel = tunnels.get(keyElements[1]);
					if (tunnel == null) {
						tunnel = new Tunnel();
						tunnel.setName(keyElements[1]);
						tunnels.put(keyElements[1], tunnel);
					}
					if (keyElements[2].equals("account")) {
						tunnel.setAccount(props.getProperty(key));
					} else if (keyElements[2].equals("listenPort")) {
						tunnel.setListenPort(Integer.parseInt(props.getProperty(key)));
					} else if (keyElements[2].equals("targetHost")) {
						tunnel.setTargetHost(props.getProperty(key));
					} else if (keyElements[2].equals("targetPort")) {
						tunnel.setTargetPort(Integer.parseInt(props.getProperty(key)));
					} else {
						throw new ConfigurationException("Unrecognized key in config: "+key);
					}

				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new ConfigurationException("Error parsing "+key, e);
			}
		}
		// check config
		for (Account account: accounts.values()) {
			if (account.getHost() == null) throw new ConfigurationException("Account "+account.getName()+" is missing host");
			if (account.getPassword() == null) throw new ConfigurationException("Account "+account.getName()+" is missing password");
			if (account.getUsername() == null) throw new ConfigurationException("Account "+account.getName()+" is missing username");
		}
		for (Tunnel tunnel: tunnels.values()) {
			if (tunnel.getAccount() == null) throw new ConfigurationException("Tunnel "+tunnel.getName()+" is missing account");
			if (tunnel.getListenPort() == null) throw new ConfigurationException("Tunnel "+tunnel.getName()+" is missing listenPort");
			if (tunnel.getTargetHost() == null) throw new ConfigurationException("Tunnel "+tunnel.getName()+" is missing targetHost");
			if (tunnel.getTargetPort() == null) throw new ConfigurationException("Tunnel "+tunnel.getName()+" is missing targetPort");
			if (accounts.get(tunnel.getAccount()) == null) throw new ConfigurationException("Account "+tunnel.getAccount()+" not found for tunnel "+tunnel.getName());
		}
	}
}
