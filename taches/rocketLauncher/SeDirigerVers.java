package pepisha.taches.rocketLauncher;

import java.awt.Color;

import pepisha.WarRocketLauncherBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class SeDirigerVers extends TacheAgent{

	public SeDirigerVers(WarBrainController b) {
		super(b);
	}

	@Override
	public void exec() {
		WarRocketLauncherBrainController rocket=(WarRocketLauncherBrainController)typeAgent;
		
		//Si seDirigerVersPoint et vrai
		if(rocket.getSeDirigerVersPoint()){
			
			//Si on est plus ou moins arrivé au point
			if(rocket.getDistancePointOuAller()<=0){
				rocket.setSeDirigerVersUnPoint(false);
				AttaquerEnnemi nvTache=new AttaquerEnnemi(rocket);
				rocket.setTacheCourante(nvTache);
			}else{ //Sinon on avance
				rocket.setDistancePointOuAller(rocket.getDistancePointOuAller()-WarRocketLauncher.SPEED);
				rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
			}
		}
		
		//Sinon on passe en mode chercherEnnemi
		else{
			ChercherEnnemi nvTache=new ChercherEnnemi(rocket);
			rocket.setTacheCourante(nvTache);
			rocket.setToReturn(WarRocketLauncher.ACTION_MOVE);
		}
	}

	@Override
	public String toString() {
		return "Tache se diriger vers";
	}

}
