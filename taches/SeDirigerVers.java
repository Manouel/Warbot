package pepisha.taches;

import java.awt.Color;

import pepisha.WarRocketLauncherBrainController;
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
				System.out.println("SeDirigerVers : "+rocket.getDistancePointOuAller());
				rocket.getBrain().setDebugStringColor(Color.blue);
				rocket.getBrain().setDebugString("Je suis arrivé au point ! ");
				rocket.setSeDirigerVersUnPoint(false);
				AttaquerEnnemi nvTache=new AttaquerEnnemi(rocket);
				rocket.setTacheCourante(nvTache);
			}else{
				rocket.setDistancePointOuAller(
						rocket.getDistancePointOuAller()-WarRocketLauncher.SPEED);
				rocket.getBrain().setDebugStringColor(Color.blue);
				rocket.getBrain().setDebugString("Je me dirige vers un point ! ");
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
