package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.JsonNode;

import constants.AccessLevel;
import constants.ReqType;
import helper.datasources.ObjectFactory;
import models.BasicRequest;
import models.TestCase;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import util.SearchLogic;
import utils.Common;
import views.html.result.unit_result;


public class AppServices extends Controller {
	
	
	public Result getBasicReqByUser() {
		Logger.info("user email: "+session("email"));
		try {
			if(session("email")!=null){
				List<BasicRequest> basicRequestByUser = ObjectFactory.getBasicRequestByUser(session("email"));
				return ok(Common.toJson(basicRequestByUser));	
			}
			else return ok(Common.toJson(new HashMap<String,String>()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RequestController.errorResponse("error", new Html(e.getMessage()));
		}
	}
	
	public Result getTestCaseReqByUser() {
		Logger.info("user email: "+session("email"));
		try {
			if(session("email")!=null){
				List<TestCase> list = ObjectFactory.getTestCaseByUser((session("email")));
				return ok(Common.toJson(list));	
			}
			else return ok(Common.toJson(new HashMap<String,String>()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RequestController.errorResponse("error", new Html(e.getMessage()));
		}
	}
	
	public Result getTestCaseById(String testcaseId) {
		Logger.info("user email: "+session("email"));
		try {
			TestCase testcase = ObjectFactory.getTestCase(testcaseId);
			return ok(Common.toJson(testcase));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RequestController.errorResponse("error", new Html(e.getMessage()));
		}
	}
	
	
	public Result doChange(String typeOfRequest,String id){
		String redierctURL="";
		if(typeOfRequest.equalsIgnoreCase("schedule")){
			redierctURL="/execute/"+id;
		}
		
		
		return ok(redierctURL);
	}
	
	
	public Result searchResult(){
		Logger.info("searchResult() ");
		//Map<String,String> output = new HashMap<String, String>();
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		JsonNode json = request().body().asJson();
		System.out.println(json);

		List<BasicRequest> search = SearchLogic.search(json);
		for(BasicRequest basic:search){
			Logger.info("adding object to result: "+basic.getId());
			//output.put(basic.getId(), unit_result.render(basic).toString());
		}
		
		search.get(0).getEnvName();
		return ok(Common.toJson(search));
	}
	
	public Result getBasicRequest(String id){
		Logger.info("getBasicRequest(String id): "+id);
		try {
			BasicRequest basicRequest=new BasicRequest();
			basicRequest.setAccessLevel(AccessLevel.PRIVATE);
			basicRequest.setId((new ObjectId()).toString());
			basicRequest.setUserEmail(session("email"));
			if(id.equalsIgnoreCase("HTTP_GET")){
				basicRequest.setReqType(ReqType.GET);
				ObjectFactory.pushBasicRequest(basicRequest);
			}else if(id.equalsIgnoreCase("HTTP_POST")){
				basicRequest.setReqType(ReqType.POST);
				ObjectFactory.pushBasicRequest(basicRequest);
			}else{
				return ok(Common.toJson(ObjectFactory.getBasicRequest(id)));
			}
			return ok(Common.toJson(ObjectFactory.getBasicRequest(basicRequest.getId())));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}
}
