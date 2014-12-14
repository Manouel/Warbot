package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.kamikazes.SeSuicider;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.braincontrollers.WarKamikazeAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;

public class WarKamikazeBrainController extends WarKamikazeAbstractBrainController {
	
	// Action du kamikaze à retourner
	private String toReturn;
	
	// Tache courante
	private TacheAgent tacheCourante;
	
	// Liste de messages
	private ArrayList<WarMessage> messages;
	
	// Vie précédente
	private int vie;
	
	private boolean seDirigerVersPoint=false; //Si true, on se dirige vers le pt poinOuAller
	private double distancePointOuAller;
	
	
	public WarKamikazeBrainController() {
		super();
		tacheCourante = new SeSuicider(this);
		vie = WarKamikaze.MAX_HEALTH;
	}
	
	//Accesseurs----------------------------------------------------------------------
	public double getDistancePointOuAller(){
		return distancePointOuAller;
	}
	
	public boolean getSeDirigerVersPoint(){
		return seDirigerVersPoint;
	}
	
	public void setDistancePoinOuAller(double nvPoint){
		distancePointOuAller=nvPoint;
	}
	
	public void setSeDirigerVersPoint(boolean nvVal){
		seDirigerVersPoint=nvVal;
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
	
	//Méthodes -----------------------------------------------------------------------------
	@Override
	public String action() {
		toReturn = null;
		
		this.messages = getBrain().getMessages();
		
		if (getBrain().isBlocked())
			getBrain().setRandomHeading();
		
		doReflex();
		
		getBrain().setDebugStringColor(Color.black);
		getBrain().setDebugString(tacheCourante.toString());
		
		if(toReturn == null)
			tacheCourante.exec();

		if(toReturn == null) {	
			return WarExplorer.ACTION_MOVE;
		} 

		return toReturn;
	}
	
	
	/**
	 * @action Définit l'ensemble des réflèxes de l'agent
	 */
	private void doReflex()
	{
		recharger();
		seDefendre();
		imAlive();
		vie = getBrain().getHealth();
		perceptFood();
	}
	
	//Reflexes -----------------------------------------------------------------------
	/**
	 * @action Prévient la base que l'agent est encore vivant
	 */
	private void imAlive()
	{
		getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.imAlive, "");
	}
	
	/**
	 * @action recharge
	 * */
	private void recharger(){
		if(!getBrain().isReloaded() && !getBrain().isReloading()){
			toReturn = WarTurret.ACTION_RELOAD;
		}
	}
	
	private boolean perdVie(){
		return getBrain().getHealth() < vie;
	}
	
	/**
	 * @action Si le kamikaze est attaqué et qu'il n'a plus beaucoup de vie, il se suicide sur l'attaquant
	 */
	private void seDefendre() {
		
		// Si on va mourir
		if(getBrain().getHealth() < Constants.vieMaxAvantSuicide && perdVie()){
			
			// On tire sur l'ennemi perçu
			ArrayList<WarPercept> ennemis = getBrain().getPerceptsEnemies();
			if(getBrain().isReloaded() && ennemis != null && ennemis.size() > 0) {
				getBrain().setHeading(ennemis.get(0).getAngle());
				toReturn = WarKamikaze.ACTION_FIRE;
			}
		}	
	}
	
	/**
	 * @action prévient les explorers et engineers que y a de la nourriture ici
	 * */
	private void perceptFood(){
		
		ArrayList<WarPercept> nourriture = getBrain().getPerceptsResources();
		
		if (nourriture != null && nourriture.size() > 0)
		{
			WarPercept food = nourriture.get(0);
			
			// On envoie un message aux autres explorers et engineers pour dire qu'il y a de la nourriture
			getBrain().broadcastMessageToAgentType(WarAgentType.WarExplorer, Constants.foodHere,
					String.valueOf(food.getDistance()), String.valueOf(food.getAngle()));
			getBrain().broadcastMessageToAgentType(WarAgentType.WarEngineer, Constants.foodHere,
					String.valueOf(food.getDistance()), String.valueOf(food.getAngle()));
		}
	}
}
