package controllers;

import java.util.List;

import org.bson.types.ObjectId;

import helper.datasources.ObjectFactory;
import main.TestExecutor;
import models.BasicRequest;
import models.ExecutionUnit;
import models.TestCase;
import models.VisualReport;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.HttpConnectionHellper;
import views.html.common_user_template;
import views.html.result.visual_result;

public class Executor extends Controller{
	public Result execute(String testcaseId) throws Exception {
		Logger.info("execute >>>> /execute/"+testcaseId);
		
		TestCase testCase = ObjectFactory.getTestCase(testcaseId);
		
		for (BasicRequest basicRequest : testCase.getFlowList()) {
			
			HttpConnectionHellper.doHit(basicRequest);
			
			
		} 
		
		// Client.doTest();
		return ok();
	}
	
	public Result push(String testcaseId) throws Exception {
		Logger.info("push >>>> /execute/"+testcaseId);
				
		TestCase testCase = ObjectFactory.getTestCase(testcaseId);
		
		if(testCase!=null){
			ExecutionUnit executionUnit= new ExecutionUnit();
			executionUnit.setTestCaseId(testCase.getId());
			try {
				List<BasicRequest> flowList = testCase.getFlowList();
				for (BasicRequest basicRequest : flowList) {
					executionUnit.getSteps().add(basicRequest);
				}
			executionUnit.setStatus(constants.Status.PENDING);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ObjectFactory.pushExecution(executionUnit);
			return redirect("/visual-report/"+executionUnit.getId().toString());

		}
		else{
		Logger.info("no test case found >>>> "+testcaseId);
		return internalServerError();

		}

	}	
	
	public Result test(String testcaseId) throws Exception {
		Logger.info("push >>>> /execute/"+testcaseId);
				
		TestCase testCase = ObjectFactory.getTestCase(testcaseId);
		
		if(testCase!=null){
			ExecutionUnit executionUnit= new ExecutionUnit();
			executionUnit.setTestCaseId(testCase.getId());
			try {
				List<BasicRequest> flowList = testCase.getFlowList();
				for (BasicRequest basicRequest : flowList) {
					executionUnit.getSteps().add(basicRequest);
				}
			executionUnit.setStatus(constants.Status.PENDING);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			TestExecutor.test(executionUnit);
			VisualReport report = new VisualReport(executionUnit);
			return ok(visual_result.render(report));
		}
		else{
		Logger.info("no test case found >>>> "+testcaseId);
		return internalServerError();

		}

	}
}
