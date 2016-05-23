package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.JsonNode;

import constants.Constants;
import helper.datasources.MorphiaObject;
import models.BasicRequest;

public class SearchLogic {

	public static Map<String,BasicRequest> searchcache;
	
	public static List<BasicRequest> search(JsonNode json){
		Query<BasicRequest> createQuery = MorphiaObject.datastore.createQuery(BasicRequest.class);
		if(json.has("search-email")){
			String userEmail=json.get("search-email").textValue();
			createQuery.field("userEmail").containsIgnoreCase(userEmail);
		}
		if(json.has("search-keywords")){
			String keywords=json.get("search-keywords").textValue();
			createQuery.field("keywords").containsIgnoreCase(keywords);
		}
		if(json.has("search-description")){
			String description=json.get("search-description").textValue();
			createQuery.field("description").containsIgnoreCase(description);
		}
		if(json.has("search-get")){
			String reqType=json.get("search-get").textValue();
			createQuery.field("reqType").containsIgnoreCase(reqType);
		}
		if(json.has("search-post")){
			String reqType=json.get("search-post").textValue();
			createQuery.field("reqType").containsIgnoreCase(reqType);
		}
		
		try {
			List<BasicRequest> basicRequestByUser = createQuery.limit(10).asList();
			return basicRequestByUser;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}

	public static void buildCache(){
		searchcache=new HashMap<String,BasicRequest>();
		
		List<BasicRequest> list = MorphiaObject.datastore.find(BasicRequest.class).asList();
		for(BasicRequest unit:list){
			String searchStr=unit.getReqType()+Constants.DELIMITER+unit.getUserEmail()+Constants.DELIMITER;
			searchcache.put("", unit);
			
		}
	}

}
