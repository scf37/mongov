package ru.scf37.mongov;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import ru.scf37.config.ConfigFactory;
import ru.scf37.config.annotation.ConfigProperty;
import ru.scf37.config.util.ConfigLog4jConfigurer;
import ru.scf37.web.support.KillPort;

public class Main {
	public static void main(String[] args) throws Exception {
		ConfigLog4jConfigurer conf = new ConfigLog4jConfigurer();
		conf.configure(null);
		
		MongovConfig config = ConfigFactory.readPropertiesFrom("classpath:")
				.overrideWithSystemProperties()
				.overrideWithEnvironmentVariables()
				.buildAnnotationReader(MongovConfig.class)
				.read("mongov.properties");
		
		if (config.killPort > 0) {
			KillPort kp = new KillPort(config.killPort);
			kp.kill();
			kp.startServer();
		}
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);
        connector.setPort(config.port);
       	connector.setHost(config.bindAddr);
        server.setConnectors(new Connector[]{connector});
		server.setHandler(buildWebAppContext(server, config));
		server.start();
		for (;;) {
			Thread.sleep(1000);
		}
	}
	
	public static WebAppContext buildWebAppContext(Server server, MongovConfig config){
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setDescriptor("WEB-INF/web.xml");
		webAppContext.setResourceBase(config.root);
		webAppContext.setContextPath(config.contextPath);
		webAppContext.setServer(server);
		return webAppContext;
	}
	
	public static class MongovConfig {
		@ConfigProperty("mongov.root")
		public String root;
		@ConfigProperty("mongov.port")
		public int port;
		@ConfigProperty(value="mongov.bindAddr", mandatory = false)
		public String bindAddr = "0.0.0.0";
		@ConfigProperty(value="mongov.contextPath", mandatory=false)
		public String contextPath = "/";
		@ConfigProperty(value="mongov.killPort", mandatory=false)
		public int killPort = 0;
	}
}
