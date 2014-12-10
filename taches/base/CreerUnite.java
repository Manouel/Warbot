package pepisha.taches.base;

import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarBaseBrainController;
import pepisha.taches.TacheAgent;

public class CreerUnite extends TacheAgent{
	
	
	// Energie minimum pour créer un nouvel agent
	private static final int MIN_HEATH_TO_CREATE = (int) (WarBase.MAX_HEALTH * 0.8);
	
	
	public CreerUnite(WarBrainController b){
		super((WarBaseBrainController)b);
	}

	@Override
	public void exec() {
		WarBaseBrainController base=(WarBaseBrainController)typeAgent;
		if(base.getBrain().getHealth()>MIN_HEATH_TO_CREATE)
		{
			if(base.getNbRocketLauncher()<base.getNbMinRocket()){
				base.getBrain().setNextAgentToCreate(WarAgentType.WarRocketLauncher);
			}
			else if(base.getNbEngineer()<base.getNbMinEngineer()){
				base.getBrain().setNextAgentToCreate(WarAgentType.WarEngineer);
			}
			else if(base.getNbExplorer()<base.getNbMinExplorer()){
				base.getBrain().setNextAgentToCreate(WarAgentType.WarExplorer);

			}
			else{ //Sinon, par défaut on fait des rocketLaunchers
				base.getBrain().setNextAgentToCreate(WarAgentType.WarRocketLauncher);
				base.getBrain().setDebugString("Création d'un Rocket Launcher");
			}
			base.setToReturn(WarBase.ACTION_CREATE);
		}
	}

	@Override
	public String toString() {
		return "Tache Creer Unite";
	}
	
	
}
