package pepisha;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;






import pepisha.taches.TacheAgent;
import pepisha.taches.base.CreerUnite;
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
	private TacheAgent tacheCourante;//Tache courante
	
	private ArrayList<WarMessage> messages;
	
	//Nombre minimal d'agents de chaque type :
	private static int nbMinRocket = 2;
	private static int nbMinExplorer = 10;
	private static int nbMinEngineer=2;
	
	
	private Map<Integer,Integer> explorers;
	private Map<Integer,Integer> rocketLaunchers;
	private Map<Integer, Integer> engineers;
	
	/**
	 * Constructeur
	 */
	public WarBaseBrainController() {
		super();
		tacheCourante=new CreerUnite(this);
		explorers=new HashMap<Integer,Integer>();
		rocketLaunchers=new HashMap<Integer,Integer>();
		engineers=new HashMap<Integer,Integer>();
	}

	//Accesseurs -------------------------------------------------------------
	
	/**
	 * @action change le toReturn
	 * */
	public void setToReturn(String nvReturn){
		toReturn=nvReturn;
	}
	
	public int getNbMinRocket(){
		return nbMinRocket;
	}
	
	public int getNbMinExplorer(){
		return nbMinExplorer;
	}
	
	public int getNbMinEngineer(){
		return nbMinEngineer;
	}
	
	public int getNbEngineer(){
		return engineers.size();
	}
	
	public int getNbExplorer(){
		return explorers.size();
	}
	
	public int getNbRocketLauncher(){
		return rocketLaunchers.size();
	}
	
	//Méthodes ----------------------------------------------------------------
	/**
	 * @action Définit le comportement de la base
	 * @return Action à effectuer
	 */
	public String action()
	{
		toReturn = null;
		
		this.messages = getBrain().getMessages();
		
		doReflex();
		
        getBrain().setDebugStringColor(Color.black);
        getBrain().setDebugString(tacheCourante.toString());
		
		if(toReturn==null){
			tacheCourante.exec();
		}
		if(toReturn == null)
			toReturn = WarBase.ACTION_IDLE;
		
		return toReturn;
	}
	
	/**
	 * @action exécute les réflexes 
	 * */
	private void doReflex(){
		WarPercept p = isAttacked();
		
		if (p != null)
			askRocketLaucherToComeBack(p);
		
		giveMyPosition();
		healMySelf();
		verifierListesAgents();
		
	}
	
	/**
	 * @action Teste s'il y a des lanceurs de missiles ennemis à proximité
	 * @return Percept si ennemis, null sinon
	 */
	private WarPercept isAttacked()
	{
		ArrayList<WarPercept> rockets = getBrain().getPerceptsEnemiesByType(WarAgentType.WarRocketLauncher);
		
		for (WarPercept p : rockets) {
            getBrain().setDebugStringColor(Color.orange);
            getBrain().setDebugString("Je suis attaqué !!");
			return p;
		}
		
		ArrayList<WarPercept> tourelles = getBrain().getPerceptsEnemiesByType(WarAgentType.WarTurret);
		
		for (WarPercept p : tourelles) {
            getBrain().setDebugStringColor(Color.orange);
            getBrain().setDebugString("Je suis attaqué !!");
			return p;
		}
		
		return null;
	}
	
	
	/**
	 * @action Demande à l'ensemble des lanceurs de missiles
	 * 			de revenir à la base.
	 * @param p Point auquel les lanceurs de missiles doivent revenir.
	 */
	private void askRocketLaucherToComeBack(WarPercept p)
	{
        getBrain().setDebugStringColor(Color.orange);
        getBrain().setDebugString("Je suis attaqué !! (mess envoyé)");
        
        if (p != null) {
        	getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, 
												Constants.baseIsAttack, String.valueOf(p.getDistance()), String.valueOf(p.getAngle()));
        }
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
	 * @action Teste si des agents me demandent
	 * 			ma position et répond
	 */
	private void giveMyPosition()
	{		
		for(WarMessage msg : messages)
		{
			if (msg.getMessage().equals(Constants.whereAreYou)) {
				getBrain().sendMessage(msg.getSenderID(), Constants.here, "");
			}
		}	
	}
	
	/**
	 * @action vérifie dans ses messages si elle a un message disant qu'un agent est en vie.
	 * Si pas de nouvelle d'un agent pdt trois tours, considéré comme mort.
	 * */
	private void verifierListesAgents(){
		//Explorers ------------------------------------
		Set<Integer> listeCles = explorers.keySet();
		//Je décrémente tout
		for(Integer cle : listeCles){
			explorers.put(cle,explorers.get(cle)-1);
		}
		
		//Rocket Launcher ---------------------------------
		listeCles = rocketLaunchers.keySet();
		//Je décrémente tout
		for(Integer cle : listeCles){
			rocketLaunchers.put(cle,rocketLaunchers.get(cle)-1);
		}
		
		//Engineers ---------------------------------------
		listeCles = engineers.keySet();
		//Je décrémente tout
		for(Integer cle : listeCles){
			engineers.put(cle,engineers.get(cle)-1);
		}
		
		for(WarMessage msg : messages){
			if(msg.getMessage().equals(Constants.imAlive)){
				if(msg.getSenderType().equals(WarAgentType.WarExplorer)){
					explorers.put(msg.getSenderID(), 3);
				}
				if(msg.getSenderType().equals(WarAgentType.WarRocketLauncher)){
					rocketLaunchers.put(msg.getSenderID(), 3);
				}
				if(msg.getSenderType().equals(WarAgentType.WarEngineer)){
					engineers.put(msg.getSenderID(), 3);
				}
			}
		}
		
		
		verifierAgentsMorts();
	}
	
	/**
	 * @action vérifie si les agents sont morts (sont morts ssi value=0)
	 * */
	private void verifierAgentsMorts(){
		
		//Explorers ---------------------------------
		Iterator it = explorers.keySet().iterator();
		
		while (it.hasNext()){
			Integer cle = (Integer) it.next();
			
			if(explorers.get(cle)<=0){
				it.remove();
			}
		}
		
		//Rocket Launchers --------------------------
		it=rocketLaunchers.keySet().iterator();
		while (it.hasNext()){
			Integer cle = (Integer) it.next();
			
			if(rocketLaunchers.get(cle)<=0){
				it.remove();
			}
		}	
		
		//Engineers ----------------------------------
		it=engineers.keySet().iterator();
		while (it.hasNext()){
			Integer cle = (Integer) it.next();
			
			if(engineers.get(cle)<=0){
				it.remove();
			}
		}

	}
}

