package pepisha.taches.explorer;

import java.util.ArrayList;

import pepisha.WarExplorerBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class DonnerNourritureBase extends TacheAgent {
	
	public DonnerNourritureBase(WarBrainController b){
		super(b);
	}

	@Override
	public void exec() {
		WarExplorerBrainController explorer = (WarExplorerBrainController) typeAgent;
		
		// Si mon sac est vide, on arrête de donner
		if(explorer.getBrain().isBagEmpty())
		{
			explorer.getBrain().setHeading(explorer.getBrain().getHeading() + 180);
			explorer.setTacheCourante(new ChercherNourriture(typeAgent));
			return;
		}
		
		ArrayList<WarPercept> bases = explorer.getBrain().getPerceptsAlliesByType(WarAgentType.WarBase);
		
		if (bases == null | bases.size() == 0 || bases.get(0).getDistance() > MovableWarAgent.MAX_DISTANCE_GIVE){
			explorer.setTacheCourante(new RetournerNourriture(typeAgent));
		}
		else{
			WarPercept base = bases.get(0);
			
			explorer.getBrain().setIdNextAgentToGive(base.getID());
			explorer.setToReturn(MovableWarAgent.ACTION_GIVE);	
		}

	}

	@Override
	public String toString() {
		return "Tache Donner Nourriture Base";
	}
	
	
}
