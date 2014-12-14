package pepisha.taches.explorer;

import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarExplorerBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.tools.CoordPolar;

public class ChercherEnnemis extends TacheAgent {

	public ChercherEnnemis(WarBrainController b){
		super(b);
	}
	
	public WarMessage getEnemyBaseMessages() {
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		for (WarMessage m : explorer.getListeMessages()) {
			if (m.getMessage().equals(Constants.enemyBaseHere)) {
				return m;
			}
		}
		
		return null;
	}
	
	public WarPercept getExplorerEnemyPercept() {
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		ArrayList<WarPercept> explorerEnemies =  explorer.getBrain().getPerceptsEnemiesByType(WarAgentType.WarExplorer);

		if (explorerEnemies != null && explorerEnemies.size() > 0) {
			return explorerEnemies.get(0);
		}
		
		return null;
	}

	@Override
	public void exec() {		
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		// On regarde si on voit la base ennemie dans les réflèxes
		// et on passe en localiser base
		
		// Si on reçoit le message base ennemie
		WarMessage m = getEnemyBaseMessages();
		if (m != null) {
			
			CoordPolar p = explorer.getBrain().getIndirectPositionOfAgentWithMessage(m);
			
			// On se dirige vers la base
			explorer.setTacheCourante(new SeDirigerBaseEnnemie(explorer));
			explorer.setDistance(p.getDistance() - WarExplorer.DISTANCE_OF_VIEW);
			explorer.getBrain().setHeading(p.getAngle());
		}
		else {
			ArrayList<WarPercept> ennemis = explorer.getBrain().getPerceptsEnemies();
			
			if (ennemis != null && ennemis.size() > 0) {
				
				// On envoie le message aux rocket launcher
				explorer.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.ennemyHere,
						String.valueOf(ennemis.get(0).getDistance()), String.valueOf(ennemis.get(0).getAngle()));
				
				// Si on voit un explorer ennemi, on le suit
				WarPercept ennemi = getExplorerEnemyPercept();
				if (ennemi != null) {
					explorer.getBrain().setHeading(ennemi.getAngle());
				} else {
					explorer.getBrain().setRandomHeading(10);
				}
			}
			else {
				explorer.getBrain().setRandomHeading(10);
			}
		}
		
		explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
	}

	@Override
	public String toString() {
		return "Tache Chercher Ennemis";
	}
	
	
}
