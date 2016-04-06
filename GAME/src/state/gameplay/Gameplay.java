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
      private Player[] players;
      Player p1;
   //Animation loop variables	
   
   //Map objects
      private Boot mapBoot;
      Dimension mapSize;
   //TESTING ONLY
      //BufferedImage testBackground = null;
      Image testBackground = null; 
      BufferedImage testMario = null;
      Image displayBackground = null;
      Image displayMario;
   //Imaging variables & objects
   
   //Player      	
      int p1Width;
      int p1Height;
      int p1DisplayWidth;
      int p1DisplayHeight;
      int p1XDisplayCoord;
      int p1YDisplayCoord;  
   
      int[][] keyIDs;    
   	
      long tStart;
   
   //~~~~~~~~~~~~~ 	
   //DEFAULT Gameplay Object
   //~~~~~~~~~~~~~
      public Gameplay( Boot boot ) { // needs to be changed to allow for customized games
         
         mapBoot = boot;
      //Initializes key game objects/ Variables      
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
         players = new Player[2];
      //Map Stuff
         mapSize = new Dimension( 32, 18 );//should be read from map being used
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
       
      //Initialize the new Player
         p1 = new Player( 1 );
         players[0] = p1;
         	
         p1Width = 1;
         p1Height = 2;
      	
         p1DisplayWidth = (int)( p1Width*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1Height*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      	
         keyIDs = new int[2][7];
         keyIDs[0] = MatchConstants.P1_KEY_CODES;
         keyIDs[1] = MatchConstants.P2_KEY_CODES;
      
      //TESTING ONLY
         try{
          //testBackground = ImageIO.read( new File( "GAME/src/TESTING/sun.jpg" ));
          //testBackground = ImageIO.read( new File( "GAME/src/TESTING/testStageBackground.jpg" ));
            testBackground = new ImageIcon(getClass().getClassLoader().getResource( "GAME/src/maps/res/bg.gif" )).getImage();
                 
            testMario = ImageIO.read( new File( "GAME/src/TESTING/8-bit-mario.jpg"));
            displayBackground = testBackground;
            displayMario = testMario;
         }
            catch(IOException e){
               e.printStackTrace();
            }
      	
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
      
      //Player 1//
      //~~~~~~~~~~~~~~~~~~~~//
         if( p1.getLifeState() == Player.PlayerLifeState.ALIVE ){ 
            //ALIVE
            p1.doMove( new boolean[] { keyboard.keyDown( keyIDs[0][0] ), keyboard.keyDown( keyIDs[0][1] ), 
                  keyboard.keyDown( keyIDs[0][2] ), keyboard.keyDown( keyIDs[0][3] ), 
                  keyboard.keyDown( keyIDs[0][4] ), keyboard.keyDown( keyIDs[0][5] ), 
                  keyboard.keyDown( keyIDs[0][6] ) } );
         }
         else if( p1.getLifeState() == Player.PlayerLifeState.RESPAWNING ){
            //RESPAWNING
         	
         }
         else{
            //DEAD
         
         
         }
      
      // a) Read the keyboard poll and update player 1
      // b) Read the keyboard poll and update player 2  
      // c) Simulate physics for any other moving objects
      // These Steps involve reading the current states of the objects
      
      // 3) Detect Collisions
      //   (when players sprites are updated, they will play any sounds from their player objects)
      
      // a) Detect Collisions for player 1, then player 2
      // b) Update states of objects if necessary
      
      // Find where to loop background music, and any other higher level sounds
      // within the Gameplay Class (e.g. match start and game over sounds)
      
      // 4) Update Game Statistics (e.g. stock, deaths, respawns, time elapsed, 
      //                       other info included in Match Results, etc.)
      
      
      //System.out.println("UPDATE GAME: \t" + System.nanoTime() / 1000000000 );
      
      }
   
      public void renderGame( float interpolation ){
      // 1) Draw Map
      
      // 2) Draw Sprites (non-constant):
      //    a) Players
      //    b) Other Items/ Objects
      //    c) Effects
      
      // 3) Draw GUI Overlay
         
         p1XDisplayCoord = (int)(this.getWidth()/2.0 + (this.getWidth()*(p1.getPos().getX() + p1.getVel().getX()*interpolation))/mapSize.getWidth()-p1DisplayWidth/2 );      
         p1YDisplayCoord = (int)(this.getHeight()/2.0 - (this.getHeight()*(p1.getPos().getY() + p1.getVel().getY()*interpolation))/mapSize.getHeight()-p1DisplayHeight );
      
         repaint();
      
      }
   
      public void resizeGraphics(){
         p1DisplayWidth = (int)( p1Width*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1Height*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      
         displayBackground = testBackground.getScaledInstance( (int) this.getPreferredSize().getWidth(), (int) this.getPreferredSize().getHeight(), Image.SCALE_FAST );
         displayMario = testMario.getScaledInstance( p1DisplayWidth, p1DisplayHeight, Image.SCALE_SMOOTH );
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
         
      	//P1 hitbox/ background
         g.setColor( Color.CYAN );
         g.fillRect( p1XDisplayCoord, p1YDisplayCoord, p1DisplayWidth, p1DisplayHeight );
         
      	//P1 image
         g.drawImage( displayMario, p1XDisplayCoord, p1YDisplayCoord, null);
      	
      	//P1 Coordinates
         g.setColor( Color.GREEN );
         g.drawString( "P1 COORDS: " + "( " + Math.round( p1.getPos().getX()*100.0 )/100.0 + ", " + 
                                                         Math.round( p1.getPos().getY()*100.0 )/100.0 + " )", 25, 25 );
         //Time elapsed (seconds)      
         g.drawString( "ELAPSED TIME (s): " + Math.round( ((System.nanoTime()-tStart)/1000000000.0)*10.0 )/10.0, 25, 40 );
      	
      	//Vertical Motion Test
         if( p1.getVerticalMotion() == Player.PlayerVerticalMotion.DOWN ){
            g.setColor( Color.RED );
            g.fillRect( 25, 55, 15, 15 );
         }
         else if( p1.getVerticalMotion() == Player.PlayerVerticalMotion.NONE ){
            g.setColor( Color.YELLOW );
            g.fillRect( 45, 55, 15, 15 );
         }
         if( p1.getVerticalMotion() == Player.PlayerVerticalMotion.UP ){
            g.setColor( Color.GREEN );
            g.fillRect( 65, 55, 15, 15 );
         }
      	
      
      }
   
   
   }