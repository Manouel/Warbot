package pepisha;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

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
	
	// Distance de la nourriture indiquée par d'autres agents
	private double distance = 0.0;
	
	// Vrai si en train de vider sac
	private boolean imGiving = false;
	
	
	/**
	 * Constructeur
	 */
	public WarExplorerBrainController() {
		super();
	}
	
	
	/**
	 * @action Définit le comportement de l'explorer
	 * @return Action à effectuer (move,take etc...)
	 */
	public String action() 
	{	
		toReturn = null;
		
		perceptEnemyBase();
		
		getFood();
		
		returnFood();
		
		
		if(toReturn == null) {
			if (getBrain().isBlocked())
				getBrain().setRandomHeading();
			
			return WarExplorer.ACTION_MOVE;
		} 
		else {
			return toReturn;
		}
	}
	
	
	/***** Ne sert plus à rien pour l'instant *******/
	/**
	 * @action Déplace l'agent aléatoirement
	 * @param range Rayon de déplacement passé à setRandomHeading
	 */
	private void moveRandomly(int range)
	{
		if (getBrain().isBlocked())
			getBrain().setRandomHeading();
		else
			getBrain().setRandomHeading(range);
		
		toReturn = MovableWarAgent.ACTION_MOVE;
	}
	
	
	/**
	 * @action Récupère les messages reçus provenants des bases
	 */
	private WarMessage getMessageFromBase()
	{
		for (WarMessage m : getBrain().getMessages())
		{
			if(m.getSenderType().equals(WarAgentType.WarBase))
				return m;
		}
		
		getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.whereAreYou, "");
		return null;
	}
	
	
	/**
	 * @action Récupère un message à propos de la nourriture s'il y en a
	 * @return Message food si trouvé, sinon null
	 */
	private WarMessage getMessageAboutFood()
	{
		for (WarMessage m : getBrain().getMessages())
		{
			if(m.getMessage().equals("foodHere"))
				return m;
		}
		
		return null;
	}
	
	
	/**
	 * @action Cherche et ramasse de la nourriture
	 */
	private void getFood()
	{
		// Si mon sac est plein je ne fais rien (je vais passer à la méthode returnFood)
		if (getBrain().isBagFull())
		{
			imGiving = true;
			return;
		}
		
		// Si je suis en train de vider mon sac, je passe à la méthode returnFood
		if(imGiving)
			return;
		
		if (getBrain().isBlocked())
			getBrain().setRandomHeading();
			
		ArrayList<WarPercept> nourriture = getBrain().getPerceptsResources();
		
		// Si il y a de la nourriture dans notre champ de vision
		if(nourriture != null && nourriture.size() > 0)
		{			
			WarPercept lePlusProche = nourriture.get(0); // le 0 est le plus proche normalement
			
			if(lePlusProche.getDistance() <= ControllableWarAgent.MAX_DISTANCE_GIVE) {
				// On envoie un message aux autres explorer pour dire qu'il y a de la nourriture
				getBrain().broadcastMessageToAgentType(WarAgentType.WarExplorer, Constants.foodHere, "");
				toReturn = MovableWarAgent.ACTION_TAKE;
			} else {
				getBrain().setHeading(lePlusProche.getAngle());
				toReturn = MovableWarAgent.ACTION_MOVE;
			}
			
			distance = 0.0;
		}
		else 		// Pas de nourriture dans notre champ de vision
		{
			// Pas de message reçu
			if (distance <= 0.0) {

				// Je regarde si j'ai reçu un message qui dit qu'il y a de la nourriture
				WarMessage food = getMessageAboutFood();
				
				if (food != null) {
					getBrain().setDebugStringColor(Color.BLUE);
					getBrain().setDebugString("Reçu message nourriture");
					
					distance = food.getDistance();
					getBrain().setHeading(food.getAngle());
				}
				else {
					getBrain().setDebugStringColor(Color.BLACK);
					getBrain().setDebugString("Searching food");
					getBrain().setRandomHeading(40);
				}
			}
			else {
				distance--;
			}
			
			toReturn = MovableWarAgent.ACTION_MOVE;
		}
	}
	
	
	/**
	 * @action Ramener la nourriture à la base
	 */
	private void returnFood()
	{
		// Si le sac n'est pas plein on arrête la méthode
		if(!imGiving)
			return;
		
		// Si mon sac est vide, je passe à la méthode returnFood
		if(getBrain().isBagEmpty())
		{
			imGiving = false;
			getBrain().setHeading(getBrain().getHeading() + 180);
			return;
		}
		
		getBrain().setDebugStringColor(Color.green);
		getBrain().setDebugString("Retour à la base... Sac plein");
		
		if(getBrain().isBlocked())
			getBrain().setRandomHeading();
		
		
		// On récupère la liste des bases alliées qui sont dans les environs
		ArrayList<WarPercept> bases = getBrain().getPerceptsAlliesByType(WarAgentType.WarBase);
		
		// Si pas de base dans champ de vision
		if (bases == null | bases.size() == 0)
		{
			WarMessage m = getMessageFromBase();
			
			if (m != null)
				getBrain().setHeading(m.getAngle());
			
			// J'envoie un message aux bases pour savoir où elles sont
			getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.whereAreYou, (String[]) null);
			
			toReturn = MovableWarAgent.ACTION_MOVE;
		}
		else		// On voit une base
		{
			WarPercept base = bases.get(0);
			
			// Si la base est assez proche pour qu'on lui donne
			if (base.getDistance() <= MovableWarAgent.MAX_DISTANCE_GIVE) {
				getBrain().setIdNextAgentToGive(base.getID());
				toReturn = MovableWarAgent.ACTION_GIVE;
			}
			else {		// Si elle est trop loin, on se dirige vers elle
				getBrain().setHeading(base.getAngle());
				toReturn = MovableWarAgent.ACTION_MOVE;
			}
		}
	}
	
	
	/**
	 * @action Envoie un message à sa base s'il perçoit la base ennemie
	 */
	private void perceptEnemyBase()
	{
		ArrayList<WarPercept> basesEnnemies = getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
		
		if (basesEnnemies != null && basesEnnemies.size() > 0)
		{
	        getBrain().setDebugStringColor(Color.blue);
	        getBrain().setDebugString("Envoi position base ennemie");
			
			// On envoie aux bases la position de la base ennemie
			getBrain().broadcastMessageToAgentType(WarAgentType.WarBase, Constants.enemyBaseHere, (String[]) null);
			getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.enemyBaseHere, (String[]) null);
		}
	}
}
