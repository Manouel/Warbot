package pepisha.taches.rocketLauncher;

import java.awt.Color;

import pepisha.WarRocketLauncherBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class SeDirigerVers extends TacheAgent{

	public SeDirigerVers(WarBrainController b) {
		super(b);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		if(rocket.getSeDirigerVersPoint()){
			if(rocket.getDistancePointOuAller()<=0){
				rocket.setSeDirigerVersUnPoint(false);
				AttaquerEnnemi nvTache=new AttaquerEnnemi(rocket);
				rocket.setTacheCourante(nvTache);
			}else{
				rocket.setDistancePointOuAller(
				rocket.getDistancePointOuAller()-WarRocketLauncher.SPEED);
				rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
			}
		}
		else{
			ChercherEnnemi nvTache=new ChercherEnnemi(rocket);
			rocket.setTacheCourante(nvTache);
		}
		
	}

	@Override
	public String toString() {
		return "Tache se diriger vers";
	}

}
