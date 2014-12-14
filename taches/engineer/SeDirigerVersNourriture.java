package pepisha.taches.engineer;

import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarEngineerBrainController;
import pepisha.taches.TacheAgent;

public class SeDirigerVersNourriture extends TacheAgent{

	public SeDirigerVersNourriture(WarBrainController b) {
		super(b);
	}

	@Override
	public void exec() {
		WarEngineerBrainController engineer = (WarEngineerBrainController) typeAgent;
		
		if (engineer.getDistance() > 0) {
			engineer.setDistance(engineer.getDistance() - WarEngineer.SPEED);
		}
		else{
			CreerTourelle nvTache=new CreerTourelle(engineer);
			engineer.setTacheCourante(nvTache);
		}
		
		engineer.setToReturn(WarEngineer.ACTION_MOVE);	
		
	}

	@Override
	public String toString() {
		return "Tache se diriger vers nourriture";
	}

}
