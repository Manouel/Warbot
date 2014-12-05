
package pepisha;
 
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
 
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
     
    boolean baseIsAttacked=false;
    private double distanceBaseAttaquee = 0.0; //Distance de la base attaquée
    private double distanceBaseAAttaquer=0.0; //distance de la base à attaquer
 
    private static final double RAYON_PROTECTION_BASE=150;
    private static final double RAYON_ATTAQUE_BASE_ENNEMIE=350;
     
 
    ArrayList<WarMessage> messages;
     
    public WarRocketLauncherBrainController() {
        super();
    }
     
    @Override
    public String action() {
        // Develop behaviour here
         
        toReturn = null;
        this.messages = getBrain().getMessages();
        isBaseAttacked();
        baseEnnemyFound();
         
        if(!(getBrain().getPerceptsAlliesByType(WarAgentType.WarBase).isEmpty())){
            distanceBaseAttaquee=0.0;
            baseIsAttacked=false;
        }
        if(baseIsAttacked && distanceBaseAttaquee>0){
            toReturn=WarRocketLauncher.ACTION_MOVE;
            distanceBaseAttaquee--;
        }
         
        if(distanceBaseAAttaquer>0){
            ArrayList<WarPercept> basesEnnemies=getBrain().getPerceptsEnemiesByType(WarAgentType.WarBase);
            if(basesEnnemies!=null && basesEnnemies.size()>0){
                WarPercept baseEnnemie=basesEnnemies.get(0);
                getBrain().setHeading(baseEnnemie.getAngle());
                
                
                if(baseEnnemie.getDistance()>WarRocket.EXPLOSION_RADIUS+1){
                    toReturn=WarRocketLauncher.ACTION_MOVE;
                }
                else{
                    if(getBrain().isReloaded()){
                        getBrain().setDebugStringColor(Color.pink);
                        getBrain().setDebugString("Fire base !!!!!");
                        getBrain().setHeading(baseEnnemie.getAngle());
                        toReturn=WarRocketLauncher.ACTION_FIRE;
                    }
                    else if(!getBrain().isReloading()){
                        toReturn=WarRocketLauncher.ACTION_RELOAD;
                    }
                }
            }
            else{
                distanceBaseAAttaquer--;
            }
        }
 
        //handleMessages();
         
//       if(iAbleToFireBase)
//           attaquerBase();
         
         
         
        if(!iAbleToFireBase)
            attackRocketLaunchers();
         
        wiggle();
         
        if(toReturn == null){
            if (getBrain().isBlocked())
                getBrain().setRandomHeading();
            toReturn = WarRocketLauncher.ACTION_MOVE;
        }
         
        return toReturn;
    }
     
    /**
     * @action poney
     * */
    private void attackRocketLaunchers() {
        //Si j'ai dejà une action à faire,
        //Je quitte direct la fonction
        if(toReturn != null)
            return;
        //Si j'ai pas encore rechargé et que je suis pas en train de recharger, je recharge
        if(!getBrain().isReloaded() && !getBrain().isReloading()){
            toReturn =  WarRocketLauncher.ACTION_RELOAD;
            return;
        }
         
        getBrain().setDebugStringColor(Color.blue);
        getBrain().setDebugString("Attack launchers");
         
        ArrayList<WarPercept> percept = getBrain().getPerceptsEnemiesByType(WarAgentType.WarRocketLauncher);
         
        // Si ds mon champ de perception je vois un WarRocket ennemy, je l'attaque !!
        if(percept != null && percept.size() > 0){
             
            //je le dit aux autres
            getBrain().broadcastMessageToAgentType(WarAgentType.WarRocketLauncher, Constants.enemyTankHere, String.valueOf(percept.get(0).getDistance()), String.valueOf(percept.get(0).getAngle()));
             
            //Si j'ai rechargé, je me tourne vers les ennemis et je tire
            if(getBrain().isReloaded()){
                 
                getBrain().setHeading(percept.get(0).getAngle());
                toReturn = WarRocketLauncher.ACTION_FIRE;
            }else{
                 
                //si je suis pas assez pres de l'enemy je m'approche
                 
                if(percept.get(0).getDistance() > WarRocket.EXPLOSION_RADIUS + 1)
                    toReturn = WarRocketLauncher.ACTION_MOVE;
                else
                    toReturn = WarRocketLauncher.ACTION_IDLE;
            }
        }else{
            //si j'ai un message me disant qu'il y a  un autre tank a tuer
             
            WarMessage m = getFormatedMessageAboutEnemyTankToKill();
            if(m != null){
                CoordPolar p = getBrain().getIndirectPositionOfAgentWithMessage(m);
                getBrain().setHeading(p.getAngle());
                toReturn = WarRocketLauncher.ACTION_MOVE;
            }
        }      
    }
     
    private void wiggle() {
        if(toReturn != null)
            return;
         
        if(getBrain().isBlocked())
            getBrain().setRandomHeading();
         
        getBrain().setDebugStringColor(Color.black);
        getBrain().setDebugString("Looking for ennemies");
         
        double angle = getBrain().getHeading() + new Random().nextInt(10) - new Random().nextInt(10);
         
        getBrain().setHeading(angle);
     
        toReturn = MovableWarAgent.ACTION_MOVE;    
    }
 
    private WarMessage getFormatedMessageAboutEnemyTankToKill() {
        for (WarMessage m : this.messages) {
            if(m.getMessage().equals(Constants.enemyTankHere) && m.getContent() != null && m.getContent().length == 2){
                return m;
            }
        }
        return null;
    }
 
    /**
     * @action permet de regarder si on a reçu un message contenant une position de base ennemie
     * */
    private WarMessage getMessageAboutEnemyBase() {
        for (WarMessage m : this.messages) {
            if(m.getMessage().equals(Constants.enemyBaseHere))
                return m;
        }
        return null;
    }
     
    /**
     * @action regarde si on a reçu un message comme quoi la base est attaquée
     * */
    private void isBaseAttacked(){
        for(WarMessage m : this.messages){
            if(m.getMessage().equals(Constants.baseIsAttack))
            {
                if(m.getDistance()<=RAYON_PROTECTION_BASE){
                     
                    getBrain().setDebugStringColor(Color.red);
                    getBrain().setDebugString("Base alliée attaquée !!");
                    getBrain().setHeading(m.getAngle());
                    distanceBaseAttaquee=m.getDistance();
                    baseIsAttacked=true;
                    break;
                }
            }
        }
    }
    /**
     * */
    private void baseEnnemyFound(){
        for(WarMessage m :this.messages){
            if(m.getMessage().equals(Constants.enemyBaseHere)){
                if(m.getDistance()<=RAYON_ATTAQUE_BASE_ENNEMIE){
                    getBrain().setDebugStringColor(Color.green);
                    getBrain().setDebugString("Base ennemie trouvée.. Attaque !");
                    getBrain().setHeading(m.getAngle());
                    distanceBaseAAttaquer=m.getDistance();
                     
                	System.out.println("Distance base à attaquer : "+distanceBaseAAttaquer);
                }
            }
        }
    }
 
 
//  private void handleMessages() {
//      for (WarMessage m : this.messages) {
//          if(m.getSenderType().equals(WarAgentType.WarKamikaze) && m.getMessage().equals(WarKamikazeBrainController.I_Exist))
//              this.iAbleToFireBase = true;
//      }
//  }
}
