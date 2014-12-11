package pepisha.taches.base;

import java.awt.Color;

import edu.turtlekit3.warbot.agents.agents.WarBase;
import edu.turtlekit3.warbot.agents.enums.WarAgentType;
import edu.turtlekit3.warbot.brains.WarBrainController;
import pepisha.WarBaseBrainController;
import pepisha.taches.TacheAgent;

public class CreerUnite extends TacheAgent{
	
	
	// Energie minimum pour créer un nouvel agent
	private static final int MIN_HEATH_TO_CREATE = (int) (WarBase.MAX_HEALTH * 0.8);
	
	//type du dernier agent créé
	private static WarAgentType lastCreatedUnit=WarAgentType.WarExplorer;
	
	
	public CreerUnite(WarBrainController b){
		super((WarBaseBrainController)b);
	}

	@Override
	public void exec() {
		WarBaseBrainController base=(WarBaseBrainController)typeAgent;
		if(base.getBrain().getHealth()>MIN_HEATH_TO_CREATE)
		{
			
			//si nbExplorers<nbMinExplorers
			if(base.getNbExplorer()<base.getNbMinExplorer()){
				lastCreatedUnit=WarAgentType.WarExplorer;
			}
			//Sinon, si nbEngineer =0
			else if(base.getNbEngineer()<base.getNbMinEngineer()){
				lastCreatedUnit=WarAgentType.WarEngineer;
			}
			//Sinon, si nbRocket<nbMinRocket
			else if(base.getNbRocketLauncher()<base.getNbMinRocket() 
					&& !(lastCreatedUnit.equals(WarAgentType.WarRocketLauncher))){
				lastCreatedUnit=WarAgentType.WarRocketLauncher;
			}
			else if(base.getNbKamikaze()<base.getNbMinKamikazes() 
					&& !(lastCreatedUnit.equals(WarAgentType.WarKamikaze))){
				lastCreatedUnit=WarAgentType.WarKamikaze;
			}
			
			else {
				//si nbAgents > nbDeuxiemeEngineer
				if(base.getNbTotalAgents()>base.getNbDeuxiemeEngineer()
						&& base.getNbMaxEngineer()>base.getNbEngineer()){
					base.getBrain().setDebugStringColor(Color.green);
					lastCreatedUnit=WarAgentType.WarEngineer;
				}
				//Si nbExplorers<nbMaxExplorers
				else{ if(base.getNbExplorer()<base.getNbMaxExplorer()){
						//Si lastcreatedunit=kamikaze
						if(lastCreatedUnit.equals(WarAgentType.WarKamikaze)){
							lastCreatedUnit=WarAgentType.WarExplorer;
						}
						//Sinon si lastcreatedunit=explorers
						else if(lastCreatedUnit.equals(WarAgentType.WarExplorer)){
							lastCreatedUnit=WarAgentType.WarRocketLauncher;
						}
						//Sinon
						else {
							lastCreatedUnit=WarAgentType.WarKamikaze;
						}
					
					}
					else {
						//Sinon si lastcreatedunit=kamikaze
						if(lastCreatedUnit.equals(WarAgentType.WarKamikaze)){
							lastCreatedUnit=WarAgentType.WarRocketLauncher;
						}
						else{
							lastCreatedUnit=WarAgentType.WarKamikaze;
						}
					}
				}
			
			}
			
			base.getBrain().setNextAgentToCreate(lastCreatedUnit);
			base.setToReturn(WarBase.ACTION_CREATE);
		}
	}

	@Override
	public String toString() {
		return "Tache Creer Unite "+lastCreatedUnit.toString();
	}
	
	
}
