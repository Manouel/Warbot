package pepisha.taches.turrets;

import java.util.ArrayList;

import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.agents.projectiles.WarRocket;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.Constants;
import pepisha.WarRocketLauncherBrainController;
import pepisha.WarTurretBrainController;
import pepisha.taches.TacheAgent;

public class Attaquer extends TacheAgent {

	public Attaquer(WarBrainController b) {
		super(b);
	}

	@Override
	public void exec() {
		WarTurretBrainController turret = (WarTurretBrainController) typeAgent;
		
		ArrayList<WarPercept> perceptsAllies=turret.getBrain().getPerceptsAllies();
		ArrayList<WarPercept> perceptsEnnemis=turret.getBrain().getPerceptsEnemies();
		
		if(perceptsEnnemis !=null && perceptsEnnemis.size()>0){
			if (perceptsAllies == null || perceptsAllies.size() == 0){
				ArrayList<WarPercept> perceptsBases=turret.getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
				if(perceptsBases!=null && perceptsBases.size()>0){
					turret.getBrain().setHeading(perceptsBases.get(0).getAngle());
					turret.setToReturn(WarTurret.ACTION_FIRE);
				}
				else{
					turret.getBrain().setHeading(perceptsEnnemis.get(0).getAngle());
					turret.setToReturn(WarTurret.ACTION_FIRE);
				}
			}
			else{
				boolean quit=false;
				for(WarPercept pAllie : perceptsAllies){
					
					for(WarPercept pEnnemi : perceptsEnnemis){
						if(pAllie.getAngle()-Constants.RAYON_NON_ATTAQUE_TURRET<pEnnemi.getAngle()
												&& pAllie.getAngle()+Constants.RAYON_NON_ATTAQUE_TURRET>pEnnemi.getAngle()
												&& pAllie.getDistance()<pEnnemi.getDistance()){
								turret.setToReturn(WarTurret.ACTION_IDLE);
						}
						else{
							if(pEnnemi.getType().equals(WarAgentType.WarBase)){
								
								turret.getBrain().setHeading(pEnnemi.getAngle());
								turret.setToReturn(WarTurret.ACTION_FIRE);
								quit=true;
								break;
								
							}else{
								turret.getBrain().setHeading(pEnnemi.getAngle());
								turret.setToReturn(WarTurret.ACTION_FIRE);
							}
						}
							
					}
					if(quit){
						break;
					}
				}
			}
		}
		else{
			Tourner nvTache=new Tourner(turret);
			turret.setTacheCourante(nvTache);
		}
	}

	@Override
	public String toString() {
		return "Tache Attaquer";
	}
	
}
