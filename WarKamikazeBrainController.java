package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.kamikazes.SeSuicider;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarTurret;
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
	
	
	public WarKamikazeBrainController() {
		super();
		tacheCourante = new SeSuicider(this);
		vie = WarKamikaze.MAX_HEALTH;
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
		
		vie = getBrain().getHealth();
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
	 * @action Si le kamikaze est attaqué et 
	 */
	private void seDefendre() {
		
		// Si on va mourir
		if(getBrain().getHealth() < (WarKamikaze.MAX_HEALTH * 0.3) && perdVie()){
			
			// On tire sur l'ennemi perçu
			ArrayList<WarPercept> ennemis = getBrain().getPerceptsEnemies();
			if(getBrain().isReloaded() && ennemis != null && ennemis.size() > 0) {
				getBrain().setHeading(ennemis.get(0).getAngle());
				setToReturn(WarKamikaze.ACTION_FIRE);
			}
		}	
	}
}
