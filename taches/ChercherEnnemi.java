package pepisha.taches;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pepisha.Constants;
import pepisha.WarRocketLauncherBrainController;
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
		
		//On regarde si il y a un ennemi dans le champ de vision. Dans ce cas, on change de tache
		ArrayList<WarPercept> percept = rocket.getBrain().getPerceptsEnemies();
		if(percept != null && percept.size() > 0){
			//je le dit aux autres
			rocket.getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.enemyTankHere, String.valueOf(percept.get(0).getDistance()), String.valueOf(percept.get(0).getAngle()));
			
			rocket.getBrain().setDebugStringColor(Color.blue);
			rocket.getBrain().setDebugString("Ennemi en vue ! ");
			AttaquerEnnemi nvTache=new AttaquerEnnemi(rocket);
			rocket.setTacheCourante(nvTache);

		}
		else{
			
			//Je regarde si y a quelqu'un qui m'a envoyé un mess comme quoi il a trouvé un ennemi
			WarMessage m = getFormatedMessageAboutEnemyTankToKill();
			if(m != null){
				CoordPolar p = rocket.getBrain().getIndirectPositionOfAgentWithMessage(m);
				rocket.getBrain().setHeading(p.getAngle());
				rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
				rocket.setPointOuAller(p.getDistance());//pour décrémenter : rocket.setPointOuAller(rocket.getPointOuAller-WarRocketLauncher.SPEED)
				//IL FAUT FAIRE LA TACHE SEDIRIGERVERSUNPOINT
			}
			
			if(rocket.getBrain().isBlocked())
				rocket.getBrain().setRandomHeading();
			
			rocket.getBrain().setDebugStringColor(Color.black);
			rocket.getBrain().setDebugString(toString());
			
			double angle = rocket.getBrain().getHeading() + new Random().nextInt(10) - new Random().nextInt(10);
			
			rocket.getBrain().setHeading(angle);
		
			rocket.setToReturn(MovableWarAgent.ACTION_MOVE);
		}
	}

	@Override
	public String toString() {
		return "Tache chercher ennemy";
	}
	
	private WarMessage getFormatedMessageAboutEnemyTankToKill() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		for (WarMessage m : rocket.getMessages()) {
			if(m.getMessage().equals(Constants.enemyTankHere) && m.getContent() != null && m.getContent().length == 2){
				return m;
			}
		}
		return null;
	}
}
