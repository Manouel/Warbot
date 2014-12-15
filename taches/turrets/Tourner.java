package pepisha.taches.turrets;

import java.util.ArrayList;

import pepisha.WarTurretBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class Tourner extends TacheAgent {

	public Tourner(WarBrainController b) {
		super(b);
	}

	@Override
	public void exec() {
		WarTurretBrainController turret = (WarTurretBrainController) typeAgent;
		
		ArrayList<WarPercept> percept = turret.getBrain().getPerceptsEnemies();

		if(percept != null && percept.size() > 0){	
			turret.setToReturn(WarTurret.ACTION_IDLE);
			Attaquer nvTache=new Attaquer(turret);
			turret.setTacheCourante(nvTache);
		}
		else{
			turret.getBrain().setHeading(turret.getBrain().getHeading()+180);
			turret.setToReturn(WarTurret.ACTION_IDLE);
		}
	}

	@Override
	public String toString() {
		return "Tache Tourner";
	}
	
}
