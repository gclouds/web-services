package controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mongodb.WriteResult;

import constants.AccessLevel;
import constants.Constants;
import constants.ReqType;
import constants.ResponseType;
import play.twirl.api.Html;
import utils.Common;
import utils.HttpConnectionHellper;
import helper.datasources.MorphiaObject;
import helper.datasources.ObjectFactory;
import models.BasicRequest;
import models.TestCase;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.common_user_template;
import views.html.message;
import views.html.login_signup;
import views.html.request.create_request;
import views.html.request.create_chain_request;
import views.html.request.list_request;
import views.html.request.add_validation;
import views.html.request.request_http_get;
import views.html.request.request_http_post;
import views.html.request.create_test_case;
import views.html.request.request_http_empty;
import views.html.request.add_components;
import views.html.request.list_components;
import views.html.request.list_testcase;
import views.html.request.list_testcase_execute;

public class RequestController extends Controller {

	public Result createRequest() {
		Logger.info("GET >>>> /request/create");
		String title = "Create request";
		// Client.doTest();
		return ok(common_user_template.render(title, create_request.render(), "gaurav"));
	}



	public Result createComponent() {
		Logger.info("GET >>>> createComponent");
		String title = "Create component";
		// Client.doTest();
		return validResponse(title, add_components.render());
	}



	public Result getUnitRequest(String typeOfReq, String testcaseId) {
		Logger.info("GET >>>> /request/create:" + typeOfReq);
		String id = "";
		BasicRequest basicRequest = new BasicRequest();
		basicRequest.setAccessLevel(AccessLevel.PRIVATE);
		basicRequest.setId((new ObjectId()).toString());
		basicRequest.setUserEmail(session("email"));
		try {
			if (typeOfReq.equalsIgnoreCase("HTTP_GET")) {
				basicRequest.setReqType(ReqType.GET);
				id = ObjectFactory.pushBasicRequest(basicRequest);
				Logger.info("Empty basic request has been saved,id: " + id);
				return ok(request_http_get.render(id, testcaseId));
			} else if (typeOfReq.equalsIgnoreCase("HTTP_POST")) {
				basicRequest.setReqType(ReqType.POST);
				id = ObjectFactory.pushBasicRequest(basicRequest);
				Logger.info("Empty basic request has been saved,id: " + id);
				return ok(request_http_post.render(id, testcaseId));
			} else if (typeOfReq.equalsIgnoreCase("HTTP_EMPT")) {
				basicRequest.setReqType(ReqType.POST);
				id = ObjectFactory.pushBasicRequest(basicRequest);
				Logger.info("Empty basic request has been saved,id: " + id);
				return ok(request_http_empty.render(id, testcaseId));
			} else if (typeOfReq.equalsIgnoreCase(ReqType.WAIT.name())) {
				basicRequest.setReqType(ReqType.POST);
				id = ObjectFactory.pushBasicRequest(basicRequest);
				Logger.info("Empty basic request has been saved,id: " + id);
				return ok(request_http_post.render(id, testcaseId));
			} else {
				return internalServerError();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return errorResponse("error page", message.render("Something went wrong.../n" + e.getMessage()));
		}

	}

	public Result removeUnitRequest(String testcaseId, String reqId) {
		Logger.info("removeUnitRequest:" + reqId);
		try {
			ObjectFactory.removeBasicRequest(reqId);
			TestCase testCase = ObjectFactory.getTestCase(testcaseId);
			System.out.println(testCase.getFlowList().size() + " : ");
			testCase.getFlowList().remove(new ObjectId(reqId));
			System.out.println(testCase.getFlowList().size());
			ObjectFactory.pushTestCase(testCase);
			Logger.info("Removing BasicRequest: " + reqId + " >> " + reqId);
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return errorResponse("error page", message.render("Something went wrong.../n" + e.getMessage()));
		}

	}

	public Result removeComponent(String reqId) {
		Logger.info("removeUnitRequest:" + reqId);
		try {
			ObjectFactory.removeBasicRequest(reqId);
			Logger.info("Removing BasicRequest: " + reqId + " >> " + reqId);
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return errorResponse("error page", message.render("Something went wrong.../n" + e.getMessage()));
		}

	}

	public Result testUnitRequest(String typeOfReq) {
		Logger.info("POST >>>> /request/test:" + typeOfReq);
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		BasicRequest basicRequest = new BasicRequest();
		ObjectNode result = Json.newObject();
		System.err.println(params);
		String errorMsg = "";
		try {
			if (Common.isNotNull(params.get("requestEndPoint"))) {
				basicRequest.setEndPoint(params.get("requestEndPoint")[0]);
			} else {
				errorMsg = "please specify requested End Point!!!";
			}
			basicRequest.setReqHeader(getHeaders(params));
			basicRequest.setReqParams(getUrlParams(params));
			basicRequest.setFormParams(getFormParams(params));
			basicRequest.setJsonExtractorParams(getJsonExtractors(params));
			System.out.println("condition for body: " + Common.isNotNull(params.get("jsonBody"))
					+ Common.isNotNull(params.get("paramKey")) + Common.isNotNull(params.get("paramValue")));

			if (Common.isNotNull(params.get("jsonBody"))
					|| (Common.isNotNull(params.get("paramKey")) && Common.isNotNull(params.get("paramValue")))) {
				if (Common.isNotNull(params.get("jsonBody"))) {
					basicRequest.setReqBody(params.get("jsonBody")[0]);
				} else {
					basicRequest.setReqBody("");
				}
			} else {
				errorMsg = "please specify requested body or form parameter!!!";
			}
			if (typeOfReq.equalsIgnoreCase(ReqType.GET.name())) {
				basicRequest.setReqType(ReqType.GET);
			} else if (typeOfReq.equalsIgnoreCase(ReqType.POST.name())) {
				basicRequest.setReqType(ReqType.POST);
			}
			result = Json.newObject();
			Map<String, Object> doHit = HttpConnectionHellper.doHit(basicRequest);
			result.put(Constants.STATUSCODE, (int) doHit.get(Constants.STATUSCODE));
			result.put(Constants.RESPONSEBODY, (String) doHit.get(Constants.RESPONSEBODY));

			return ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errormsg", errorMsg);
			return internalServerError(errorMsg);
		}

	}

	public Result saveUnitRequest() {
		Logger.info("POST >>>> /request/save");
		JsonNode jsonBody =  request().body().asJson().get(0);
		System.out.println(jsonBody);
		try {
			BasicRequest basicRequest = FactoryMethod.getBasicRequest(jsonBody);
			ObjectFactory.pushBasicRequest(basicRequest);
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError();
		}
	}
	


	private Map<String, String> getJsonParams(Map<String, String[]> params) {
		// get request parameters
		String keys[] = HttpConnectionHellper.getStringArray(params.get("resJsonPropVal"));
		String values[] = HttpConnectionHellper.getStringArray(params.get("resJsonPropVarName"));
		Logger.info(keys.length + " >>>paramKey");
		Logger.info(values.length + " >>>paramValue");

		Map<String, String> arugParams = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			arugParams.put(keys[i], values[i]);
		}
		return arugParams;
	}

	public Result doCreateRequest() {
		Logger.info("POST >>>> /request/create");
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		BasicRequest basicRequest = new BasicRequest();

		try {
			ObjectFactory.pushBasicRequest(basicRequest);
			return validResponse("Successfully stored in DB...", message.render("Successfully stored in DB..."));
		} catch (Exception e) {
			return errorResponse("Something went wrong...",
					message.render("Something went wrong.../n" + e.getMessage()));
		}
	}

	public Result viewAllRequest() {
		Logger.info("GET >>>> /team/view All");

		Query<BasicRequest> equal = MorphiaObject.datastore.createQuery(BasicRequest.class);
		List<BasicRequest> asList = equal.asList();

		return validResponse("List of all request", list_request.render(asList));
	}

	public Result getReqList() {
		Logger.info("getReqList/view All/" + session("email"));
		return validResponse("component list page", list_components.render());
	}

	public Result getTestCaseList() {
		Logger.info("getReqList/view All/" + session("email"));
		return validResponse("component list page", list_testcase.render());
	}

	public Result getTestCaseListExecute() {
		Logger.info("getReqList/view All/" + session("email"));
		return validResponse("component list page", list_testcase_execute.render());
	}

	public Result deleteRequest(String id) {
		Logger.info("GET >>>> /request/delete/" + id);
		try {
			Query<BasicRequest> delete = MorphiaObject.datastore.createQuery(BasicRequest.class).field(Mapper.ID_KEY)
					.equal(new ObjectId(id));
			WriteResult writeResult = MorphiaObject.datastore.delete(delete);
			Logger.info(writeResult.toString());
			if (writeResult.getN() > 0) {
				return ok("successfully deleted team: " + id);
			} else {
				return ok("not records found for the team: " + id);
			}

		} catch (Exception e) {
			return internalServerError(e.getMessage());
		}
	}

	public Result addValidation() {
		Logger.info("GET >>>> /request/create");
		String title = "Add validation";
		// Client.doTest();
		return validResponse(title, add_validation.render());
	}

	public static Result validResponse(String title, Html page) {
		String userEmail = session("email");
		if (userEmail != null) {
			return ok(common_user_template.render(title, page, userEmail));
		} else {
			return Application.loginOrSignUp(request().path());
		}
	}

	public static Result errorResponse(String title, Html page) {
		String userEmail = session("email");

		if (userEmail != null) {
			return ok(common_user_template.render(title, page, userEmail));
		} else {
			return Application.loginOrSignUp(request().path());
		}
	}

	public static Map<String, String> getHeaders(Map<String, String[]> requestData) {
		// header parameters
		String headerKeys[] = HttpConnectionHellper.getStringArray(requestData.get("headerKey"));
		String headerValues[] = HttpConnectionHellper.getStringArray(requestData.get("headerValue"));
		Logger.info(headerKeys.length + " >>>headerKey");
		Logger.info(headerValues.length + " >>>headerValue");
		Map<String, String> arugHeader = new HashMap<>();
		for (int i = 0; i < headerKeys.length; i++) {
			arugHeader.put(headerKeys[i], headerValues[i]);
		}
		return arugHeader;
	}

	public static Map<String, String> getUrlParams(Map<String, String[]> requestData) {
		// get request parameters
		String keys[] = HttpConnectionHellper.getStringArray(requestData.get("urlParamKey"));
		String values[] = HttpConnectionHellper.getStringArray(requestData.get("urlParamValue"));
		Logger.info(keys.length + " >>>urlParamKey");
		Logger.info(values.length + " >>>urlParamValue");

		Map<String, String> arugParams = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			arugParams.put(keys[i], values[i]);
		}
		return arugParams;
	}

	public static Map<String, String> getFormParams(Map<String, String[]> requestData) {
		// get request parameters
		String keys[] = HttpConnectionHellper.getStringArray(requestData.get("paramKey"));
		String values[] = HttpConnectionHellper.getStringArray(requestData.get("paramValue"));
		Logger.info(keys.length + " >>>paramKey");
		Logger.info(values.length + " >>>paramValue");

		Map<String, String> arugParams = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			arugParams.put(keys[i], values[i]);
		}
		return arugParams;
	}

	public static Map<String, String> getJsonExtractors(Map<String, String[]> requestData) {
		// get request parameters
		String keys[] = HttpConnectionHellper.getStringArray(requestData.get("resJsonPropVal"));
		String values[] = HttpConnectionHellper.getStringArray(requestData.get("resJsonPropVarName"));
		Logger.info(keys.length + " >>>paramKey");
		Logger.info(values.length + " >>>paramValue");

		Map<String, String> arugParams = new HashMap<>();
		for (int i = 0; i < keys.length; i++) {
			arugParams.put(keys[i], values[i]);
		}
		return arugParams;
	}

	String getNotNull(String str) {
		if (Common.isNotNull(str)) {
			return str;
		} else
			return "";
	}

	public Result getUIConfigJson() {
		try {
			File file = Play.application().getFile("public/sample/sample.json");
			String string = Files.toString(file, Charsets.UTF_8);
			return ok(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError();
		}
	}
}
