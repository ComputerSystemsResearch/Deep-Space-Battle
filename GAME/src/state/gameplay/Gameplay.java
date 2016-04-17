package GAME.src.state.gameplay;  

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.awt.image.BufferStrategy;  
import java.io.*;
import java.lang.Runnable;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.*;
import javax.swing.JPanel;

import GAME.src.state.gameplay.MatchConstants;
import GAME.src.matchClasses.actor.Player;
import GAME.src.matchClasses.object.Hitbox;
import GAME.src.matchClasses.Vector2D;
import GAME.src.maps.data.*;
import GAME.src.maps.res.*;

public class Gameplay extends JPanel implements Runnable {

   InputHandler keyboard = new InputHandler();
   private volatile boolean running;
   private boolean paused;
   private boolean gameOver;
	
   private boolean infiniteStock;
   private long nanoTimeAllotted;
   private int numStock;
	
   private Thread mainThread;
//JPanel pauseMenu;
//Collection of GameObjects

//Map objects
   private Boot mapBoot;
   int[][] mapArray;
   Dimension mapSize;
   //int tilePixelWidth;
   //int tilePixelHeight;
	
   int[] mapPixelXCoords;
   int[] mapPixelYCoords;
   double[] mapXCoords;
   double[] mapYCoords;
	
//TESTING ONLY
   Image testBackground = null; 
   Image displayBackground = null;
	
   BufferedImage testMap = null;
   Image displayMap = null;
	
   BufferedImage testMario = null;
   Image displayMario = null;
	
   BufferedImage testLuigi = null;
   Image displayLuigi = null;
//Imaging variables & objects

//Player
   private Player[] players;
   Player p1;
   Player p2;
	   	
   int p1DisplayWidth;
   int p1DisplayHeight;
   int p1XDisplayCoord;
   int p1YDisplayCoord;  
	
   int p2DisplayWidth;
   int p2DisplayHeight;
   int p2XDisplayCoord;
   int p2YDisplayCoord; 
	
//Background and Maps;
   int bgDispWidth;
   int bgDispHeight;
	
   int[][] keyIDs;    
	
   long tStart;

//~~~~~~~~~~~~~ 	
//DEFAULT Gameplay Object
//~~~~~~~~~~~~~
   public Gameplay( Boot boot ) { // needs to be changed to allow for customized games
      
      mapBoot = boot;
      defaultInitialize();    
   
   }

   public synchronized void start(){
      if(running)
         return;		
      running = true;
      mainThread = new Thread(this); // Come back to set up safe stopping and suspension of the mainThread
      mainThread.start();
      mapBoot.start();
   }
 
//public Gameplay( Map map, ArrayList<Player> players, GameType type ){}

//~~~~~~~~~~~~~~
//DEFAULT Init method
//~~~~~~~~~~~~~~
   public final void defaultInitialize(){
   
      System.out.println("Initializing Gameplay...");
      
      tStart = 0L;
      running = false;
      paused = false;
      gameOver = false;
   	
   //JPanel Stuff
      requestFocus();
      requestFocusInWindow();
      setMinimumSize( new Dimension( 800, 450 ) );
      setMaximumSize( new Dimension( 1600, 900 ) );
      setBackground( Color.BLACK );
   	
   //Hookup keyboard polling
      addKeyListener( keyboard );
                                       //ADD a new keyListener to handle escape key pausing
   //Initialize the Players Characters
    //TODO
    
    //TESTING ONLY
      try{
       //testBackground = ImageIO.read( new File( "GAME/src/TESTING/sun.jpg" ));
       //testBackground = ImageIO.read( new File( "GAME/src/TESTING/testStageBackground.jpg" ));
         testBackground = new ImageIcon(getClass().getClassLoader().getResource( "GAME/src/maps/res/bg.gif" )).getImage();
         testMap = ImageIO.read( new File( "GAME/src/maps/res/map 1.png" ));
         testMario = ImageIO.read( new File( "GAME/src/TESTING/8-bit-mario.jpg"));
         testLuigi = ImageIO.read( new File( "GAME/src/TESTING/8-bit-luigi.png"));
         displayBackground = testBackground;
         displayMap = testMap;
         displayMario = testMario;
         displayLuigi = testLuigi;
      }
      catch(IOException e){
         e.printStackTrace();
      }
    
    //Map Stuff
      mapArray = mapBoot.getMap();
      mapSize = new Dimension( mapArray[0].length, mapArray.length );
      //tilePixelWidth = (int)( 1.0* this.getWidth() / mapSize.getWidth() );
      //tilePixelHeight = (int)( 1.0* this.getHeight() / mapSize.getHeight() );
   	
      mapPixelXCoords = new int[]{73, 827, 700, 190};
      mapPixelYCoords = new int[]{250, 250, 450, 450};
      mapXCoords = new double[4];
      mapYCoords = new double[4];
   	
      for( int i=0; i<4; i++ ){
         mapXCoords[i] = (1.0*mapSize.getWidth()/testMap.getWidth())*(mapPixelXCoords[i]-testMap.getWidth()/2.0);
         mapYCoords[i] = (1.0*mapSize.getHeight()/testMap.getHeight())*(testMap.getHeight()/2.0-mapPixelYCoords[i]);
      }
   	
      System.out.println( Arrays.toString(mapXCoords) );
      System.out.println( Arrays.toString(mapYCoords) );
    
   //Initialize the new Players
      players = new Player[2];
      p1 = new Player( 1, new Vector2D(1.5,(6.0/3)), new Vector2D(-0.25*mapSize.getWidth(), 0.25*mapSize.getHeight()));
      p2 = new Player( 2, new Vector2D(1.5,(6.0/3)), new Vector2D(0.25*mapSize.getWidth(), 0.25*mapSize.getHeight()));
      players[0] = p1;
      players[1] = p2;
   
      p1DisplayWidth = (int)( p1.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
      p1DisplayHeight = (int)( p1.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
   	
      p2DisplayWidth = (int)( p2.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
      p2DisplayHeight = (int)( p2.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
   	
      keyIDs = new int[2][7];
      keyIDs[0] = MatchConstants.P1_KEY_CODES;
      keyIDs[1] = MatchConstants.P2_KEY_CODES;
   	
   //Background and Maps
      bgDispWidth = (int) this.getPreferredSize().getWidth();
      bgDispHeight = (int) this.getPreferredSize().getHeight();
   	
      System.out.println("Gameplay Initialized");
   
   }

   public double getDesiredAspectRatio(){
      return MatchConstants.VIEWPORT_ASPECT_RATIO;
   }

   public boolean isMatchPaused(){
      return paused;
   }

   public boolean isGameOver(){ 
      return gameOver;
   }

   public void startGame(){
      System.out.println("GAME START");
   //Do starting sequence/ countdown
   }

   public void endGame(){
      System.out.println("GAME OVER");
   //Do end sequence 
   //Finalize any endgame data necessary
   }    

   public void pauseMatch(){ 
      paused = true;
      System.out.println("GAME PAUSED");
   }

   public void resumeMatch(){
      paused = false;
      System.out.println("GAME RESUMED");
   }       	


//For the run() method
//
//To initialize
//1	Background
//2	Map (Then combine 1&2 to make the backBuffer) --(sidenote: backbuffer may need to be alterable for dynamic match viewport)
//3	Characters
//3' 	Particle Effects 
//4	HUD
//5	Add Listeners
//6	Countdown to Match Start
//7	Activate Listener Control
//7'	START MATCH

//Gameplay update()->
//2, 3, 3', & 4

//-> Pause Match ->
//1	Stop updating gameplay
//2	Display pause menu
//2'	Carry out an action from the pause menu
//3 	Either: resume match or end match

//End Game Sequence
//1	Stop gameplay	
//2	Write Match Data to a file
//3	Cleanup & Exit
//4	Show Match Results

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   

   @Override
   public void run() {	  
   	
      long next_game_tick = System.nanoTime();
      int loops;
      float interpolation;
   
      startGame();
      
      tStart = System.nanoTime();
   	
      while( running ){
      
         while( !(gameOver) ){
         
            while(!(paused)){
            
               loops = 0;
               while( System.nanoTime() > next_game_tick && loops < MatchConstants.MAX_FRAMESKIP ){
                  updateGame();
               
                  next_game_tick += MatchConstants.SKIP_TICKS;
                  loops++;
               }
            
               interpolation = (float)( System.nanoTime() + MatchConstants.SKIP_TICKS - next_game_tick) / (float)( MatchConstants.SKIP_TICKS );           
               renderGame( interpolation );
            }
         
         }
         endGame();
      }
   
   }

   public void updateGame(){
   
   // 1) Poll keyboard to allow for processing   
   
      keyboard.poll();
   
   // 2) Simulate Physics for each Player & any moving game objects. 
   //    (Don't do anything for stationary objects, as those will be handled by the other objects collision detections).
     // a) Read the keyboard poll and update player 1
     // b) Read the keyboard poll and update player 2  
     // c) Simulate physics for any other moving objects
     // These Steps involve reading the current states of the objects
   
   //Doing Moves
      movePlayers(); 
   
   // 3) Detect Collisions
   //   (when players sprites are updated, they will play any sounds from their player objects)
   
   // a) Detect Collisions for player 1, then player 2
   // b) Update states of objects if necessary
      
      detectCollisions();     	
   	
   // Find where to loop background music, and any other higher level sounds
   // within the Gameplay Class (e.g. match start and game over sounds)
   
   // 4) Update Game Statistics (e.g. stock, deaths, respawns, time elapsed, 
   //                       other info included in Match Results, etc.)
   
   }
   
   
	
	// PLAYER METHODS
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
   
   public void movePlayers(){
   
      for( Player p: players ){
         if( p.getLifeState() == Player.LifeState.ALIVE ){ 
         //ALIVE
            move(p);
         }
         else if( p.getLifeState() == Player.LifeState.RESPAWNING ){
         //RESPAWNING
            if( anyKeysDown(p) ){
               move(p);
               p.setLifeState( Player.LifeState.ALIVE );
            }
         }
         else{
         //DEAD
         //If liveslost<stock
            p.respawn();
         }
      }
   
   }
	
   public void move( Player p ){
      int i = p.getPlayerNumber() - 1;
      p.doMove( new boolean[] { keyboard.keyDown( keyIDs[i][0] ), keyboard.keyDown( keyIDs[i][1] ), 
               keyboard.keyDown( keyIDs[i][2] ), keyboard.keyDown( keyIDs[i][3] ), 
               keyboard.keyDown( keyIDs[i][4] ), keyboard.keyDown( keyIDs[i][5] ), 
               keyboard.keyDown( keyIDs[i][6] ) } );
   }
   
   public void detectCollisions(){
   
   //PLAYER LEFT MAP
      for( Player p: players){
         if( hasLeftMap(p) )
            p.die();
      }
      
   //PLAYER COLLISIONS
      if( p1.getLifeState() == Player.LifeState.ALIVE && p2.getLifeState() == Player.LifeState.ALIVE ){
         //Player to Player
         if( p2pCollision() ){
            resolveP2Pcollision();
         }
      }
      //Player to Stage
      for( Player p: players ){
         if( p.getVerticalMotion() != Player.VerticalMotion.NONE ){
              //Checks and Resolves Player to Stage Collisions
            if( p.getLifeState() == Player.LifeState.ALIVE )
               evalStageCollisions( p );
         }
      }
   
   }
	
   public boolean anyKeysDown( Player p ){
      int i = p.getPlayerNumber() - 1;
      if( keyboard.keyDown( keyIDs[i][0] ) || keyboard.keyDown( keyIDs[i][1] ) || keyboard.keyDown( keyIDs[i][2] ) || keyboard.keyDown( keyIDs[i][3] ) || 
             keyboard.keyDown( keyIDs[i][4] ) || keyboard.keyDown( keyIDs[i][5] ) || keyboard.keyDown( keyIDs[i][6] ) )
         return true;
      return false;
   }
	
   public boolean hasLeftMap( Player p ){
      if( p.getPos().getX() + p.width() < -0.5*mapSize.getWidth()-4 || p.getPos().getX() > 0.5*mapSize.getWidth()+4 
          || p.getPos().getY() + p.height() < -0.5*mapSize.getHeight()-2.25 || p.getPos().getY() > 0.5*mapSize.getHeight()+2.25)
         return true;
      return false;
   }
	
   public boolean p2pCollision(){
   
      if( (p1.getPos().getX() + p1.width()) < p2.getPos().getX() || p1.getPos().getX() > (p2.getPos().getX() + p2.width()) ) 
         return false;
      if( (p1.getPos().getY() + p1.height()) < p2.getPos().getY() || p1.getPos().getY() > (p2.getPos().getY() + p2.height()) ) 
         return false;
   		
      return true;
   }
   
   public void resolveP2Pcollision(){
   
      double penHalfWidth = 0.0;
      double penHalfHeight = 0.0;
      //PENETRATION WIDTH            
      if( p1.getVel().getX() > p2.getVel().getX() )
         penHalfWidth = ((p1.getPos().getX() + p1.width()) - p2.getPos().getX()) / 2.0;
      else
         penHalfWidth = ((p2.getPos().getX() + p2.width()) - p1.getPos().getX()) /2.0;
      //PENETRATION HEIGHT
      if( p1.getVel().getY() > p2.getVel().getY() )
         penHalfHeight = ((p1.getPos().getY() + p1.height()) - p2.getPos().getY()) /2.0;
      else
         penHalfHeight = ((p2.getPos().getY() + p2.height()) - p1.getPos().getY()) /2.0;
               
      //Apply Collision Impulse
      if( penHalfHeight > penHalfWidth ){
         if( p1.getVel().getX() > p2.getVel().getX() ){
            p1.setPos( p1.getPos().getX() - penHalfWidth, p1.getPos().getY() );
            p2.setPos( p2.getPos().getX() + penHalfWidth, p2.getPos().getY() );
         }
         else{
            p1.setPos( p1.getPos().getX() + penHalfWidth, p1.getPos().getY() );
            p2.setPos( p2.getPos().getX() - penHalfWidth, p2.getPos().getY() );
         }
         double p1xTemp = p1.getVel().getX();
         p1.setVel( p2.getVel().getX(), p1.getVel().getY() );
         p2.setVel( p1xTemp, p2.getVel().getY() );
      }
      else if( penHalfWidth >= penHalfHeight ){
         if( p1.getVel().getY() > p2.getVel().getY() ){
            p1.setPos( p1.getPos().getX(), p1.getPos().getY() - penHalfHeight);
            p2.setPos( p2.getPos().getX(), p2.getPos().getY() + penHalfHeight);
         }
         else{
            p1.setPos( p1.getPos().getX(), p1.getPos().getY() + penHalfHeight);
            p2.setPos( p2.getPos().getX(), p2.getPos().getY() - penHalfHeight);
         }
         double p1yTemp = p1.getVel().getY();
         p1.setVel( p1.getVel().getX(), p2.getVel().getY() );
         p2.setVel( p2.getVel().getX(), p1yTemp );
      }
   
   }
   
   public void evalStageCollisions( Player p ){
   
      for( int i=0; i<mapXCoords.length; i++ ){
      
         double nearX, farX, nearY, farY;
         double Ax, Ay, Bx, By;
                  
         if( i == mapXCoords.length-1 ){
            Ax = mapXCoords[i];
            Ay = mapYCoords[i];
            Bx = mapXCoords[0];
            By = mapYCoords[0];
         }
         else{
            Ax = mapXCoords[i];
            Ay = mapYCoords[i];
            Bx = mapXCoords[i+1];
            By = mapYCoords[i+1];
         }
         
         double deltaX = Bx-Ax;
         double deltaY = By-Ay;
         double scaleX = 1.0/deltaX;
         double scaleY = 1.0/deltaY;
         
         if( deltaX >= 0 ){
            nearX = p.getPos().getX();
            farX = nearX + p.width();
         }
         else{
            farX = p.getPos().getX();
            nearX = farX + p.width();
         }
         if( deltaY >= 0 ){
            nearY = p.getPos().getY();
            farY = nearY + p.height();
         }
         else{
            farY = p.getPos().getY();
            nearY = farY + p.height();
         }
         
         double nearTimeX = scaleX * ( nearX - Ax ); 
         double nearTimeY = scaleY * ( nearY - Ay );
         double farTimeX = scaleX * ( farX - Ax ); 
         double farTimeY = scaleY * ( farY - Ay );
         
         if( nearTimeX > farTimeY || nearTimeY > farTimeX )
            continue;
         
         double nearTime = Math.max( nearTimeX, nearTimeY );
         double farTime = Math.min( farTimeX, farTimeY );
         
         if( nearTime >= 1 || farTime <= 0 )
            continue;
            
         //System.out.println( "P" + p.getPlayerNumber() + " CONTACTING STAGE EDGE" );
         
         switch (i){
            //TOP
            case 0:
               p.setPos( p.getPos().getX(), mapYCoords[0] );
               p.setVel( p.getVel().getX(), 0.0 );
               p.setVerticalMotion( Player.VerticalMotion.NONE );
               break;
               
            //RIGHT
            case 1:
               p.setPos( Ax + nearTimeY*deltaX, p.getPos().getY() );
               double magScale = 1.0/(Math.sqrt( deltaX*deltaX + deltaY*deltaY ) );
               double unitABx = -1 * deltaX * magScale;
               double unitABy = -1 * deltaY * magScale;
               double newXVel = unitABx * p.getVel().getX();
               double newYVel = unitABy * p.getVel().getY();
               p.setVel( newXVel, newYVel );
               break;
               
            //BOTTOM
            case 2:
               p.setPos( p.getPos().getX(), mapYCoords[0] - p.height() );
               p.setVel( p.getVel().getX(), 0.0 );
               p.setVerticalMotion( Player.VerticalMotion.NONE );
               break;
               
            //LEFT
            case 3:
               p.setPos( (Ax + farTimeY*deltaX) - p.width(), p.getPos().getY() );
               magScale = 1.0/(Math.sqrt( deltaX*deltaX + deltaY*deltaY ) );
               unitABx = deltaX * magScale;
               unitABy = deltaY * magScale;
               newXVel = unitABx * p.getVel().getX();
               newYVel = unitABy * p.getVel().getY();
               p.setVel( newXVel, newYVel );
               break;
               
            //DEFAULT/ ERROR
            default: System.out.println( "ERROR: STAGE COLLISION DETECTION" );
               break;
         
         }
         
      }
   
   }
   
   
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

   public void renderGame( float interpolation ){
   // 1) Draw Map
   
   // 2) Draw Sprites (non-constant):
   //    a) Players
   //    b) Other Items/ Objects
   //    c) Effects
   
   // 3) Draw GUI Overlay
      
   	//Calculate Display Coords for Moving Objects
      p1XDisplayCoord = (int)(this.getWidth()/2.0 + (this.getWidth()*(p1.getPos().getX() + p1.getVel().getX()*interpolation))/mapSize.getWidth() );      
      p1YDisplayCoord = (int)(this.getHeight()/2.0 - (this.getHeight()*(p1.getPos().getY() + p1.getVel().getY()*interpolation))/mapSize.getHeight()-p1DisplayHeight );
      
      p2XDisplayCoord = (int)(this.getWidth()/2.0 + (this.getWidth()*(p2.getPos().getX() + p2.getVel().getX()*interpolation))/mapSize.getWidth() );      
      p2YDisplayCoord = (int)(this.getHeight()/2.0 - (this.getHeight()*(p2.getPos().getY() + p2.getVel().getY()*interpolation))/mapSize.getHeight()-p2DisplayHeight );
   	
      repaint();
   
   }

   public void resizeGraphics(){
   
   	//PLAYERS
      p1DisplayWidth = (int)( p1.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
      p1DisplayHeight = (int)( p1.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      displayMario = testMario.getScaledInstance( p1DisplayWidth, p1DisplayHeight, Image.SCALE_SMOOTH );
   	
      p2DisplayWidth = (int)( p2.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
      p2DisplayHeight = (int)( p2.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      displayLuigi = testLuigi.getScaledInstance( p2DisplayWidth, p2DisplayHeight, Image.SCALE_SMOOTH );
   	
      //BACKGROUND
      bgDispWidth = (int) this.getPreferredSize().getWidth();
      bgDispHeight = (int) this.getPreferredSize().getHeight();
      displayBackground = testBackground.getScaledInstance( bgDispWidth, bgDispHeight, Image.SCALE_FAST );
   	
   	//MAP TEXTURE
      displayMap = testMap.getScaledInstance( bgDispWidth, bgDispHeight, Image.SCALE_SMOOTH );
      
   }

//TEST ONLY
   @Override
   public synchronized void paint(Graphics g) {
      super.paintComponent(g);
   	
   	//Background
      g.drawImage( displayBackground, 0, 0, null );
   	
      //Axis Lines
      g.setColor( Color.RED );
      g.drawLine( this.getWidth()/2, 0, this.getWidth()/2, this.getHeight() );
      g.drawLine( 0, this.getHeight()/2, this.getWidth(), this.getHeight()/2 );
   	
   	//Stage Image
      g.drawImage( displayMap, 0, 0, null );
   	
   	//P1 hitbox/ background
      g.setColor( Color.CYAN );
      g.fillRect( p1XDisplayCoord, p1YDisplayCoord, p1DisplayWidth, p1DisplayHeight );
   	//P1 image
      g.drawImage( displayMario, p1XDisplayCoord, p1YDisplayCoord, null);
   	//P1 Coordinates
      g.setColor( Color.RED );
      g.drawString( "P1:COORDS: " + "( " + Math.round( p1.getPos().getX()*100.0 )/100.0 + ", " + 
                                                      Math.round( p1.getPos().getY()*100.0 )/100.0 + " )", 25, 25 );
   																	
   //P2 hitbox/ background
      g.setColor( Color.MAGENTA );
      g.fillRect( p2XDisplayCoord, p2YDisplayCoord, p2DisplayWidth, p2DisplayHeight );
   //P2 image
      g.drawImage( displayLuigi, p2XDisplayCoord, p2YDisplayCoord, null);
   //P2 Coordinates
      g.setColor( Color.GREEN );
      g.drawString( "P2:COORDS: " + "( " + Math.round( p2.getPos().getX()*100.0 )/100.0 + ", " + 
                                                      Math.round( p2.getPos().getY()*100.0 )/100.0 + " )", 25, 40 );
   	
   	
   //P1 Vertical Motion Test
      if( p1.getVerticalMotion() == Player.VerticalMotion.DOWN ){
         g.setColor( Color.RED );
         g.fillRect( 180, 13, 15, 15 );
      }
      else if( p1.getVerticalMotion() == Player.VerticalMotion.NONE ){
         g.setColor( Color.YELLOW );
         g.fillRect( 200, 13, 15, 15 );
      }
      if( p1.getVerticalMotion() == Player.VerticalMotion.UP ){
         g.setColor( Color.GREEN );
         g.fillRect( 220, 13, 15, 15 );
      }
   //P2 Vertical Motion Test
      if( p2.getVerticalMotion() == Player.VerticalMotion.DOWN ){
         g.setColor( Color.RED );
         g.fillRect( 180, 28, 15, 15 );
      }
      else if( p2.getVerticalMotion() == Player.VerticalMotion.NONE ){
         g.setColor( Color.YELLOW );
         g.fillRect( 200, 28, 15, 15 );
      }
      if( p2.getVerticalMotion() == Player.VerticalMotion.UP ){
         g.setColor( Color.GREEN );
         g.fillRect( 220, 28, 15, 15 );
      }
   	
   //Time elapsed (seconds)     
      g.setColor( Color.GRAY ); 
      g.drawString( "ELAPSED TIME (s): " + Math.round( ((System.nanoTime()-tStart)/1000000000.0)*10.0 )/10.0, 25, 55 );
   
   }


}