package ru.scf37.mongov.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.scf37.mongov.dao.MongoDao;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Controller
public class EditPageController {
	@Autowired
	private MongoDao mongoDao;
	
	@RequestMapping(value="/edit/{db}/{coll}/{id}", method = RequestMethod.GET)
	public void index(Model model,
		@PathVariable(value="db") String db,
		@PathVariable(value="coll") String coll,
		@PathVariable(value="id") String id, 
		HttpServletResponse response
		) throws IOException {
		DBObject obj = mongoDao.get(db, coll, JSON.parse(id));
		
		response.setContentType("application/json");
		response.getWriter().write(JSON.serialize(obj));
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
