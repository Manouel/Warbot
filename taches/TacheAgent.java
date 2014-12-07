package pepisha.taches;

import edu.turtlekit3.warbot.brains.WarBrainController;


public abstract class TacheAgent {
	
	protected WarBrainController typeAgent;
	
	public TacheAgent(WarBrainController b){
		typeAgent=b;
	}
	
	/**
	 * @param	b agent 
	 * @action exécute la tache
	 * */
	public abstract void exec();
	
	public abstract String toString();
}
