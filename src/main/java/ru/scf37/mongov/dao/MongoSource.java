package ru.scf37.mongov.dao;

import javax.servlet.http.HttpSession;

import lombok.Data;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mongodb.MongoClient;

@Data
public class MongoSource {
	private String host;
	private int port;
	private MongoClient mongo;
	
	public MongoSource(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public MongoSource(String host) {
		this(host, 27017);
	}
	
	public void disconnect() {
		if (mongo != null) {
			mongo.close();
		}
		mongo = null;
	}
	
	public void connect() throws Exception {
		disconnect();
		mongo = new MongoClient(host, port);
		mongo.getDatabaseNames();
	}
	
	public static MongoSource getInstance() {
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
		MongoSource source = (MongoSource)session.getAttribute("mongoSource");
		if (source == null) {
			source = new MongoSource("localhost");
			session.setAttribute("mongoSource", source);
		}
		return source;
	}
}
