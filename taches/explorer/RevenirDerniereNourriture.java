package pepisha.taches.explorer;

import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarExplorerBrainController;
import pepisha.taches.TacheAgent;

public class RevenirDerniereNourriture extends TacheAgent
{

	public RevenirDerniereNourriture(WarBrainController b) {
		super(b);
	}

	@Override
	public void exec() {
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		if (explorer.getDistanceLastFood() > 0) {
			explorer.setDistanceLastFood(explorer.getDistanceLastFood() - WarExplorer.SPEED);
		} else {
			explorer.setTacheCourante(new ChercherNourriture(typeAgent));
		}
		
		explorer.setToReturn(WarExplorer.ACTION_MOVE);
	}

	@Override
	public String toString() {
		return "Tache Revenir Derniere Nourriture";
	}

}
