package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.turrets.Attaquer;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.braincontrollers.WarTurretAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;

public class WarTurretBrainController extends WarTurretAbstractBrainController {
	
	// Action de la tourelle à retourner
	private String toReturn;
	
	// Tache courante
	private TacheAgent tacheCourante;
	
	// Liste de messages
	private ArrayList<WarMessage> messages;
	
	
	public WarTurretBrainController() {
		super();
		tacheCourante = new Attaquer(this);
	}
	
	
	public ArrayList<WarMessage> getListeMessages() {
		return this.messages;
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
	

	@Override
	public String action() {
		toReturn = null;
		
		this.messages = getBrain().getMessages();
		
		doReflex();
		
		getBrain().setDebugStringColor(Color.black);
		getBrain().setDebugString(tacheCourante.toString());
		
		if(toReturn == null)
			tacheCourante.exec();

		if(toReturn == null) {
			return WarExplorer.ACTION_IDLE;
		} 

		return toReturn;
	}
	
	
	/**
	 * @action Définit l'ensemble des réflèxes de l'agent
	 */
	private void doReflex()
	{
		recharger();
		perceptFood();
	}
	
	//Reflexes -------------------------------------------------------------------------
	
	/**
	 * @action prévient les explorers que y a de la nourriture ici
	 * */
	private void perceptFood(){
		
		ArrayList<WarPercept> nourriture = getBrain().getPerceptsResources();
		
		if (nourriture != null && nourriture.size() > 0)
		{
			WarPercept food = nourriture.get(0);
			
			// On envoie un message aux autres explorer pour dire qu'il y a de la nourriture
			getBrain().broadcastMessageToAgentType(WarAgentType.WarExplorer, Constants.foodHere,
					String.valueOf(food.getDistance()), String.valueOf(food.getAngle()));
		}
	}
	
	/**
	 * @action recharge 
	 * */
	private void recharger(){
		if(!getBrain().isReloaded() && !getBrain().isReloading()){
			toReturn = WarTurret.ACTION_RELOAD;
		}
	}
}
