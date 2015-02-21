package ru.scf37.mongov.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bson.BSON;
import org.springframework.stereotype.Repository;

import ru.scf37.mongov.exception.NotConnectedException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Repository
public class MongoDao {
	private Mongo getMongo() {
		if (MongoSource.getInstance().getMongo() == null) {
			throw new NotConnectedException();
		}
		return MongoSource.getInstance().getMongo();
	}
	
	public int setMongoParams;
	
	
	public List<String> getDBNames() {
		return getMongo().getDatabaseNames();
	}
	
	public List<String> getCollectionNames(String db) {
		List<String> result = new ArrayList<String>(getMongo().getDB(db).getCollectionNames());
		Collections.sort(result);
		return result;
	}
	
	public long getCollectionSize(String db, String collection) {
		return getMongo().getDB(db).getCollection(collection).getCount();
	}
	
	public DBObject get(String db, String collection, Object id) {
		return getMongo().getDB(db).getCollection(collection).findOne(new BasicDBObject("_id", id));
	}
	
	public void save(String db, String collection, DBObject o) {
		getMongo().getDB(db).getCollection(collection).save(o);
	}
	
	public void delete(String db, String collection, Object id) {
		getMongo().getDB(db).getCollection(collection).remove(new BasicDBObject("_id", id));
	}
	
	public List<DBObject> getCollectionElements(String db, String collection, int offset, int count, String sort, boolean desc, Map<String, Object> search) {
		DBCollection coll = getMongo().getDB(db).getCollection(collection);
		
		DBObject q = new BasicDBObject();
		
		for (Map.Entry<String, Object> entry: search.entrySet()) {
			if (entry.getValue() instanceof String) {
				q.put(entry.getKey(), Pattern.compile(".*" + 
					Pattern.quote(entry.getValue().toString()) + ".*", BSON.regexFlags("i")));
			} else {
				q.put(entry.getKey(), entry.getValue());
			}
		}
		
		DBCursor cursor = coll.find(q);
		
		if (sort != null) {
			cursor.sort(new BasicDBObject(sort, desc ? -1 : 1));
		}
		
		cursor.skip(offset);
		
		List<DBObject> result = new ArrayList<DBObject>();
				
		for (int i = 0 ; i < count ; i++) {
			if (!cursor.hasNext()) break;
			result.add(cursor.next());	
		}
		return result;
		
	}
}
