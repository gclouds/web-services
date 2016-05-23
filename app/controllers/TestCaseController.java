package controllers;

import java.util.Iterator;
import java.util.Map;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.JsonNode;

import constants.ReqType;
import helper.datasources.ObjectFactory;
import models.BasicRequest;
import models.TestCase;
import play.Logger;
import play.mvc.Result;
import views.html.request.create_chain_request;
import views.html.testcase.create_test_case;
import views.html.testcase.request_http_get;

public class TestCaseController extends AppController {

	public Result addAndGetComponent(String testcaseId, String reqId) {
		Logger.info("user email: " + session("email") + " reqId:" + reqId + " testcaseId:" + testcaseId);
		try {
			if (session("email") != null) {
				BasicRequest basicRequest = ObjectFactory.getBasicRequest(reqId);

				if (basicRequest.getReqType() == ReqType.GET) {
					basicRequest.setReqType(ReqType.POST);
					Logger.info("Empty basic request has been saved,id: " + reqId);
					return ok(request_http_get.render(reqId, testcaseId, ReqType.GET.name()));
				} else if (basicRequest.getReqType() == ReqType.POST) {
					basicRequest.setReqType(ReqType.POST);
					Logger.info("Empty basic request has been saved,id: " + reqId);
					return ok(request_http_get.render(reqId, testcaseId, ReqType.POST.name()));
				}
			}
			return internalServerError("something went wrong");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}

	public Result createTestCase() {
		Logger.info("GET >>>> /request/create");
		String title = "Create request";
		// Client.doTest();
		return validResponse(title, create_test_case.render());
	}

	public Result createChainRequest() {
		Logger.info("GET >>>> /request/create");
		String title = "Create request";
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		try {
			if (session("email") != null) {
				TestCase testCase = new TestCase();
				testCase.setId(new ObjectId().toString());
				testCase.setTcName(params.get("testname")[0]);
				testCase.setUserEmail(session("email"));
				testCase.setDescription(params.get("testDes")[0]);
				testCase.setKeywords(params.get("testKeywords")[0]);
				String pushTestCaseid = ObjectFactory.pushTestCase(testCase);
				Logger.info("test case >>>> pushTestCaseid: " + pushTestCaseid);
				return validResponse(title, create_chain_request.render(pushTestCaseid, testCase.getTcName()));
			} else
				return internalServerError("Some thing went wrong...");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError("Some thing went wrong...");
		}
	}

	public Result saveTestCase(String testcaseId) {
		Logger.info("POST >>>> /Test Case/save/" + testcaseId);
		System.out.println(request().body().asJson());
		try {
			TestCase testCase = ObjectFactory.getTestCase(testcaseId);
			Iterator<JsonNode> iterator = request().body().asJson().iterator();

			while (iterator.hasNext()) {
				BasicRequest basicRequest = FactoryMethod.getBasicRequest(iterator.next());
				testCase.addFlowReq(basicRequest);
			}
			ObjectFactory.pushTestCase(testCase);
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError();
		}
	}

	public Result editTestCase(String testcaseId) {
		Logger.info("GET >>>> /request/create");
		String title = "Create request";
		try {
			TestCase testCase = ObjectFactory.getTestCase(testcaseId);
			return validResponse(title, create_chain_request.render(testcaseId, testCase.getTcName()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError("Some thing went wrong...");
		}
	}

	public Result deleteTestCase(String testcaseId) {
		Logger.info("GET >>>> /request/delete");
		try {
			ObjectFactory.removeTestCase(testcaseId);
			return redirect("/chain-request/list");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError("Some thing went wrong...");
		}
	}

	public Result runTestCase(String testcaseId) {
		Logger.info("POST >>>> /Test Case/save/" + testcaseId);
		System.out.println(request().body().asJson());
		try {
			TestCase testCase = ObjectFactory.getTestCase(testcaseId);
			Iterator<JsonNode> iterator = request().body().asJson().iterator();

			while (iterator.hasNext()) {
				BasicRequest basicRequest = FactoryMethod.getBasicRequest(iterator.next());
				testCase.addFlowReq(basicRequest);
			}
			// ObjectFactory.pushTestCase(testCase);
			return ok();
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError();
		}
	}

}
