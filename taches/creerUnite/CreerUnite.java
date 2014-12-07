package pepisha.taches.creerUnite;

import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarBaseBrainController;
import pepisha.taches.TacheAgent;

public abstract class CreerUnite extends TacheAgent{
	
	public CreerUnite(WarBrainController b){
		super((WarBaseBrainController)b);
	}
}
