package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.rocketLauncher.AttaquerEnnemi;
import pepisha.taches.rocketLauncher.ChercherEnnemi;
import pepisha.taches.rocketLauncher.SeDirigerVers;
import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.brains.braincontrollers.WarRocketLauncherAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.tools.CoordPolar;

public class WarRocketLauncherBrainController extends WarRocketLauncherAbstractBrainController {

	//Attributs ----------------------------------------------------------------------------
	
	private String toReturn = null;
	private TacheAgent tacheCourante;//Tache courante
	
	private boolean seDirigerVersPoint=false; //Si true, on se dirige vers le pt poinOuAller
	private double distancePointOuAller;
	

	ArrayList<WarMessage> messages;
	
	// Vie précédente
	private int vie;
	
	
	//Constructeur ---------------------------------
	
	public WarRocketLauncherBrainController() {
		super();
		tacheCourante=new ChercherEnnemi(this);
		vie = WarKamikaze.MAX_HEALTH;
		//messageAboutEnemyBase=null;
	}
	
	//Accesseurs ----------------------------------------------------------------------------
	
	public int getVie(){
		return vie;
	}
	public void setToReturn(String nvReturn){
		toReturn=nvReturn;
	}
	
	public void setTacheCourante(TacheAgent nvTache){
		tacheCourante=nvTache;
	}
	public ArrayList<WarMessage> getMessages(){
		return messages;
	}
	
	public void setDistancePointOuAller(double nvPoint){
		distancePointOuAller=nvPoint;
	}
	
	public double getDistancePointOuAller(){
		return distancePointOuAller;
	}
	
	public boolean getSeDirigerVersPoint(){
		return seDirigerVersPoint;
	}
	
	public void setSeDirigerVersUnPoint(boolean val){
		seDirigerVersPoint=val;
	}
	

	//Méthodes ---------------------------------------------------------------------------
	@Override
	public String action() {
		// Develop behaviour here
		
		toReturn = null;
		this.messages = getBrain().getMessages();
		
		doReflex();
		
		if (getBrain().isBlocked()){
			getBrain().setRandomHeading();
		}
		
		this.getBrain().setDebugString(tacheCourante.toString());
		
		if(toReturn==null){//Exécution de la tache courante
			tacheCourante.exec();
		}
		if(toReturn == null){
			toReturn = WarRocketLauncher.ACTION_MOVE;
		}

		vie = getBrain().getHealth();
		
		return toReturn;
	}
	
	/**
	 * @action exécute les réflexes 
	 * */
	private void doReflex(){
		imAlive();
		perceptFood();
		recharger();
		isBaseAttacked();			// Défense de notre base
		attackEnemyBase();			// Attaque de la base ennemie
	}
	
	
	//Reflexes -----------------------------------------------------------------------------------
	
	/**
	 * @action recharge 
	 * */
	private void recharger(){
		if(!getBrain().isReloaded() && !getBrain().isReloading()){
			toReturn =  WarRocketLauncher.ACTION_RELOAD;
		}
	}
	
	/**
	 * @action Défend la base si elle a a envoyé un message signalant qu'elle a été attaquée
	 * */
	private void isBaseAttacked(){
		for(WarMessage m : this.messages){
			
			if(m.getMessage().equals(Constants.baseIsAttack)){
				
				CoordPolar p = getBrain().getIndirectPositionOfAgentWithMessage(m);
				
				// On s'oriente vers l'ennemi
				getBrain().setHeading(p.getAngle());
				
				// Si l'ennemi est dans notre champ de vision
				if(p.getDistance()<=WarRocketLauncher.DISTANCE_OF_VIEW){
					getBrain().setDebugStringColor(Color.green);
					AttaquerEnnemi nvTache=new AttaquerEnnemi(this);
					this.setTacheCourante(nvTache);
				}
				// Sinon si on est dans le rayon de défense
				else if(p.getDistance() !=0 && p.getDistance()<=Constants.rayonDefenseBase){
					getBrain().setDebugStringColor(Color.red);
					this.setDistancePointOuAller(p.getDistance()-WarRocketLauncher.DISTANCE_OF_VIEW);
					this.setSeDirigerVersUnPoint(true);
					SeDirigerVers nvTache=new SeDirigerVers(this);
					this.setTacheCourante(nvTache);
				}		
			}
		}		
	}

	/**
	 * @action regarde si il faut attaquer la base ennemmie 
	 * (si on a un message à propose de la base ennemie et qu'elle est dans le rayon d'attaque)
	 * */
	private void attackEnemyBase(){
		
		//On vérifie qu'on n'a pas déjà une base ennemie dans les percepts
		ArrayList<WarPercept> bases = getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		
		if (bases != null && bases.size() > 0){
			getBrain().setHeading(bases.get(0).getAngle());
			AttaquerEnnemi nvTache=new AttaquerEnnemi(this);
			setTacheCourante(nvTache);
		}
		else{
			WarMessage m=getMessageAboutEnemyBase();
			if(m!= null) {
				CoordPolar p = getBrain().getIndirectPositionOfAgentWithMessage(m);
				
				if(p.getDistance()<=Constants.rayonAttaqueBaseEnnemie){
					
					//On se dirige vers la base ennemie
					getBrain().setHeading(p.getAngle());
					setDistancePointOuAller(p.getDistance()-WarRocketLauncher.DISTANCE_OF_VIEW);
					setSeDirigerVersUnPoint(true);
					//setToReturn(WarRocketLauncher.ACTION_MOVE);
					SeDirigerVers nvTache=new SeDirigerVers(this);
					setTacheCourante(nvTache);
				}
			}
		}
	}
	
	/**
	 * @action enregistre la position de la base ennemie fournie par un agent
	 * */
	private WarMessage getMessageAboutEnemyBase() {
		for(WarMessage m : this.messages){
			if(m.getMessage().equals(Constants.enemyBaseHere)){
				//messageAboutEnemyBase=m;
				return m;
			}
		}
		return null;
	}

	
	/**
	 * @action Prévient la base que l'agent est encore vivant
	 */
	private void imAlive()
	{
		getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.imAlive, "");
	}
	
	/**
	 * @action prévient les explorers que y a de la nourriture ici
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