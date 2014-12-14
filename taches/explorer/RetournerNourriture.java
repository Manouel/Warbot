package pepisha.taches.explorer;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.Constants;
import pepisha.WarExplorerBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;

public class RetournerNourriture extends TacheAgent {
	
	public RetournerNourriture(WarBrainController b){
		super(b);
	}
	
	/**
	 * @action Récupère les messages reçus provenant des bases
	 */
	private WarMessage getMessageFromBase()
	{
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		for (WarMessage m : explorer.getListeMessages())
		{
			if(m.getSenderType().equals(WarAgentType.WarBase))
				return m;
		}
		
		return null;
	}

	@Override
	public void exec() {
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		// On augmente la distance à parcourir pour revenir à la dernière nourriture vue
		explorer.setDistanceLastFood(explorer.getDistanceLastFood() + WarExplorer.SPEED);
		
		// On récupère la liste des bases alliées qui sont dans les environs
		ArrayList<WarPercept> bases = explorer.getBrain().getPerceptsAlliesByType(WarAgentType.WarBase);
		
		// Si pas de base dans champ de vision
		if (bases == null || bases.size() == 0)
		{
			WarMessage m = getMessageFromBase();
			
			if (m != null) {
				explorer.getBrain().setHeading(m.getAngle());
			}
			else {
				// J'envoie un message aux bases pour savoir où elles sont
				explorer.getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.whereAreYou, "");
			}

			explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
		}
		else		// On voit une base
		{
			WarPercept base = bases.get(0);
			
			// Si la base est assez proche pour qu'on lui donne
			if (base.getDistance() <= MovableWarAgent.MAX_DISTANCE_GIVE) {
				explorer.setTacheCourante(new DonnerNourritureBase(typeAgent));
			}
			else {		// Si elle est trop loin, on se dirige vers elle
				explorer.getBrain().setHeading(base.getAngle());
				explorer.setToReturn(MovableWarAgent.ACTION_MOVE);
			}
		}
	}

	@Override
	public String toString() {
		return "Tache Retourner Nourriture";
	}
}
