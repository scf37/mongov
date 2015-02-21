package ru.scf37.mongov.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import ru.scf37.mongov.dao.MongoSource;
import ru.scf37.mongov.exception.NotConnectedException;

@Controller
@Slf4j
public class MongoExceptionHandler implements HandlerExceptionResolver {
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		
		if (ex instanceof NotConnectedException) {
			log.info(ex.toString());
			return connectView(ex.getMessage());
		}
		
		log.error("",ex);
		response.setStatus(500);
		return applicationErrorView(ex);
	}

	private ModelAndView applicationErrorView(Exception ex) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("error");
		mv.addObject("message", ex.getMessage());
		
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		mv.addObject("stacktrace", sw.toString());
		return mv;
	}
			
	private ModelAndView connectView(String message) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("connect");
		mv.addObject("host", MongoSource.getInstance().getHost());
		mv.addObject("port", MongoSource.getInstance().getPort());
		mv.addObject("message", message);
		return mv;
	}

}
