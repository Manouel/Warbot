package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.engineer.CreerTourelle;
import pepisha.taches.engineer.SeDirigerVersNourriture;
import edu.turtlekit3.warbot.agents.ControllableWarAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.braincontrollers.WarEngineerAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;

public class WarEngineerBrainController extends WarEngineerAbstractBrainController {
	
	// Action de l'ingénieur à retourner
	private String toReturn;
	
	// Tache courante
	private TacheAgent tacheCourante;
	
	// Liste de messages
	private ArrayList<WarMessage> messages;
	
	// Distance à parcourir pour poser la tourelle
	private double distance = 0.0;
	
	// Nourriture perçue
	private WarPercept food;
	
	
	public WarEngineerBrainController() {
		super();
		tacheCourante = new CreerTourelle(this);
	}
	
	
	public ArrayList<WarMessage> getListeMessages() {
		return this.messages;
	}
	
	public double getDistance() {
		return this.distance;
	}
	
	public void setDistance(double dist) {
		distance = dist;
	}
	
	public WarPercept getFood() {
		return this.food;
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
		eatFood();
		perceptEnemyBase();
		imAlive();
		perceptFood();
	}
	
	//Reflexes -------------------------------------------------------------------
	

	
	/**
	 * @action prévient les explorers et ingénieurs qu'il y a de la nourriture ici
	 * */
	private void perceptFood(){
		
		food = null;
		
		ArrayList<WarPercept> nourriture = getBrain().getPerceptsResources();
		
		if (nourriture != null && nourriture.size() > 0)
		{
			food = nourriture.get(0);
			
			// On envoie un message aux autres explorer pour dire qu'il y a de la nourriture
			getBrain().broadcastMessageToAgentType(WarAgentType.WarExplorer, Constants.foodHere,
					String.valueOf(food.getDistance()), String.valueOf(food.getAngle()));
			getBrain().broadcastMessageToAgentType(WarAgentType.WarEngineer, Constants.foodHere,
					String.valueOf(food.getDistance()), String.valueOf(food.getAngle()));
			
			if(!(tacheCourante.getClass().getSimpleName().equals("AttendreCreation"))){
				CreerTourelle nvTache=new CreerTourelle(this);
				setTacheCourante(nvTache);
			}
		}
		

	}
	
	/**
	 * @action Prévient la base que l'agent est encore vivant
	 */
	private void imAlive()
	{
		getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.imAlive, "");
	}
	
	private void eatFood()
	{
		if (!getBrain().isBagEmpty()) {
			toReturn = WarEngineer.ACTION_EAT;
		}
	}
	
	private void perceptEnemyBase()
	{
		ArrayList<WarPercept> bases = getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		
		if (bases != null && bases.size() > 0){
			WarPercept base = bases.get(0);
			
			// On envoie aux bases la position de la base ennemie
			getBrain().broadcastMessageToAll(Constants.enemyBaseHere, String.valueOf(base.getDistance()), String.valueOf(base.getAngle()));
		}
	}
}
