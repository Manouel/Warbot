package pepisha;

import java.awt.Color;
import java.util.ArrayList;

import pepisha.taches.TacheAgent;
import pepisha.taches.engineer.CreerTourelle;
import edu.turtlekit3.warbot.agents.ControllableWarAgent;
import edu.turtlekit3.warbot.agents.MovableWarAgent;
import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.agents.WarExplorer;
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
	
	
	public WarEngineerBrainController() {
		super();
		tacheCourante = new CreerTourelle(this);
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
			if (getBrain().isBlocked())
				getBrain().setRandomHeading();
	
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
		
		if (toReturn == null) {
			getFood();
		}
	}
	
	private void eatFood()
	{
		if (!getBrain().isBagEmpty()) {
			toReturn = WarEngineer.ACTION_EAT;
		}
	}
	
	private void wiggle()
	{
		getBrain().setRandomHeading(20);
	}
	
	private void getFood()
	{	
		ArrayList<WarPercept> nourriture = getBrain().getPerceptsResources();
		
		// Si il y a de la nourriture dans notre champ de vision
		if(nourriture != null && nourriture.size() > 0)
		{			
			WarPercept lePlusProche = nourriture.get(0); // le 0 est le plus proche normalement
			
			if(lePlusProche.getDistance() <= ControllableWarAgent.MAX_DISTANCE_GIVE) {
				toReturn = MovableWarAgent.ACTION_TAKE;
			} else {
				getBrain().setHeading(lePlusProche.getAngle());
			}
		}
		else
		{
			wiggle();
		}
	}
}
