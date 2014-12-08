package pepisha.taches;

import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarExplorerBrainController;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class ChercherEnnemis extends TacheAgent {

	public ChercherEnnemis(WarBrainController b){
		super(b);
	}

	@Override
	public void exec() {		
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		ArrayList<WarPercept> basesEnnemies = explorer.getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		
		if (basesEnnemies != null && basesEnnemies.size() > 0) {
			
			// On envoie aux bases la position de la base ennemie
			explorer.getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.enemyBaseHere, (String[]) null);
			explorer.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.enemyBaseHere, (String[]) null);
		}
		else {
			explorer.getBrain().setRandomHeading(40);
		}
		
		explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
	}

	@Override
	public String toString() {
		return "Tache Chercher Ennemis";
	}
	
	
}
