package ru.scf37.web.freemarker;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.Validate;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
/**
 * Adds new paramterer to given URL
 * args:
 * url, parameter name, parameter value
 * 
 * @author scf37
 *
 */
public class AppendUrlParamFunction implements TemplateMethodModelEx {

	@Override
	public Object exec(List arguments) throws TemplateModelException {
		Validate.isTrue(arguments.size() == 3, "3 arguments expected(url, paramName, paramValue) but %d found", arguments.size());
		Validate.isInstanceOf(SimpleScalar.class, arguments.get(0), "url must be a String");
		Validate.isInstanceOf(SimpleScalar.class, arguments.get(1), "paramName must be a String");
		Validate.isInstanceOf(SimpleScalar.class, arguments.get(1), "paramValue must be a String");
		String url = arguments.get(0).toString();
		String paramName = arguments.get(1).toString();
		String paramValue= arguments.get(2).toString();
		
		String result = url;
		if (url.contains("?")) {
			result += "&";
		} else {
			result += '?';
		}
		result += urlencode(paramName) + '=' + urlencode(paramValue);
		return result;
	}

	private String urlencode(String paramValue) {
		try {
			return URLEncoder.encode(paramValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
