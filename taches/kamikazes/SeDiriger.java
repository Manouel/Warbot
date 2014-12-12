package pepisha.taches.kamikazes;



import java.awt.Color;

import pepisha.WarKamikazeBrainController;
import pepisha.WarRocketLauncherBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class SeDiriger extends TacheAgent{

	public SeDiriger(WarBrainController b) {
		super(b);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void exec() {
		// TODO Auto-generated method stub
		WarKamikazeBrainController kamikaze=(WarKamikazeBrainController)typeAgent;
		
		//Si seDirigerVersPoint et vrai
		if(kamikaze.getSeDirigerVersPoint()){
			
			//Si on est plus ou moins arrivé au point
			if(kamikaze.getDistancePointOuAller()<=0){
				kamikaze.setSeDirigerVersPoint(false);
				kamikaze.getBrain().setDebugStringColor(Color.green);
				SeSuicider nvTache=new SeSuicider(kamikaze);
				kamikaze.setTacheCourante(nvTache);
				//Sinon on avance
			}else{
				kamikaze.setDistancePoinOuAller(
						kamikaze.getDistancePointOuAller()-WarKamikaze.SPEED);
				kamikaze.setToReturn(WarKamikaze.ACTION_MOVE);
			}
		}
		
		//Sinon on passe en mode sesuicider
		else{
			SeSuicider nvTache=new SeSuicider(kamikaze);
			kamikaze.setTacheCourante(nvTache);
			kamikaze.setToReturn(WarKamikaze.ACTION_MOVE);
		}
		
	}

	@Override
	public String toString() {
		return "Tache se diriger";
	}

}
