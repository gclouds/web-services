

import helper.datasources.MongoDB;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;

public class Global extends GlobalSettings {



	public void onStart(Application app) {
        Logger.info("Application started!");
        String mongoUri = Play.application().configuration().getString("mongodb.uri");
        MongoDB.connect(mongoUri);
        Logger.info("Connected to Database!");

   

    }

    public void onStrop(Application app) {
        Logger.info("Appplication stopped!");
        MongoDB.disconnect();
    }

    // Update to add a role

   
}
