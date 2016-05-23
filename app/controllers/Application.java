package controllers;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.node.ObjectNode;

import constants.Constants;
import helper.datasources.MorphiaObject;
import helper.datasources.ObjectFactory;
import models.AutomationTeam;
import models.BasicRequest;
import models.TestCase;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import utils.Common;
import utils.HttpConnectionHellper;
import views.html.common_user_template;
import views.html.login_signup;
import views.html.alert;
import views.html.message;
import views.html.request.create_request;
import views.html.teams.create_team;
import views.html.testcase.create_test_case;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";

	public static Result index() {

		return RequestController.validResponse("Welcome Page", new Html("Welcome"));
	}

	public static Result doIndex() {
		return loginOrSignUp("NA");
	}

	public static Result loginOrSignUp(String redirectUrl) {
		session().clear();
		if (redirectUrl.equalsIgnoreCase("NA")) {
			return ok(login_signup.render(new Html("")));
		} else {
			return ok(login_signup.render(new Html("")));
		}
	}

	public static Result doLoginOrSignUp() {
		try {
			session().clear();
			Map<String, String[]> params = request().body().asFormUrlEncoded();
			String loginType = params.get("loginType")[0];
			if (loginType.equalsIgnoreCase("login")) {
				String email = params.get("email")[0];
				String password = params.get("password")[0];
				if (password.length() > 5) {
					session().put("email", email);
					return index();
				} else {
					String msg = "password should be more than 5 characters";
					return ok(login_signup.render(alert.render("alert-danger", msg)));
				}

			} else {
				String userEmail = params.get("userEmail")[0];
				String password = params.get("password")[0];
				String fname = params.get("fname")[0];
				String lname = params.get("lname")[0];
				String deptName = params.get("deptName")[0];
				if (password.length() > 5) {
					User user = new User();
					user.setEmail(userEmail);
					user.setFname(fname);
					user.setLname(lname);
					user.setDeptName(deptName);
					user.setPassword(password);
					ObjectFactory.pushUser(user);
					session().put("email", userEmail);
					return index();
				}
				else{
					String msg = "password should be more than 5 characters";
					return ok(login_signup.render(alert.render("alert-danger", msg)));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok();
	}

	public static Result logout() {
		session().clear();
		return ok(login_signup.render(new Html("")));
	}

	public static Result doHit() {
		Logger.info("in the do post");
		request().body().asFormUrlEncoded();
		BasicRequest basicRequest = new BasicRequest();
		// JsonNode json = request().body().asJson();
		try {
			ObjectNode result = Json.newObject();
			Map<String, Object> doHit = HttpConnectionHellper.doHit(basicRequest);
			result.put(Constants.STATUSCODE, (int) doHit.get(Constants.STATUSCODE));
			result.put(Constants.RESPONSEBODY, (String) doHit.get(Constants.RESPONSEBODY));

			return ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(new Html("some error"));
		}
	}

	public static Result doHit_GET(String id) {
		Logger.info("in the do get");
		BasicRequest basicRequest = MorphiaObject.datastore.get(BasicRequest.class, new ObjectId(id));
		// JsonNode json = request().body().asJson();
		try {
			ObjectNode result = Json.newObject();
			Map<String, Object> doHit = HttpConnectionHellper.doHit(basicRequest);
			result.put(Constants.STATUSCODE, (int) doHit.get(Constants.STATUSCODE));
			result.put(Constants.RESPONSEBODY, (String) doHit.get(Constants.RESPONSEBODY));

			return ok(result);
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(new Html("some error"));
		}
	}



	public static Result createDataSource() {
		Logger.info("GET >>>> /datasource/create");
		// Client.doTest();
		return ok(create_test_case.render());
	}

	public static Result doCreateDataSource() {
		Logger.info("POST >>>> /datasource/create");
		DynamicForm requestData = Form.form().bindFromRequest();

		return ok("Hello " + requestData.data());
	}

	public static Result testCalculator() {
		DynamicForm requestData = Form.form().bindFromRequest();
		TestCase tc = new TestCase();
		tc.setTcName(requestData.get("firstname"));

		return ok("Hello " + requestData.data());
	}

}