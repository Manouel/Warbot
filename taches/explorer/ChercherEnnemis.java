package pepisha.taches.explorer;

import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarExplorerBrainController;
import pepisha.taches.TacheAgent;
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
		
		ArrayList<WarPercept> ennemis = explorer.getBrain().getPerceptsEnemies();
		
		if (ennemis != null && ennemis.size() > 0) {
			WarPercept ennemi = ennemis.get(0);
			
			// On envoie le message aux rocket launcher
			explorer.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.ennemyHere,
					String.valueOf(ennemi.getDistance()), String.valueOf(ennemi.getAngle()));
		}
		else {
			explorer.getBrain().setRandomHeading(10);
		}
		
		explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
	}

	@Override
	public String toString() {
		return "Tache Chercher Ennemis";
	}
	
	
}
