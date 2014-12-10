package pepisha.taches.explorer;

import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarExplorerBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.ControllableWarAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;

public class ChercherNourriture extends TacheAgent {

	public ChercherNourriture(WarBrainController b){
		super(b);
	}
	
	
	/**
	 * @action Récupère un message à propos de la nourriture s'il y en a
	 * @return Message food si trouvé, sinon null
	 */
	private WarMessage getMessageAboutFood()
	{
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		for (WarMessage m : explorer.getListeMessages())
		{	
			if(m.getMessage().equals("foodHere"))
				return m;
		}
		
		return null;
	}

	@Override
	public void exec() {
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		// Si mon sac est plein je change de tache
		if (explorer.getBrain().isBagFull()) {
			explorer.setTacheCourante(new RetournerNourriture(typeAgent));
			return;
		}
			
		ArrayList<WarPercept> nourriture = explorer.getBrain().getPerceptsResources();
		
		// Si il y a de la nourriture dans notre champ de vision
		if(nourriture != null && nourriture.size() > 0)
		{			
			WarPercept lePlusProche = nourriture.get(0); // le 0 est le plus proche normalement
			
			if(lePlusProche.getDistance() <= ControllableWarAgent.MAX_DISTANCE_GIVE) {
				explorer.setToReturn(MovableWarAgent.ACTION_TAKE);
			} else {
				explorer.getBrain().setHeading(lePlusProche.getAngle());
				explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
			}
			
			explorer.setDistance(0.0);
		}
		else 		// Pas de nourriture dans notre champ de vision
		{
			// Pas de message déjà reçu
			if (explorer.getDistance() <= 0.0) {

				// Je regarde si j'ai reçu un message qui dit qu'il y a de la nourriture
				WarMessage food = getMessageAboutFood();
				
				if (food != null) {
					explorer.setDistance(food.getDistance());
					explorer.getBrain().setHeading(food.getAngle());
				}
				else {
					explorer.getBrain().setRandomHeading(20);
				}
			}
			else {
				explorer.setDistance(explorer.getDistance()-1);
			}
			
			explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
		}
	}

	@Override
	public String toString() {
		return "Tache Chercher Nourriture";
	}
}