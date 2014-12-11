package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.rocketLauncher.ChercherEnnemi;
import pepisha.taches.rocketLauncher.SeDirigerVers;

import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
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
	
	private static final double rayonAttaqueBaseEnnemie = 500;
	private static final double rayonDefenseBase = 500;
	private static final double rayonAttaqueEnnemi = 400;//Rayon dans lequel on attaque les ennemis
	

	ArrayList<WarMessage> messages;
	
	public WarRocketLauncherBrainController() {
		super();
		tacheCourante=new ChercherEnnemi(this);
	}
	
	//Accesseurs ----------------------------------------------------------------
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
	
	public double getRayonAttaqueEnnemi(){
		return rayonAttaqueEnnemi;
	}
	
	public double getRayonAttaqueBaseEnnemie(){
		return rayonAttaqueBaseEnnemie;
	}
	
	public double getRayonDefenseBase(){
		return rayonDefenseBase;
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
			ChercherEnnemi nvTache=new ChercherEnnemi(this);
			
			this.setTacheCourante(nvTache);
		}
		
		this.getBrain().setDebugString(tacheCourante.toString());
		
		if(toReturn==null){//Exécution de la tache courante
			tacheCourante.exec();
		}
		if(toReturn == null){
			if (getBrain().isBlocked())
				getBrain().setRandomHeading();
			toReturn = WarRocketLauncher.ACTION_MOVE;
		}
		
		return toReturn;
	}
	
	/**
	 * @action exécute les réflexes 
	 * */
	private void doReflex(){
		imAlive();
		recharger();
		isBaseAttacked() ;
		attackEnemyBase();
		perceptFood();
		//eviterMissile();
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
	 * @return true si une base alliée est attaquée et a envoyé un message signalant qu'elle a été attaquée
	 * */
	private void isBaseAttacked(){
		for(WarMessage m : this.messages){
			if(m.getMessage().equals(Constants.baseIsAttack)){	
				CoordPolar p = getBrain().getIndirectPositionOfAgentWithMessage(m);
				if(p.getDistance()!=0 && p.getDistance()<=rayonDefenseBase){
					getBrain().setDebugStringColor(Color.red);
					this.setDistancePointOuAller(p.getAngle());
					this.setSeDirigerVersUnPoint(true);
					this.getBrain().setHeading(p.getAngle());
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
		WarMessage m = getMessageAboutEnemyBase();
		if(m!= null && m.getDistance()<=rayonAttaqueBaseEnnemie){
			CoordPolar p = getBrain().getIndirectPositionOfAgentWithMessage(m);
			setDistancePointOuAller(p.getDistance());
			setSeDirigerVersUnPoint(true);
			getBrain().setHeading(p.getAngle());
			setToReturn(WarRocketLauncher.ACTION_MOVE);
			SeDirigerVers nvTache=new SeDirigerVers(this);
			setTacheCourante(nvTache);
		}
	}
	
	/**
	 * @return un message disant que l'on a la position de la base ennemie
	 * */
	private WarMessage getMessageAboutEnemyBase() {
		for (WarMessage m : this.messages) {
			if(m.getMessage().equals(Constants.enemyBaseHere))
				return m;
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
			
			// On envoie un message aux autres explorer pour dire qu'il y a de la nourriture
			getBrain().broadcastMessageToAgentType(WarAgentType.WarExplorer, Constants.foodHere,
					String.valueOf(food.getDistance()), String.valueOf(food.getAngle()));
		}
	}
	
//	/**
//	 * @action évite les rockets 
//	 * */
//	private void eviterMissile(){
//		ArrayList<WarPercept> missiles = getBrain().getPerceptsEnemiesByType(WarAgentType.WarRocket);
//		if(missiles !=null && missiles.size()>0){
//			WarPercept missile = missiles.get(0);
//			getBrain().setHeading);
//		}
//	}
	
}