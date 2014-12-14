package pepisha.taches.rocketLauncher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pepisha.Constants;
import pepisha.WarRocketLauncherBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.tools.CoordPolar;


public class ChercherEnnemi extends TacheAgent{

	
	public ChercherEnnemi(WarBrainController b){
		super(b);
	}

	@Override
	public void exec() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		
		//Les bases ont déjà été vérifiées dans les réflèxes
		//Si on est dans cette tâche, pas de base à attaquer ou à défendre
		
		//On regarde si il y a un ennemi dans le champ de vision. Dans ce cas, on change de tache
		ArrayList<WarPercept> percept = rocket.getBrain().getPerceptsEnemies();
		if(percept != null && percept.size() > 0){
			//je le dit aux autres
			rocket.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.ennemyHere, String.valueOf(percept.get(0).getDistance()), String.valueOf(percept.get(0).getAngle()));
			rocket.getBrain().setHeading(percept.get(0).getAngle());
			
			AttaquerEnnemi nvTache=new AttaquerEnnemi(rocket);
			rocket.setTacheCourante(nvTache);
		}
		else{	
			//Je regarde si y a quelqu'un qui m'a envoyé un mess comme quoi il a trouvé un ennemi
			WarMessage m = getFormatedMessageAboutEnemyTankToKill();
			if(m != null){
				CoordPolar p = rocket.getBrain().getIndirectPositionOfAgentWithMessage(m);
				rocket.setDistancePointOuAller(p.getDistance()-WarRocketLauncher.DISTANCE_OF_VIEW);
				rocket.setSeDirigerVersUnPoint(true);
				rocket.getBrain().setHeading(p.getAngle());
				SeDirigerVers nvTache=new SeDirigerVers(rocket);
				rocket.setTacheCourante(nvTache);
			}
			else{		// Sinon on cherche
				double angle = rocket.getBrain().getHeading() + new Random().nextInt(10) - new Random().nextInt(10);
				
				rocket.getBrain().setHeading(angle);
			}
		}
		
		rocket.setToReturn(MovableWarAgent.ACTION_MOVE);
	}

	@Override
	public String toString() {
		return "Tache chercher ennemi";
	}
	
	private WarMessage getFormatedMessageAboutEnemyTankToKill() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		for (WarMessage m : rocket.getMessages()) {
			if(m.getMessage().equals(Constants.ennemyHere) && m.getContent() != null && m.getContent().length == 2 && m.getDistance()<=Constants.rayonAttaqueBaseEnnemie){
				return m;
			}
		}
		return null;
	}
}
