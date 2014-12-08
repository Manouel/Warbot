package pepisha.taches;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarRocketLauncherBrainController;
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

	@Override
	public void exec() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		rocket.getBrain().setDebugStringColor(Color.blue);
		rocket.getBrain().setDebugString("Ennemi en vue ! ");
		
		//ArrayList<WarPercept> percept = rocket.getBrain().getPerceptsEnemiesByType(WarAgentType.WarRocketLauncher);
		ArrayList<WarPercept> percept = rocket.getBrain().getPerceptsEnemies();
		// Je un agentType dans le percept
		if(percept != null && percept.size() > 0){
			rocket.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.ennemyHere, String.valueOf(percept.get(0).getDistance()), String.valueOf(percept.get(0).getAngle()));
			
			//rocket.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.ennemyHere);
			
			if(rocket.getBrain().isReloaded()){
				
				rocket.getBrain().setHeading(percept.get(0).getAngle());
				rocket.setToReturn(WarRocketLauncher.ACTION_FIRE);
			}else{
				//si je suis pas trop pres de l'enemy je m'approche
				
				if(percept.get(0).getDistance() > WarRocket.EXPLOSION_RADIUS + 1)
					rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
				else
					rocket.setToReturn(WarRocketLauncher.ACTION_IDLE);
			}
		}else{
			//si j'ai un message me disant qu'il y a  un autre tank a tuer
			WarMessage m = getFormatedMessageAboutEnemyTankToKill();
			if(m != null){
				CoordPolar p = rocket.getBrain().getIndirectPositionOfAgentWithMessage(m);
				rocket.setDistancePointOuAller(p.getDistance());
				rocket.setSeDirigerVersUnPoint(true);
				rocket.getBrain().setHeading(p.getAngle());
				rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
				SeDirigerVers nvTache=new SeDirigerVers(rocket);
				rocket.setTacheCourante(nvTache);
				
			}
			else{//On change de tache courante, on se met à chercher des ennemis
				
				ChercherEnnemi nvTache=new ChercherEnnemi(rocket);
				rocket.setTacheCourante(nvTache);
			}
		}		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private WarMessage getFormatedMessageAboutEnemyTankToKill() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		for (WarMessage m : rocket.getMessages()) {
			if(m.getMessage().equals(Constants.ennemyHere) && m.getContent() != null && m.getContent().length == 2){
				return m;
			}
		}
		return null;
	}

}
