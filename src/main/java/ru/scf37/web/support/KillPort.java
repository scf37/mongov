package ru.scf37.web.support;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Can kill previous instance of the application when starting new instance.
 * Quite useful when doing web development since two instances cant run simultaneously because of HTTP port
 * 
 * @author scf37
 *
 */
public class KillPort {
	private Logger log = LoggerFactory.getLogger(getClass());
	private int port;
	private ServerSocket serverSocket;
	private Thread thread;
	private Object startupLock = new Object();
	
	public KillPort(int port) {
		this.port = port;
	}
	
	public void startServer() {
		thread = new Thread(server);
		thread.setDaemon(true);
		thread.start();
		synchronized (startupLock) {
			while (serverSocket == null) {
				try {
					startupLock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	public void stopServer() {
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
	}
	
	private Runnable server = new Runnable() {
		
		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(port, 2, InetAddress.getByName("127.0.0.1"));
			}
			catch (Exception ex) {
				log.error("Unable to start KillPort", ex);
				return;
			}
			synchronized (startupLock) {
				startupLock.notify();
			}

			try {
				serverSocket.accept();
				log.info("We were asked to kill yourself - terminating ungracefully");
				System.exit(0);
			} catch (IOException e) {
			}

		}
	};

	public void kill() {
		while (!doKill()) ;
	}

	private boolean doKill() {
		Socket	s = null;
		OutputStream os = null;
		try {
			s = new Socket(InetAddress.getByName("127.0.0.1"), port);
			os = s.getOutputStream();
			os.write(1);
		} catch (Exception e) {
			return true;
		} finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(s);
		}
		return false;
	}
	
}
