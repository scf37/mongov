package ru.scf37.mongov.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.scf37.mongov.dao.MongoDao;
import ru.scf37.mongov.dao.MongoSource;
import ru.scf37.mongov.exception.NotConnectedException;

@Controller
public class ConnectPageController {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MongoDao mongoDao;
	
	@RequestMapping(value="/connect", method=RequestMethod.POST)
	public String connect(Model model_,
		HttpServletRequest servletRequest,
		HttpServletResponse servletResponse,
		@RequestParam(value="host") String host,
		@RequestParam(value="port") int port
		) {
		
		MongoSource.getInstance().setHost(host);
		MongoSource.getInstance().setPort(port);
		try {
			MongoSource.getInstance().connect();
		} catch (Exception e) {
			throw new NotConnectedException(e.getMessage() + (e.getCause() != null? ", " + e.getCause().getMessage() : ""));
		}
		
		String referer = servletRequest.getHeader("Referer");
		
		if (referer == null) {
			return "redirect:/";
		}
		
		servletResponse.setHeader("Location", referer);
		servletResponse.setStatus(303);
		return null;
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.GET)
	public String connect() {
		return "redirect:/";
	}
	
	@RequestMapping(value="/disconnect", method=RequestMethod.GET)
	public String disconnect() {
		try {
			MongoSource.getInstance().disconnect();
		} catch (Exception e) {
			log.warn("Error disconnecting mongo", e);
		}
		return "redirect:/";
	}
}
