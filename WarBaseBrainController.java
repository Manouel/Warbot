package pepisha;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.agents.percepts.WarPercept;
import edu.turtlekit3.warbot.agents.resources.WarFood;
import edu.turtlekit3.warbot.brains.braincontrollers.WarBaseAbstractBrainController;
import edu.turtlekit3.warbot.communications.WarMessage;

public class WarBaseBrainController extends WarBaseAbstractBrainController 
{
	// Action de la base à retourner
	private String toReturn;
	
	//private WarAgentType lastCreateUnit = null;
	
	// Energie minimum pour créer un nouvel agent
	private static final int MIN_HEATH_TO_CREATE = (int) (WarBase.MAX_HEALTH * 0.8);
	
	private static final double RAYON_PERCEPTION_ENNEMIS = 150;
	
	
	/**
	 * Constructeur
	 */
	public WarBaseBrainController() {
		super();
	}


	/**
	 * @action Définit le comportement de la base
	 * @return Action à effectuer
	 */
	public String action()
	{
		toReturn = null;
		
		if (isAttacked())
			askRocketLaucherToComeBack();
		
		giveMyPosition();
		
		healMySelf();
		
		createUnit(WarAgentType.WarRocketLauncher);
		
		if(toReturn == null)
			toReturn = WarBase.ACTION_IDLE;
		
		return toReturn;
	}
	
	
	/**
	 * @action Teste s'il y a des lanceurs de missiles ennemis à proximité
	 * @return Vrai si un rocket launcher perçu, faux sinon
	 */
	private boolean isAttacked()
	{
		ArrayList<WarPercept> ennemies = getBrain().getPerceptsEnemiesByType(WarAgentType.WarRocketLauncher);
		
		for (WarPercept p : ennemies) {
			if (p.getDistance() <= RAYON_PERCEPTION_ENNEMIS)
			{
                getBrain().setDebugStringColor(Color.orange);
                getBrain().setDebugString("Je suis attaqué !!");
				return true;
			}
		}
		
        getBrain().setDebugStringColor(Color.black);
        getBrain().setDebugString("Tout va bien");
		
		return false;
	}
	
	
	/**
	 * @action Demande à l'ensemble des lanceurs de missiles
	 * 			de revenir à la base.
	 */
	private void askRocketLaucherToComeBack()
	{
		getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, 
												Constants.baseIsAttack, "");
	}

	
	/**
	 * Vérifie si le sac de la base n'est pas vide et si la base a besoin d'énergie
	 * et mange une portion de nourriture
	 */
	private void healMySelf()
	{
		if(toReturn != null)
			return;
		
		if(getBrain().isBagEmpty())
			return;
		
		if(getBrain().getHealth() <= WarBase.MAX_HEALTH - WarFood.HEALTH_GIVEN)
			toReturn = WarBase.ACTION_EAT;
	}

	
	/**
	 * @action Créé l'unité passée en paramètre 
	 * @param a1 Agent à créer
	 */
	private void createUnit(WarAgentType a1) 
	{
		if(toReturn != null)
			return;
		
		if(getBrain().getHealth() > MIN_HEATH_TO_CREATE)
		{
			getBrain().setNextAgentToCreate(a1);
			getBrain().setDebugString("Create: "+a1.name());
			
			toReturn = WarBase.ACTION_CREATE;
		}
	}

	
	/**
	 * @action Teste si des agents me demandent
	 * 			ma position et répond
	 */
	private void giveMyPosition()
	{
		ArrayList<WarMessage> msgs = getBrain().getMessages();
		
		for(WarMessage msg : msgs)
		{
			if (msg.getMessage().equals(Constants.whereAreYou)) {
				getBrain().sendMessage(msg.getSenderID(), Constants.here, "");
			}
		}	
	}
}
