package ru.scf37.mongov.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.scf37.mongov.dao.MongoDao;
import ru.scf37.mongov.dao.MongoSource;
import ru.scf37.web.freemarker.URLCodec;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Controller
public class IndexPageController {
	@Autowired
	private MongoDao mongoDao;
	
	private URLCodec urlCodec = new URLCodec();
	
	@RequestMapping("/")
	public String index(Model model_,
		HttpServletRequest servletRequest,
		@RequestParam(value="db", required=false) String db,
		@RequestParam(value="coll", required=false) String coll,
		@RequestParam(value="offset", defaultValue="0") int offset,
		@RequestParam(value="count", defaultValue="50") int count,
		@RequestParam(value="sort", required=false) String sort,
		@RequestParam(value="desc", defaultValue="false") boolean desc
		) {
		Map<String, Object> model = new HashMap<String, Object>();
		Map<String, Object> params = urlCodec.decode(servletRequest.getQueryString());
		
		Map<String, Object> search = new HashMap<String, Object>();
		
		for (Entry<String, Object> e: params.entrySet()) {
			if (!e.getKey().startsWith("search.")) continue;
					
			Object value = e.getValue();
			//try to convert to ObjectId and Long, also treat "qwe" as string qwe
			// w/o quotes
			try {
				value = new ObjectId(value.toString());
			} catch (Exception ex) {
				try {
					value = Long.parseLong(value.toString());
				} catch (Exception ex2) {
					if (value.toString().startsWith("\"") && value.toString().endsWith("\"")) {
						value = value.toString().substring(1, value.toString().length() - 1);
					}
				}
			}
			search.put(e.getKey().substring(7), value);
		}
		model.put("search", search);
		
		
		model.put("databases", mongoDao.getDBNames());
		model.put("db", db);
		
		if (db != null) {
			List<Map<String, Object>> collections = new ArrayList<Map<String, Object>>();
			
			for (String s: mongoDao.getCollectionNames(db)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", s);
				map.put("size", mongoDao.getCollectionSize(db, s));
				collections.add(map);
			}
			model.put("collections", collections);
		}
		
		if (coll != null) {
			List<DBObject> elements = mongoDao.getCollectionElements(db, coll, offset, count, sort, desc, search);
			List<String> fields = extractFields(elements);
			
			model.put("fields", fields);
			
			model.put("sort", sort);
			model.put("desc", desc);
			
			model.put("coll", coll);
			model.put("fields", fields);
			
			List<Map<String, Object>> els = new ArrayList<Map<String,Object>>();
			
			for (DBObject e: elements) {
				Map<String, Object> result = new HashMap<String, Object>();
				for (String f: fields) {
					Map<String, Object> r = formatValue(f, e.get(f));
					result.put(f, r);
				}
				els.add(result);
			}
			model.put("elements", els);
			model.put("elementsCount", mongoDao.getCollectionSize(db, coll));
			model.put("offset", offset);
			model.put("count", count);
		}
		
		model.put("host", MongoSource.getInstance().getHost());
		model.put("port", MongoSource.getInstance().getPort());
		
		model_.asMap().putAll(model);
		return "index";
	}
		
	Map<String, Object> formatValue(String field, Object v) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (field.equals("_id")) {
			map.put("id", JSON.serialize(v));
		}
		
		if (v instanceof ObjectId) {
			ObjectId id = (ObjectId) v;
			map.put("shortValue", v.toString());
			map.put("value", id.getDate().toGMTString() + "\n" + Integer.toHexString(id.getMachine()) 
					+ "/" + Integer.toHexString(id.getInc()));
			map.put("searchable", false);
			return map;
		}
		
		if (v instanceof Collection || v instanceof Map) {
			map.put("shortValue", "{...}");
			map.put("value", new Gson().toJson(v));
			map.put("searchable", false);
			return map;
		}
		if (v == null) {
			map.put("shortValue", "null");
			map.put("value", "null");
			map.put("searchable", true);
			return map;
		}
		String s = v.toString();
		
		map.put("shortValue", s.length() <= 25 ? s : s.substring(0, 25) + "...");
		map.put("value", s);
		map.put("searchable", true);
		return map;
	}
	
	private List<String> extractFields(List<DBObject> elements) {
		Set<String> elems = new HashSet<String>();
		for (DBObject e: elements) {
			elems.addAll(((Map<String, Object>)e).keySet());
		}
		List<String> list = new ArrayList<String>(elems);
		Collections.sort(list);
		return list;
		
	}
}
