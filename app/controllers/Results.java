package controllers;

import helper.datasources.ObjectFactory;
import models.ExecutionUnit;
import models.VisualReport;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.common_user_template;
import views.html.result.visual_result;

public class Results extends Controller {

	public Result visualReport(String executionId) {
		try {
			Logger.info("GET >>>> /visualReport");
			String title = "Result page";
			ExecutionUnit execution = ObjectFactory.getExecution(executionId);
			VisualReport report = new VisualReport(execution);
			return RequestController.validResponse(title, visual_result.render(report));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RequestController.errorResponse("error page",new Html(e.getMessage()));
		}
	}
}
