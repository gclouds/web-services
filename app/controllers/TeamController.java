package controllers;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.WriteResult;

import helper.datasources.MorphiaObject;
import helper.datasources.ObjectFactory;
import models.AutomationTeam;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.common_user_template;
import views.html.message;
import views.html.teams.create_team;
import views.html.teams.list_teams;


public class TeamController extends Controller{

	public static Result createTeam() {
		Logger.info("GET >>>> /team/create");
		String title = "Create request";
		// Client.doTest();
		return ok(common_user_template.render(title, create_team.render(),"gaurav"));
	}

	public static Result getTeamList() {
		Logger.info("GET >>>> /team/list");
		ObjectNode result = Json.newObject();

		Query<AutomationTeam> equal = MorphiaObject.datastore.createQuery(AutomationTeam.class).field("active")
				.equal(true);
		List<AutomationTeam> asList = equal.asList();
		ObjectNode data = Json.newObject();
		for (AutomationTeam automationTeam : asList) {
			data.put(automationTeam.getId(), automationTeam.name+"-"+automationTeam.getEmail());
			result.set("data", data);
		}
		result.put("size", asList.size() + "");
		return ok(result);
	}
	public static Result doCreateTeam() {
		Logger.info("POST >>>> /team/create");
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		AutomationTeam automationTeam = new AutomationTeam(params);
		try {
			ObjectFactory.pushTeam(automationTeam);
			return ok(common_user_template.render("Successfully stored in DB...",
					message.render("Successfully stored in DB..."),"gaurav"));
		} catch (Exception e) {
			return internalServerError(common_user_template.render("Something went wrong...",
					message.render("Something went wrong.../n" + e.getMessage()),"gaurav"));
		}
	}
	
	
	public static Result viewAllTeam(){
		Logger.info("GET >>>> /team/view All");

		Query<AutomationTeam> equal = MorphiaObject.datastore.createQuery(AutomationTeam.class);
		List<AutomationTeam> asList = equal.asList();
		return ok(common_user_template.render("List of all request",list_teams.render(asList),"gaurav"));
	}
	
	public static Result deleteTeam(String id){
		Logger.info("GET >>>> /team/delete/"+id);
		try {
			Query<AutomationTeam> delete = MorphiaObject.datastore.createQuery(AutomationTeam.class).field(Mapper.ID_KEY).equal(new ObjectId(id));
			WriteResult writeResult = MorphiaObject.datastore.delete(delete);
			Logger.info(writeResult.toString());
			if(writeResult.getN()>0){
				return ok("successfully deleted team: "+id);
			}
			else{
				return ok("not records found for the team: "+id);
			}
			
		} catch (Exception e) {
			return internalServerError(e.getMessage());
		}
	}
	
}
