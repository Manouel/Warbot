package pepisha;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pepisha.taches.ChercherNourriture;
import pepisha.taches.TacheAgent;
import edu.turtlekit3.warbot.agents.ControllableWarAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.braincontrollers.WarExplorerAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;


public class WarExplorerBrainController extends WarExplorerAbstractBrainController
{	
	/** Attributs **/
	
	// Action de l'explorer à retourner
	private String toReturn;
	
	// Tache courante
	private TacheAgent tacheCourante;
	
	// Distance de la nourriture indiquée par d'autres agents
	private double distance = 0.0;
	
	
	/**
	 * Constructeur
	 */
	public WarExplorerBrainController() {
		super();
		tacheCourante = new ChercherNourriture(this);
	}
	
	/**
	 * @action change le toReturn
	 * */
	public void setToReturn(String nvReturn){
		toReturn=nvReturn;
	}
	
	public void setTacheCourante(TacheAgent nvTache){
		tacheCourante=nvTache;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public void setDistance(double nvDistance){
		distance=nvDistance;
	}
	
	
	/**
	 * @action Définit le comportement de l'explorer
	 * @return Action à effectuer (move,take etc...)
	 */
	public String action() 
	{	
		toReturn = null;
		
		doReflex();
		
		getBrain().setDebugStringColor(Color.black);
		getBrain().setDebugString(tacheCourante.toString());
		
		if(toReturn == null)
			tacheCourante.exec();

		if(toReturn == null) {
			if (getBrain().isBlocked())
				getBrain().setRandomHeading();
	
			return WarExplorer.ACTION_MOVE;
		} 
		else {
			return toReturn;
		}
	}
	
	
	/**
	 * @action Définit l'ensemble des réflèxes de l'agent
	 */
	private void doReflex()
	{
		perceptEnemyBase();
	}
	
	
	/**
	 * @action Envoie un message à sa base s'il perçoit la base ennemie
	 */
	private void perceptEnemyBase()
	{
		ArrayList<WarPercept> basesEnnemies = getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		
		if (basesEnnemies != null && basesEnnemies.size() > 0)
		{	
			// On envoie aux bases la position de la base ennemie
			getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.enemyBaseHere, (String[]) null);
			getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.enemyBaseHere, (String[]) null);
		}
	}
}
