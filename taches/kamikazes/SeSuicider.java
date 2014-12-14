package pepisha.taches.kamikazes;

import java.util.ArrayList;

import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.tools.CoordPolar;
import pepisha.Constants;
import pepisha.WarKamikazeBrainController;
import pepisha.taches.TacheAgent;
import pepisha.taches.rocketLauncher.SeDirigerVers;

public class SeSuicider extends TacheAgent
{
	private static final int DISTANCE_MAX_ATTAQUE_BASE = 1000;
	private static final int NB_MIN_ROCKETS_TO_KILL = 3;
	private static final int NB_MIN_TURRET_TO_KILL = 2;

	public SeSuicider(WarBrainController b) {
		super(b);
	}
	
	private WarMessage getEnemyBaseMessage()
	{
		WarKamikazeBrainController kamikaze = (WarKamikazeBrainController) typeAgent;
		
		for (WarMessage m : kamikaze.getListeMessages()) {
			if (m.getMessage().equals(Constants.enemyBaseHere)) {
				return m;
			}
		}
		
		return null;
	}

	@Override
	public void exec() {
		WarKamikazeBrainController kamikaze = (WarKamikazeBrainController) typeAgent;
		
		// Si on perçoit une base
		ArrayList<WarPercept> bases = kamikaze.getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		if(bases != null && bases.size() > 0) {
			WarPercept base = bases.get(0);
			
			// On envoie la position
			kamikaze.getBrain().broadcastMessageToAll(Constants.enemyBaseHere, String.valueOf(base.getDistance()), String.valueOf(base.getAngle()));
			
			// On se dirige vers elle
			kamikaze.getBrain().setHeading(base.getAngle());

			if(kamikaze.getBrain().isReloaded()){
				kamikaze.setToReturn(WarKamikaze.ACTION_FIRE);
			}
		}
		else {
			// Si on reçoit un message de base ennemie
			
			WarMessage m = getEnemyBaseMessage();
			if (m != null) {
				CoordPolar p = kamikaze.getBrain().getIndirectPositionOfAgentWithMessage(m);
				
				if (p.getDistance() < DISTANCE_MAX_ATTAQUE_BASE) {
					kamikaze.setDistancePoinOuAller(p.getDistance()-WarKamikaze.DISTANCE_OF_VIEW);
					kamikaze.setSeDirigerVersPoint(true);
					kamikaze.getBrain().setHeading(p.getAngle());
					kamikaze.setToReturn(WarKamikaze.ACTION_MOVE);
					SeDiriger nvTache=new SeDiriger(kamikaze);
					kamikaze.setTacheCourante(nvTache);
					kamikaze.setToReturn(WarKamikaze.ACTION_MOVE);
				}
			}
			else {		// Sinon on cherche des rockets
				ArrayList<WarPercept> rockets = kamikaze.getBrain().getPerceptsEnemiesByType(WarAgentType.WarRocketLauncher);
				ArrayList<WarPercept> tourelles = kamikaze.getBrain().getPerceptsEnemiesByType(WarAgentType.WarTurret);
				
				if (kamikaze.getBrain().isReloaded() && rockets != null && rockets.size() >= NB_MIN_ROCKETS_TO_KILL) {
					kamikaze.getBrain().setHeading(rockets.get(0).getAngle());
					kamikaze.setToReturn(WarKamikaze.ACTION_FIRE);
				}
				else if(kamikaze.getBrain().isReloaded() && tourelles != null && tourelles.size() >= NB_MIN_TURRET_TO_KILL){
					kamikaze.getBrain().setHeading(tourelles.get(0).getAngle());
					kamikaze.setToReturn(WarKamikaze.ACTION_FIRE);
				}
				else {	// Si pas de rockets, on cherche
					kamikaze.getBrain().setRandomHeading(10);
					kamikaze.setToReturn(WarKamikaze.ACTION_MOVE);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Tache Chercher ennemi à tuer";
	}

}
