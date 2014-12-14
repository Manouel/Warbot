package pepisha;

import edu.turtlekit3.warbot.agents.agents.WarEngineer;
import edu.turtlekit3.warbot.agents.agents.WarKamikaze;
import edu.turtlekit3.warbot.agents.agents.WarRocketLauncher;

public class Constants {
	public final static String whereAreYou="whereAreYou?";
	public final static String here="here";

	public static final String enemyBaseHere = "enemyBaseHere";
	public static final String enemyTankHere = "enemyTankHere";
	public static final String ennemyHere = "ennemyHere";
	
	public static final String foodHere = "foodHere";
	public static final String imAlive = "imAlive";
	
	// Constantes base
	
	public static final String baseIsAttack = "baseIsAttack";
	public static final String noEspion ="noEspion";
	
	// Constantes rockets
	
	public static final double rayonAttaqueBaseEnnemie = 500;
	public static final double rayonDefenseBase = 500;
	public static final double rayonAttaqueEnnemi = 400;//Rayon dans lequel on attaque les ennemis
	
	// Constantes kamikazes
	
	public static final int vieMaxAvantSuicide = (int) (WarKamikaze.MAX_HEALTH * 0.3);	// S'il est attaqué et en dessous du seuil
	public static final int DISTANCE_MAX_KAMIKAZE_ATTAQUE_BASE = 1000;
	public static final int NB_MIN_ROCKETS_TO_KILL = 3;
	public static final int NB_MIN_TURRET_TO_KILL = 2;
	
	//Nombre minimal d'agents de chaque type :
	public static final int nbMinRocket=5;
	public static final int nbMinExplorer=2;
	public static final int nbMinEngineer=1;
	public static final int nbMinKamikazes=3;
	
	//NbMax de chaque type :
	public static final int nbMaxExplorer=5;
	public static final int nbMaxEngineer=2;
	
	//nb total d'agents à partir duquel on fait un deuxième engineer
	public static final int nbDeuxiemeEngineer=10;
	
	//Constantes turrets
	public static final int RAYON_NON_ATTAQUE_TURRET=17;
	
	// Constantes ingénieurs	
	public static final int DISTANCE_MAX_INGENIEUR_ATTAQUE_BASE = 1000;
}



