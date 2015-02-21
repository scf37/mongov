package ru.scf37.web.freemarker;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * encodes/decodes data in a way compatible with $.param
 * @author scf37
 *
 */

@Component
public class URLCodec {
	
	public Map<String, Object> decode(String s) {
		Map<String, Object> result = Maps.newLinkedHashMap();
		if (s == null) {
			return result;
		}

		int qix = s.indexOf('?');
		if (qix >= 0) {
			if (qix == s.length()) {
				return result;
			}
			s = s.substring(qix + 1);
		}

		Map<String, Object> obj = Maps.newLinkedHashMap();

		// Iterate over all name=value pairs.
		for (String pair : s.split("&")) {
			String[] param = pair.split("=");

			String key = urldecode(param[0]), val;

			if (key.isEmpty())
				continue;

			Object cur = obj;
			int i = 0;

			// If key is more complex than 'foo', like 'a[]' or 'a[b][c]', split
			// it
			// into its component parts.
			String[] keys = key.split("\\]\\[");
			int keys_last = keys.length - 1;

			// If the first keys part contains [ and the last ends with ], then
			// []
			// are correctly balanced.
			if (keys[0].contains("[") && keys[keys_last].endsWith("]")) {
				// Remove the trailing ] from the last keys part.
				keys[keys_last] = keys[keys_last].replace("]", "");

				// Split first keys part into two parts on the [ and add them
				// back onto
				// the beginning of the keys array.
				keys = ArrayUtils.addAll(keys[0].split("\\["), Arrays.copyOfRange(keys, 1, keys.length));
				keys_last = keys.length - 1;
			} else {
				// Basic 'foo' style key.
				keys_last = 0;
			}

			// Are we dealing with a name=value pair, or just a name?
			if (param.length == 2) {
				val = urldecode(param[1]);

				if (keys_last > 0) {
					// Complex key, build deep object structure based on a few
					// rules:
					// * The 'cur' pointer starts at the object top-level.
					// * [] = array push (n is set to array length), [n] = array
					// if n is
					// numeric, otherwise object.
					// * If at the last keys part, set the value.
					// * For each keys part, if the current level is undefined
					// create an
					// object or array based on the type of the next keys part.
					// * Move the 'cur' pointer to the next level.
					// * Rinse & repeat.
					for (; i <= keys_last; i++) {
						key = keys[i];
						if (i < keys_last) {
							if (key.isEmpty()) {
								if (cur instanceof List) {
									((List<String>) cur).add(val);
								}
							} else if (Character.isDigit(key.charAt(0))) {
								int ix;
								try {
									ix = Integer.parseInt(key);
								} catch (NumberFormatException ex) {
									continue;
								}
								if (cur instanceof List && ix >= 0 && ix <= 9000) {
									// never, never send over 9000 items via GET
									// string
									List<Object> l = (List<Object>) cur;
									while (l.size() <= ix) {
										l.add(null);
									}
									if (l.get(ix) == null) {
										if (isArrayKey(keys[i + 1])) {
											l.set(ix, Lists.newArrayList());
										} else {
											l.set(ix, Maps.newLinkedHashMap());
										}
									}

									cur = l.get(ix);
								}

							} else {
								if (cur instanceof Map) {
									Map<String, Object> map = (Map<String, Object>) cur;
									if (!map.containsKey(key)) {
										if (isArrayKey(keys[i + 1])) {
											map.put(key, Lists.newArrayList());
										} else {
											map.put(key, Maps.newLinkedHashMap());
										}
									}
									cur = map.get(key);
								}
							}

						} else {
							if (cur instanceof List) {
								List<Object> l = (List<Object>) cur;
								int ix;
								try {
									ix = Integer.parseInt(key);
								} catch (NumberFormatException ex) {
									continue;
								}
								while (l.size() <= ix) {
									l.add(null);
								}
								l.set(ix, val);
							} else {
								((Map<String, Object>) cur).put(key, val);
							}

						}
					}

				} else {
					// Simple key, even simpler rules, since only scalars and
					// shallow
					// arrays are allowed.
					key = keys[0];
					if (obj.get(key) instanceof List) {
						// val is already an array, so push on the next value.
						((List<Object>) (obj.get(key))).add(val);

					} else if (obj.containsKey(key)) {
						// val isn't an array, but since a second value has been
						// specified,
						// convert val into an array.
						obj.put(key, Lists.newArrayList(obj.get(key), val));

					} else {
						// val is a scalar.
						obj.put(key, val);
					}
				}

			} else if (key != null) {
				// No value was defined, so set something meaningful.
				obj.put(key, "");
			}
		}
		;

		return obj;
	}

	private boolean isArrayKey(String nextKey) {
		return nextKey.isEmpty() || Character.isDigit(nextKey.charAt(0));
	};

	private String urldecode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// will never happen
			return s;
		}
	}

	public String encode(Map<String, Object> a) {
		if (a == null) {
			return "";
		}
		List<String> s = Lists.newArrayList();

		for (String key : a.keySet()) {
			buildParams(key, a.get(key), s);
		}

		return StringUtils.join(s, "&").replace(" ", "+");
	}

	void buildParams(String key, Object obj, List<String> result) {
		if (obj instanceof List) {
			List<Object> l = (List<Object>) obj;
			for (int i = 0; i < l.size(); i++) {
				Object v = l.get(i);

				if (key.contains("]")) {
					// Treat each array item as a scalar.

					add(result, key, (String) v);

				} else {
					// If array item is non-scalar (array or object), encode its
					// numeric index to resolve deserialization ambiguity
					// issues.
					// Note that rack (as of 1.0.0) can't currently deserialize
					// nested arrays properly, and attempting to do so may cause
					// a server error. Possible fixes are to modify rack's
					// deserialization algorithm or to provide an option or flag
					// to force array serialization to be shallow.
					buildParams(key + "[" + (!(v instanceof String) ? String.valueOf(i) : "") + "]", v, result);
				}
			}

		} else if (obj instanceof Map) {
			// Serialize object item.
			for (Entry<String, Object> entry : ((Map<String, Object>) obj).entrySet()) {
				buildParams(key + "[" + entry.getKey() + "]", entry.getValue(), result);
			}

		} else {
			// Serialize scalar item.
			add(result, key, (String) obj);
		}
	}
	
	private void add(List<String> params, String k, String v) {
		params.add(urlencode(k) + "=" + urlencode(v));
	}

	private String urlencode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
		} catch (UnsupportedEncodingException e) {
			// will never happen
			return s;
		}
	}
}
