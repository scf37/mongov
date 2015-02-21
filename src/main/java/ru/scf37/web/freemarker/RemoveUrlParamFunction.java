package ru.scf37.web.freemarker;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Multimap;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
/**
 * Removes url parameter from given url
 * 
 * @author scf37
 *
 */
public class RemoveUrlParamFunction implements TemplateMethodModelEx {

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		Validate.isTrue(arguments.size() == 3 || arguments.size() == 2, "2 or 3 arguments expected(url[not req], paramName, paramValue) but %d found", arguments.size());
		
		final String url;
		final String paramName;
		final String paramValue;
		
		if (arguments.size() == 3) {
			url = arguments.get(0).toString();
			paramName = toString(arguments.get(1));
			paramValue= toString(arguments.get(2));
		} else {
			ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
			url = formatRequestUrl(attrs.getRequest());
			paramName = toString(arguments.get(0));
			paramValue= toString(arguments.get(1));
		}
			
		
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw new TemplateModelException("invalid URL format: " + url, e);
		}
		
		Multimap<String, String> params = URLEncodedUtils.parse(uri, "UTF-8");
		
		if (paramValue != null) {
			params.remove(paramName, paramValue);
		} else {
			params.removeAll(paramName);
		}
		
		try {
			String query = URLEncodedUtils.format(params, "UTF-8");
			String result = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null, null).toString();
			if (StringUtils.isNotEmpty(query)) {
				result += "?" + query;
			}
			if (uri.getFragment() != null) {
				result += "#" + uri.getFragment();
			}
			return result;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String formatRequestUrl(HttpServletRequest request) {
		StringBuilder msg = new StringBuilder();
		msg.append(request.getRequestURI());
		if (request.getQueryString() != null) {
			msg.append('?').append(request.getQueryString());
		}
		return msg.toString();
	}
	
	private String toString(Object o) {
		if (o == null) {
			return null;
		}
		if (o instanceof TemplateScalarModel) {
			try {
				return ((TemplateScalarModel)o).getAsString();
			} catch (TemplateModelException e) {
				throw new RuntimeException(e);
			}
		}
		return o.toString();
	}
}
