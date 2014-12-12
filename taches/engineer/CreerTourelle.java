package pepisha.taches.engineer;

import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarBaseBrainController;
import pepisha.WarEngineerBrainController;
import pepisha.WarKamikazeBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.ControllableWarAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.tools.CoordPolar;

public class CreerTourelle extends TacheAgent {
	
	// Energie minimum pour créer un nouvel agent
	private static final int MIN_HEATH_TO_CREATE = (int) (WarEngineer.MAX_HEALTH * 0.8);
	
	private static final int DISTANCE_MAX_ATTAQUE_BASE = 1000;
	
	
	public CreerTourelle(WarBrainController b){
		super((WarEngineerBrainController)b);
	}
	
	private WarMessage getEnnemyBaseMessage()
	{
		WarEngineerBrainController enginneer = (WarEngineerBrainController) typeAgent;
		
		for (WarMessage m : enginneer.getListeMessages()) {
			if (m.getMessage().equals(Constants.enemyBaseHere)) {
				return m;
			}
		}
		
		return null;
	}

	@Override
	public void exec() {
		WarEngineerBrainController engineer = (WarEngineerBrainController) typeAgent;
		
		if(engineer.getBrain().getHealth() > MIN_HEATH_TO_CREATE)
		{
			ArrayList<WarPercept> bases = engineer.getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
			
			// On teste les percepts de base ennemie
			if (bases != null && bases.size() > 0) {
				if(bases.get(0).getDistance() < WarTurret.DISTANCE_OF_VIEW){
					engineer.getBrain().setNextAgentToCreate(WarAgentType.WarTurret);
					engineer.setToReturn(WarEngineer.ACTION_CREATE);
				} else {
					engineer.getBrain().setHeading(bases.get(0).getAngle());
					engineer.setToReturn(WarEngineer.ACTION_MOVE);
				}
			}
			else {
				// On regarde les messages base ennemie
				WarMessage m = getEnnemyBaseMessage();
				if (m != null) {
					CoordPolar p = engineer.getBrain().getIndirectPositionOfAgentWithMessage(m);
					
					if (p.getDistance() < DISTANCE_MAX_ATTAQUE_BASE) {
						engineer.getBrain().setHeading(p.getAngle());
						engineer.setDistance(p.getDistance() - WarEngineer.DISTANCE_OF_VIEW);
						engineer.setTacheCourante(new AttendreCreation(engineer));
						engineer.setToReturn(WarEngineer.ACTION_MOVE);
					}
					else {				
						engineer.getBrain().setNextAgentToCreate(WarAgentType.WarTurret);
						engineer.setToReturn(WarEngineer.ACTION_CREATE);
					}
				}
				else {
					engineer.getBrain().setNextAgentToCreate(WarAgentType.WarTurret);
					engineer.setToReturn(WarEngineer.ACTION_CREATE);
				}
			}
		}
		else {				// Pas assez de vie
			if (engineer.getFood() != null) {
				WarPercept food = engineer.getFood();
				
				if(food.getDistance() <= ControllableWarAgent.MAX_DISTANCE_GIVE) {
					engineer.setToReturn(MovableWarAgent.ACTION_TAKE);
				} else {
					engineer.getBrain().setHeading(food.getAngle());
					engineer.setToReturn(MovableWarAgent.ACTION_MOVE);
				}
			}
			else {
				engineer.getBrain().setRandomHeading(10);
				engineer.setToReturn(WarEngineer.ACTION_MOVE);
			}
		}
	}

	@Override
	public String toString() {
		return "Tache Creer Tourelle";
	}

}
