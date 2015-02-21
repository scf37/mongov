package ru.scf37.mongov.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.scf37.mongov.dao.MongoDao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Controller
public class EditPageController {
	@Autowired
	private MongoDao mongoDao;
	
	@RequestMapping(value="/edit/{db}/{coll}/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object index(Model model,
		@PathVariable(value="db") String db,
		@PathVariable(value="coll") String coll,
		@PathVariable(value="id") String id
		) {
		DBObject obj = mongoDao.get(db, coll, JSON.parse(id));
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		
		return g.toJson(g.fromJson(JSON.serialize(obj), Map.class));
	}
		
	@RequestMapping(value="/delete/{db}/{coll}/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String delete(Model model,
		@PathVariable(value="db") String db,
		@PathVariable(value="coll") String coll,
		@PathVariable(value="id") String id
		) {
		mongoDao.delete(db, coll, JSON.parse(id));
		return null;
	}
		
	
	@RequestMapping(value="/edit/{db}/{coll}/", method=RequestMethod.POST)
	@ResponseBody
	public String save(@RequestBody String json, 
		@PathVariable(value="db") String db,
		@PathVariable(value="coll") String coll
		) {
		DBObject obj = (DBObject) JSON.parse(json);
		mongoDao.save(db, coll, obj);
		return null;
	}
}
