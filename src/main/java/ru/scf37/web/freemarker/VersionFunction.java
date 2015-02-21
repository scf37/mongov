package ru.scf37.web.freemarker;

import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.Validate;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * Produces application version to append to all app's urls.
 * If running in dev mode it uses current timestamp as version thus eliminating effect of browser cache.
 * If running as deployed application, it uses timestamp of WEB-INF/web.xml as version
 * 
 * @author scf37
 *
 */
@Slf4j
public class VersionFunction implements TemplateMethodModelEx {
	private static long version = 0;
	
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		Validate.isTrue(arguments.size() == 0, "no parameters expected but %d found", arguments.size());
		if (version == 0) {
			version = detectVersion();
		}
		if (version > 0) {
			return numberToString(version);
		}
		return timestampToString(System.currentTimeMillis());
		
	}
	
	private long detectVersion() {
//		String url = VersionFunction.class.getClassLoader().getResource("").toString();
//		int webinf = url.indexOf("/WEB-INF/");
//		if (webinf > 0) {
//			try {
//				url = url.substring(0, webinf + "/WEB-INF/".length()) + "web.xml";
//				long v = new UrlResource(url).lastModified() / 1000 / 60;
//				log.info("version detected: {}", v);
//				return v;
//			} catch (IOException e) {
//				log.warn("Error detecting web.xml timestamp", e);
//			}
//		}
	
		return -1;
	}

	private static final char[] symbols = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
										  "abcdefghijklmnopqrstuvwxyz" + 
										  "0123456789" +
										  "-._~").toCharArray();
	private static final int symbols_len = symbols.length;
	private static final long start_timestamp = new Date(2013 - 1900, 5, 1).getTime();
	
	private String timestampToString(long timestamp) {
		long t = (timestamp - start_timestamp) / 1000;
		return numberToString(t);
	}

	private String numberToString(long t) {
		StringBuilder sb = new StringBuilder();
		while (t > 0) {
			sb.append(symbols[(int)(t % symbols_len)]);
			t /= symbols_len;
		}
		return sb.toString();
	}
}
