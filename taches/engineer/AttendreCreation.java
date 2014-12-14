package pepisha.taches.engineer;

import java.util.ArrayList;

import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarEngineerBrainController;
import pepisha.taches.TacheAgent;

public class AttendreCreation extends TacheAgent
{

	public AttendreCreation(WarBrainController b) {
		super(b);
	}

	@Override
	public void exec() {
		WarEngineerBrainController engineer = (WarEngineerBrainController) typeAgent;
		
		if (engineer.getDistance() > 0) {
			engineer.setDistance(engineer.getDistance() - WarEngineer.SPEED);
		}
		else {
			ArrayList<WarPercept> bases = engineer.getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
			
			// On teste les percepts de base ennemie
			if (bases != null && bases.size() > 0 && bases.get(0).getDistance() <= WarTurret.DISTANCE_OF_VIEW){
				engineer.setTacheCourante(new CreerTourelle(engineer));
			}
		}
		
		engineer.setToReturn(WarEngineer.ACTION_MOVE);	
	}

	@Override
	public String toString() {
		return "Tache Attendre Creation";
	}
}
