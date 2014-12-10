package pepisha.taches.base;

import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarBaseBrainController;
import pepisha.taches.TacheAgent;

public class CreerUnite extends TacheAgent{
	
	public CreerUnite(WarBrainController b){
		super((WarBaseBrainController)b);
	}

	@Override
	public void exec() {
		WarBaseBrainController base=(WarBaseBrainController)typeAgent;
		if(base.getBrain().getHealth()>base.getMIN_HEATH_TO_CREATE())
		{
			if(base.getNbRocketLauncher()<base.getNbMinRocket()){
				base.getBrain().setNextAgentToCreate(WarAgentType.WarRocketLauncher);
				base.getBrain().setDebugString("Création d'un Rocket Launcher");
			}
			else if(base.getNbExplorer()<base.getNbMinExplorer()){
				base.getBrain().setNextAgentToCreate(WarAgentType.WarExplorer);
				base.getBrain().setDebugString("Création d'un Explorer");

			}
			else{ //Sinon, par défaut on fait des rocketLaunchers
				
			}
			base.setToReturn(WarBase.ACTION_CREATE);
		}
	}

	@Override
	public String toString() {
		return "Tache Creer Unite";
	}
	
	
}
