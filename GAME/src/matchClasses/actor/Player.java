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
   	
      private Dimension characterUnitSize; // unit: meters
      private Point2D playerCoords;
      private Vector2D playerVelocity;
      private Vector2D playerAcceleration;
      private double maximumRunningSpeed;   //(possibly) read from Chosen Character's stats
      private double runningAcceleration;   //^^^ same
      private double slidingDecceleration;  //^^^ same
   
      //private enum PlayerDirection { FACING_LEFT, FACING_RIGHT };
      //private enum PlayerActivity { IDLE, DUCK, DIVE, BLOCK, PRIMARY, SECONDARY };
      //private enum PlayerVerticalMotion { NONE, UP, DOWN };
      //private enum PlayerHorizontalMotion { NONE, LEFT, RIGHT };
      //private enum PlayerLifeState { ALIVE, DEAD, RESPAWNING };
   
      //private PlayerDirection facing;
      //private PlayerActivity activity;
      //private PlayerHorizontalMotion xMotion;
      //private PlayerVerticalMotion yMotion;
      //private PlayerLifeState lifeStatus;
   	
      private Dimension characterRenderSize;
   
   //Default Constructor
      public Player ( int playerNumber ){
      
         playerNum = playerNumber;
         KOs = 0;
         livesLost = 0;
         jumpNumber = 0;
         stock = 5;
         damagePercentage = 0;
         screenOrder = 0;
         
         playerCoords = new Point2D.Double( 0.0, 0.0 );
         playerVelocity = new Vector2D( 0.0, 0.0 );
         playerAcceleration = new Vector2D( 0.0, 0.0 );
      	
         maximumRunningSpeed = 10.0/MatchConstants.TICKS_PER_SECOND;        // 10   units/second
         runningAcceleration = 5.0/(MatchConstants.TICKS_PER_SECOND*3);     // 5/3  units/s/s 
         slidingDecceleration = -25.0/(MatchConstants.TICKS_PER_SECOND*6);  //-25/6 units/s/s
      	
         if( playerNum == 1 ){
            //facing = PlayerDirection.FACING_RIGHT;
            playerKeyCodes = MatchConstants.P1_KEY_CODES;
            //...
         }
         else if( playerNum == 2 ){
            //facing = PlayerDirection.FACING_LEFT;
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
   //public 
      public int getPlayerNumber(){
         return playerNum;
      }    
      /*public PlayerDirection getDirection(){
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
      }*/
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   //SET METHODS
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~    
      /*public void setDirection( PlayerDirection direction ){ }
   
      public void setActivity( PlayerActivity activity ){ }
   
      public void setHorizontalMotion( PlayerHorizontalMotion xMotion ){ }
   
      public void setVerticalMotion( PlayerVerticalMotion yMotion ){ }
   
      public void setLifeState( PlayerLifeState lifeState){ }*/
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
      
   	
   //PLAYER CONTROL
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
      public int[] getPlayerKeyCodes(){
         return playerKeyCodes;
      }
   
      public void doMove( boolean[] keysDown ){
         
      	
         /*if( !( keysDown[3] && keysDown[2])){
            if ( keysDown[3]){
               if( p1XVelocity >= maxXVel )
                  p1XVelocity = maxXVel;
               else
                  p1XVelocity += movingAcceleration;
            }
            else if( keysDown[2]){
               if( p1XVelocity <= -1*maxXVel )
                  p1XVelocity = -1*maxXVel;
               else
                  p1XVelocity -= movingAcceleration;
            }
            else{
               if( p1XVelocity > 0 ){
                  if( p1XVelocity <= Math.abs(movingDecceleration) || (p1XVelocity + movingDecceleration )<= 0)
                     p1XVelocity = 0;
                  else
                     p1XVelocity += movingDecceleration;
               }
               else if( p1XVelocity < 0 ){
                  if( p1XVelocity >= movingDecceleration )
                     p1XVelocity = 0;
                  else
                     p1XVelocity -= movingDecceleration;
               }
               
            }
         }
      	
      	p1X += p1XVelocity;
      	
      	*/
      	
      	
      }	 
    
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   
   
   }