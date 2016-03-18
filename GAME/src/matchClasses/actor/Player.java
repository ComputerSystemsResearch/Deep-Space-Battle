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
      private int playerNum, KOs, livesLost, jumpNumber, stock, damagePercentage, screenOrder;
      private int[] playerKeyCodes;
   	// 0 - UP
   	// 1 - DOWN
   	// 2 - LEFT
   	// 3 - RIGHT
   	// 4 - BLOCK
   	// 5 - PRIMARY
   	// 6 - SECONDARY
   	
      private Vector2D characterSize;       // units
		
      private Point2D position;             // units
      private Vector2D totalVelocity;       // units/s
      private Vector2D totalAcceleration;   // units/s/s
   	
      private double maximumRunningSpeed;   //(possibly) read from Chosen Character's stats
      private double runningAcceleration;   //^^^ same
      private double slidingDecceleration;  //^^^ same
   
      private enum PlayerDirection { FACING_LEFT, FACING_RIGHT };
      //private enum PlayerActivity { IDLE, DUCK, DIVE, BLOCK, PRIMARY, SECONDARY };
      //private enum PlayerVerticalMotion { NONE, UP, DOWN };
      //private enum PlayerHorizontalMotion { NONE, LEFT, RIGHT };
      private enum PlayerLifeState { ALIVE, DEAD, RESPAWNING };
   
      private PlayerDirection facing;
      //private PlayerActivity activity;
      //private PlayerHorizontalMotion xMotion;
      //private PlayerVerticalMotion yMotion;
      private PlayerLifeState lifeStatus;
   
   //Default Constructor
      public Player ( int playerNumber ){
      
         playerNum = playerNumber;
         KOs = 0;
         livesLost = 0;
         jumpNumber = 0;
         stock = 5;
         damagePercentage = 0;
         screenOrder = 0;
         
         characterSize = new Vector2D( 1.0, 2.0 );
			      	
         position = new Point2D.Double( 0.0, 0.0 );
         totalVelocity = new Vector2D( 0.0, 0.0 );
         totalAcceleration = new Vector2D( 0.0, 0.0 );
      	
         maximumRunningSpeed = 10.0/MatchConstants.TICKS_PER_SECOND;        //   10  units/second
         runningAcceleration = 5.0/(MatchConstants.TICKS_PER_SECOND*3);     //  5/3  units/s/s 
         slidingDecceleration = -25.0/(MatchConstants.TICKS_PER_SECOND*6);  //-25/6  units/s/s
      	
         if( playerNum == 1 ){
            facing = PlayerDirection.FACING_RIGHT;
            playerKeyCodes = MatchConstants.P1_KEY_CODES;
            //...
         }
         else if( playerNum == 2 ){
            facing = PlayerDirection.FACING_LEFT;
            playerKeyCodes = MatchConstants.P2_KEY_CODES;
         	//...
         }
      
         //activity = PlayerActivity.IDLE;
         //xMotion = PlayerHorizontalMotion.NONE;
         //yMotion = PlayerVerticalMotion.NONE;
         //lifeStatus = PlayerLifeState.ALIVE;
      
      }
   
   
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
      /*
      public PlayerActivity getActivity(){
         return activity;
      }
   
      public PlayerHorizontalMotion getHorizontalMotion(){
         return xMotion;
      }
   
      public PlayerVerticalMotion getVerticalMotion(){
         return yMotion;
      }
      */
      public PlayerLifeState getLifeState(){
         return lifeStatus;
      }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   //SET METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
      public void setDirection( PlayerDirection direction ){ 
         facing = direction;
      }
      /*
      public void setActivity( PlayerActivity activity ){ 
   	   this.activity = activity;
   	}
   
      public void setHorizontalMotion( PlayerHorizontalMotion xMotion ){ 
   	   this.xMotion = xMotion;
   	}
   
      public void setVerticalMotion( PlayerVerticalMotion yMotion ){ 
   	   this.yMotion = yMotion;
   	}
      */
      public void setLifeState( PlayerLifeState lifeState){ 
         lifeStatus = lifeState;
      }
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      
   	
   //PLAYER CONTROL
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
      //POSITION
      public Point2D getPos(){
         return position;
      }
      public void setPos( double xComp, double yComp ){
         position.setLocation( xComp, yComp );
      }
   	
      //VELOCITY	
      public Vector2D getVel(){
         return totalVelocity;
      } 	
      public void setVel( double xComp, double yComp ){
         totalVelocity.set( xComp, yComp );
      }
   	
   	//ACCELERATION
      public Vector2D getAcc(){
         return totalAcceleration;
      }
      public void setAcc( double xComp, double yComp ){
         totalAcceleration.set( xComp, yComp );
      }
   	
   	//This is where the action happens
   	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      public void doMove( boolean[] keysDown ){
         
         if( lifeStatus == PlayerLifeState.ALIVE ){
         
         }
         else if( lifeStatus == PlayerLifeState.RESPAWNING ){
         
         }
         else{
         
         }
      	
      	
         /*if( !( keysDown[3] && keysDown[2])){
            if ( keysDown[3]){
               if( totalVelocity.getX() >= maxXVel )
                  totalVelocity.getX() = maxXVel;
               else
                  totalVelocity.getX() += movingAcceleration;
            }
            else if( keysDown[2]){
               if( totalVelocity.getX() <= -1*maxXVel )
                  totalVelocity.getX() = -1*maxXVel;
               else
                  totalVelocity.getX() -= movingAcceleration;
            }
            else{
               if( totalVelocity.getX() > 0 ){
                  if( totalVelocity.getX() <= Math.abs(movingDecceleration) || (totalVelocity.getX() + movingDecceleration )<= 0)
                     totalVelocity.getX() = 0;
                  else
                     totalVelocity.getX() += movingDecceleration;
               }
               else if( totalVelocity.getX() < 0 ){
                  if( totalVelocity.getX() >= movingDecceleration )
                     totalVelocity.getX() = 0;
                  else
                     totalVelocity.getX() -= movingDecceleration;
               }
               
            }
         }
      	
      	position.getX() += totalVelocity.getX();
      	
      	*/
      	
      	
      }	 
    
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   }