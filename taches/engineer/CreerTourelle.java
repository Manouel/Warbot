package pepisha.taches.engineer;

import pepisha.WarBaseBrainController;
import pepisha.WarEngineerBrainController;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class CreerTourelle extends TacheAgent {
	
	// Energie minimum pour créer un nouvel agent
	private static final int MIN_HEATH_TO_CREATE = (int) (WarEngineer.MAX_HEALTH * 0.8);
	
	
	public CreerTourelle(WarBrainController b){
		super((WarEngineerBrainController)b);
	}

	@Override
	public void exec() {
		WarEngineerBrainController engineer = (WarEngineerBrainController) typeAgent;
		
		if(engineer.getBrain().getHealth() > MIN_HEATH_TO_CREATE)
		{
			engineer.getBrain().setNextAgentToCreate(WarAgentType.WarTurret);
			engineer.setToReturn(WarBase.ACTION_CREATE);
		}
	}

	@Override
	public String toString() {
		return "Tache Creer Tourelle";
	}

}
