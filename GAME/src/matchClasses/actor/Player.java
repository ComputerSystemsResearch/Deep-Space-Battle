   package GAME.src.matchClasses.actor;

   import java.awt.Dimension;
   import java.awt.geom.Point2D;
   import java.awt.event.KeyEvent;

   import GAME.src.state.gameplay.MatchConstants;
   import GAME.src.matchClasses.Vector2D;
   import GAME.src.matchClasses.object.Hitbox;

   public class Player{
   
   // Character character
      private Hitbox hitbox;
      private int playerNum, KOs, livesLost, jumpNumber, damagePercentage, screenOrder;
      private int[] playerKeyCodes;
   // 0 - UP
   // 1 - DOWN
   // 2 - LEFT
   // 3 - RIGHT
   // 4 - BLOCK
   // 5 - PRIMARY
   // 6 - SECONDARY
      
   	//Cooldown Booleans
      private boolean jumpCD, primaryCD, secondaryCD, stunCD;
      private long jumpT, primaryT, secondaryT, stunT;
   
      private Vector2D characterSize;  // units
   
      private Vector2D pos_left_feet; // units
      private Vector2D vel_total;      // units/s
    //private Vector2D acc_walking;    // units/s/s
      //private Vector2D acc_total;      // units/s/s
   
      private double vel_max_running;  //(possibly) read from Chosen Character's stats
      private double acc_running;      //^^^ same
      private double acc_friction;     //^^^ same
      private double acc_gravity;
   
      public enum PlayerDirection { FACING_LEFT, FACING_RIGHT };
      public enum PlayerActivity { IDLE, DUCK, BLOCK, PRIMARY, SECONDARY, STUNNED };
      public enum PlayerVerticalMotion { NONE, UP, DOWN };
      public enum PlayerHorizontalMotion { NONE, LEFT, RIGHT };
      public enum PlayerLifeState { ALIVE, DEAD, RESPAWNING };
   
      private PlayerDirection facing;
      private PlayerActivity activity;
      private PlayerVerticalMotion yMotion;
      private PlayerHorizontalMotion xMotion;
      private PlayerLifeState lifeStatus;
   
   //Default Constructor
      public Player ( int playerNumber, Vector2D characterSize ){
         
         playerNum = playerNumber;
         KOs = livesLost = jumpNumber = damagePercentage = screenOrder = 0;
      	
         jumpCD = primaryCD = secondaryCD = stunCD = false;
         jumpT = primaryT = secondaryT = stunT = 0L;
      
         this.characterSize = characterSize;
            	
         pos_left_feet = new Vector2D( 0.0, 0.0 );
         vel_total = new Vector2D( 0.0, 0.0 );
         //acc_total = new Vector2D( 0.0, 0.0 );
      
         vel_max_running = MatchConstants.MAX_RUNNING_SPEED;
         acc_running = MatchConstants.RUNNING_ACCELERATION;
         acc_friction = MatchConstants.FRICTION;
         acc_gravity = MatchConstants.GRAVITY;
        
         if( playerNum == 1 ){
            facing = PlayerDirection.FACING_RIGHT;
            playerKeyCodes = MatchConstants.P1_KEY_CODES;
         //...
         //Set player spawn location, given by map class
         }
         else if( playerNum == 2 ){
            facing = PlayerDirection.FACING_LEFT;
            playerKeyCodes = MatchConstants.P2_KEY_CODES;
         //...
         //Set player spawn location, given by map class
         }
         
         //hitbox = new Hitbox( new Vector2D( pos_left_feet.getX()-(characterSize.getX()/2.0), pos_left_feet.getY() ), 
         //                     new Vector2D( pos_left_feet.getX()+(characterSize.getX()/2.0), pos_left_feet.getY() + characterSize.getY() ));
      	
         activity = PlayerActivity.IDLE;
         xMotion = PlayerHorizontalMotion.NONE;
         yMotion = PlayerVerticalMotion.NONE;
         lifeStatus = PlayerLifeState.ALIVE;
      
      }
   
   //public Player ( int playerNumber, int characterID ){}
   
   
   //GET METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      public int[] getPlayerKeyCodes(){
         return playerKeyCodes;
      }
      public int getPlayerNumber(){
         return playerNum;
      }
      public PlayerDirection getDirection(){
         return facing;
      }
      public PlayerActivity getActivity(){
         return activity;
      }
      public PlayerHorizontalMotion getHorizontalMotion(){
         return xMotion;
      }
      public PlayerVerticalMotion getVerticalMotion(){
         return yMotion;
      }
      public PlayerLifeState getLifeState(){
         return lifeStatus;
      }
   	
      public double width(){
         return characterSize.getX();
      }
      public double height(){
         return characterSize.getY();
      }
   	
      public int getLivesLost(){
         return livesLost;
      }
      public int getKOs(){
         return KOs;   
      }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   //SET METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
      public void setDirection( PlayerDirection direction ){ 
         facing = direction;
      }
      public void setActivity( PlayerActivity activity ){ 
         this.activity = activity;
      }
      public void setHorizontalMotion( PlayerHorizontalMotion xMotion ){ 
         this.xMotion = xMotion;
      }
      public void setVerticalMotion( PlayerVerticalMotion yMotion ){ 
         this.yMotion = yMotion;
      }
      public void setLifeState( PlayerLifeState lifeState){ 
         lifeStatus = lifeState;
      }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   //PLAYER CONTROL
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   //POSITION
      public Vector2D getPos(){
         return pos_left_feet;
      }
      public void setPos( double xComp, double yComp ){
         pos_left_feet.set( xComp, yComp );
      }
   
   //VELOCITY	
      public Vector2D getVel(){
         return vel_total;
      } 	
      public void setVel( double xComp, double yComp ){
         vel_total.set( xComp, yComp );
      }
   
   //ACCELERATION
      /*public Vector2D getAcc(){
         return acc_total;
      }
      public void setAcc( double xComp, double yComp ){
         acc_total.set( xComp, yComp );
      }*/
      
   	
   //This is where the action happens
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      public void doMove( boolean[] keysDown ){
      
      // - Check for keysDown
      // - Move Player/ Do Corresponding Actions
      // - Update Player's States
         
      	//Assess Cooldowns
         if( System.nanoTime() > jumpT + MatchConstants.JUMP_COOLDOWN ){
            jumpCD = false;
         }
      	
      	
         if( activity == PlayerActivity.STUNNED ){
               //if stunned
         
         }
         else{
         
            //do inputs   
            
         	//Horizontal Motion Commands
            if( !( keysDown[3] && keysDown[2])){ //NOT RIGHT and LEFT
               //RIGHT ONLY
               if ( keysDown[3]){ 
                  moveRight();
               }
               //LEFT ONLY
               else if( keysDown[2]){
                  moveLeft();
               }
               else{ // need to check if player is in contact with a surface first
                  slide(); 
               }
            }
            
            //Vertical Motion Commands
         	
         	//UP ONLY   	
            if( keysDown[0] && !jumpCD){
               jump();
            }
            //DOWN ONLY
            else if( keysDown[1] ){
               if( this.getVerticalMotion() == PlayerVerticalMotion.NONE )
                  duck();
               else
                  dive();  
            }
            else{
               //FALL
               vel_total.deltaY( -1*MatchConstants.GRAVITY );
            }
         	
         	
            
         }
      	
      	//Update Player's Position
         pos_left_feet.deltaX( vel_total.getX() );
         pos_left_feet.deltaY( vel_total.getY() );
      	
      
      // - Check for Collisions -> Do Collisions
      //   -> Including Colisions with map and map boundaries
      
         
      
      // - Move Player After Collisions (if necessary)
      
         
      
      // - Update Player's States
         if( vel_total.getY() < 0 ){
            yMotion = PlayerVerticalMotion.DOWN;
         }
         else if( vel_total.getY() == 0 ){
            yMotion = PlayerVerticalMotion.NONE;
         }
      	
      	
      	
      }	 
   
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      
      //ACTIONS
      public void moveRight(){
         if( vel_total.getX() >= vel_max_running )
            vel_total.setX( vel_max_running );
         else
            vel_total.deltaX( acc_running );
      }
   	
      public void moveLeft(){
         if( vel_total.getX() <= -1*vel_max_running )
            vel_total.setX( -1*vel_max_running );
         else
            vel_total.deltaX( -1* acc_running );
      }
   	
      public void slide(){
         if( vel_total.getX() > 0 ){
            if( vel_total.getX() <= Math.abs(acc_friction) || (vel_total.getX() - acc_friction )<= 0)
               vel_total.setX( 0 );
            else
               vel_total.deltaX( -1*acc_friction );
         }
         else if( vel_total.getX() < 0 ){
            if( vel_total.getX() >= acc_friction )
               vel_total.setX( 0 );
            else
               vel_total.deltaX( acc_friction );
         }
      }
   	
      public void stun(){
      }
   	
      public void jump(){
         jumpCD = true;
         jumpT = System.nanoTime();
         jumpNumber++;
         System.out.println("P"+playerNum+": JUMP#: " + jumpNumber);
         vel_total.setY( 7.5 / MatchConstants.TICKS_PER_SECOND );
         yMotion = PlayerVerticalMotion.UP;
      }
   	
      public void dive(){
      }
   	
      public void duck(){ // may need a stand method to reset Hitbox dimensions
      }
   	
      public void primaryAttack(){
      }
   	
      public void secondaryAttack(){
      }
   	
      public void block(){
      }
   	
   	//HIGHER SCOPE
      public void die(){
         lifeStatus = PlayerLifeState.DEAD;
         livesLost++;
      }
      public void respawn(){
         
         lifeStatus = PlayerLifeState.RESPAWNING;
         activity = PlayerActivity.IDLE;
         xMotion = PlayerHorizontalMotion.NONE;
         yMotion = PlayerVerticalMotion.NONE;
      	
         jumpCD = primaryCD = secondaryCD = stunCD = false;
         jumpT = primaryT = secondaryT = stunT = 0L;
         jumpNumber = 0;
      	
         if( playerNum == 1 )
            pos_left_feet.set( -8.0, 4.5 );
         else if( playerNum == 2 )
            pos_left_feet.set( 8.0, 4.5);
         vel_total.set( 0.0, 0.0 );
      }
   
   }