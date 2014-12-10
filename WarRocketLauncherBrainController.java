package pepisha;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import pepisha.taches.TacheAgent;
import pepisha.taches.rocketLauncher.ChercherEnnemi;
import pepisha.taches.rocketLauncher.SeDirigerVers;

import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.agents.projectiles.WarRocket;
import edu.turtlekit3.warbot.brains.braincontrollers.WarRocketLauncherAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;
import edu.turtlekit3.warbot.teams.demo.WarKamikazeBrainController;
import edu.turtlekit3.warbot.tools.CoordPolar;

public class WarRocketLauncherBrainController extends WarRocketLauncherAbstractBrainController {

	private String toReturn = null;
	boolean iAbleToFireBase = false;
	private TacheAgent tacheCourante;//Tache courante
	
	private boolean seDirigerVersPoint=false; //Si true, on se dirige vers le pt poinOuAller
	private double distancePointOuAller;
	
	private boolean baseEnnemie=false; //Si on a une base ennemie connue. 
	
	private static final double rayonAttaqueBaseEnnemie = 500;
	private static final double rayonDefenseBase = 500;
	
	private static final double rayonAttaqueEnnemi = 400;//Rayon dans lequel on attaque les ennemis
	

	ArrayList<WarMessage> messages;
	
	public WarRocketLauncherBrainController() {
		super();
		tacheCourante=new ChercherEnnemi(this);
	}
	
	//Accesseurs ------------------------------------------------------
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
	

	//Méthodes -----------------------------------------------------
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
		double distance =isBaseAttacked() ;
		if(distance!=0 && distance<=rayonDefenseBase){
				this.setSeDirigerVersUnPoint(true);
				this.setDistancePointOuAller(distance);
				SeDirigerVers nvTache=new SeDirigerVers(this);
				this.setTacheCourante(nvTache);
		}
		
	}
	
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
	private double isBaseAttacked(){
		for(WarMessage m : this.messages){
			if(m.getMessage().equals(Constants.baseIsAttack)){	
				this.setDistancePointOuAller(m.getDistance());
				

				return m.getDistance();
			}
		}
			return 0;
		
		/*ArrayList<WarMessage> mess = getBrain().getMessages();
		for(WarMessage m : mess){
			this.getBrain().setDebugStringColor(Color.red);
			this.getBrain().setDebugString("PASSE LA!! ");
			if(m.getMessage().equals(Constants.baseIsAttack)){
				
				this.getBrain().setDebugString("! ");
				this.setDistancePointOuAller(m.getDistance());
				this.setSeDirigerVersUnPoint(true);
				return true;
			}
		}
		return false;*/
		
	}


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
//	private void handleMessages() {
//		for (WarMessage m : this.messages) {
//			if(m.getSenderType().equals(WarAgentType.WarKamikaze) && m.getMessage().equals(WarKamikazeBrainController.I_Exist))
//				this.iAbleToFireBase = true;
//		}
//	}
}