package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;
import views.html.common_user_template;

public class AppController extends Controller {
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
}
