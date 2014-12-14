package pepisha.taches.rocketLauncher;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarRocketLauncherBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.agents.projectiles.WarRocket;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.tools.CoordPolar;

public class AttaquerEnnemi extends TacheAgent{

	public AttaquerEnnemi(WarBrainController b) {
		super(b);
	}
	
	private void attaquer(WarPercept enemy) {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		
		if(rocket.getBrain().isReloaded()){
			rocket.getBrain().setHeading(enemy.getAngle());
			rocket.setToReturn(WarRocketLauncher.ACTION_FIRE);
		}
		else{
			rocket.getBrain().setDebugStringColor(Color.green);

			//si je suis pas trop pres de l'enemy je m'approche
			if(enemy.getDistance() > WarRocket.EXPLOSION_RADIUS + 1)
				rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
			else
				rocket.setToReturn(WarRocketLauncher.ACTION_IDLE);
		}
	}

	@Override
	public void exec() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		
		ArrayList<WarPercept> bases = rocket.getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		ArrayList<WarPercept> percept = rocket.getBrain().getPerceptsEnemies();
		
		if(bases != null && bases.size() > 0){ //Si j'ai une base dans mes percepts j'envoie message "base ici"
			rocket.getBrain().broadcastMessageToAll(Constants.enemyBaseHere, String.valueOf(bases.get(0).getDistance()), String.valueOf(bases.get(0).getAngle()));			
			
			attaquer(bases.get(0));
		}
		else if(percept != null && percept.size() > 0){ //Sinon, j'envoie message "ennemyHere"
			rocket.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.ennemyHere, String.valueOf(percept.get(0).getDistance()), String.valueOf(percept.get(0).getAngle()));
			
			attaquer(percept.get(0));
		}
		else{
			ChercherEnnemi nvTache=new ChercherEnnemi(rocket);
			rocket.setTacheCourante(nvTache);
		}
	}
		
	@Override
	public String toString() {
		return "Tache attaquer ennemi";
	}
}
