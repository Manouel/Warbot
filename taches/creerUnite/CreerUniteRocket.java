package pepisha.taches.creerUnite;

import pepisha.WarBaseBrainController;
import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.capacities.Creator;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.brains.WarBrainController;

public class CreerUniteRocket extends CreerUnite{
	
	
	public CreerUniteRocket(WarBaseBrainController b) {
		super(b);
	}

	// Energie minimum pour créer un nouvel agent
	private static final int MIN_HEATH_TO_CREATE = (int) (WarBase.MAX_HEALTH * 0.8);

	@Override
	public void exec() {
		WarBaseBrainController base=(WarBaseBrainController)typeAgent;
		if(base.getBrain().getHealth()>MIN_HEATH_TO_CREATE)
		{
			base.getBrain().setNextAgentToCreate(WarAgentType.WarRocketLauncher);
			base.getBrain().setDebugString("Création d'un War Rocket");
			base.setToReturn(WarBase.ACTION_CREATE);		
		}
	}

	@Override
	public String toString() {
		
		return "Tache Créer unité rocket";
	}

}
