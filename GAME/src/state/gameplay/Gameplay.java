   package GAME.src.state.gameplay;  

   import java.awt.*;
   import java.awt.event.KeyEvent;
   import java.awt.event.KeyListener;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import java.awt.image.*;  
   import java.io.*;
   import java.lang.Runnable;
   import java.util.*;
   import javax.imageio.ImageIO;
   import javax.swing.*;
   import javax.swing.JPanel;

   import GAME.src.matchClasses.actor.Player;
   import GAME.src.matchClasses.object.Item;
   import GAME.src.matchClasses.object.Hitbox;

   public class Gameplay extends JPanel implements Runnable {
   
      InputHandler keyboard = new InputHandler();
      private boolean paused;
      private boolean gameOver;
      private enum GameType { TIMED, STOCK };
      private Thread mainThread;
   //JPanel hud;
   //JPanel pauseMenu;
      private GameType gameType;
   //Collection of GameObjects
      private Player[] players;
      Player p1;
      Player p2;
   //Animation loop variables	
      private static final int TICKS_PER_SECOND = 25;
      private static final int SKIP_TICKS = 1000000000 / TICKS_PER_SECOND;
      private static final int MAX_FRAMESKIP = 5;
      boolean running;
   //Map objects
      Dimension mapSize;
   //TESTING ONLY
      BufferedImage testBackground = null;
      BufferedImage testMario = null;
      Image displayBackground = null;
      Image displayMario;
   //Imaging variables & objects
      private static final double DESIRED_ASPECT_RATIO = (16.0/9);
   //Player      	
      int p1Width;
      int p1Height;
      double p1X;
      double p1Y;
      double p1XVelocity;
      double movingAcceleration;
      double movingDecceleration; 
      double maxXVel;   
   	
      int p1DisplayWidth;
      int p1DisplayHeight;
      int p1XCoord;
      int p1YCoord;
   	
      int p2Width;
      int p2Height;
      double p2X;
      double p2Y;
      double p2XVelocity;
   	
      int p2DisplayWidth;
      int p2DisplayHeight;
      int p2XCoord;
      int p2YCoord;
   	
   	
      int[] p1Keys;
      int[] p2Keys;
   	
      //~~~~~~~~~~~~~ 	
   	//DEFAULT Gameplay Object
   	//~~~~~~~~~~~~~
      public Gameplay() { // needs to be changed to allow for customized games
      
      //Initializes key game objects/ Variables      
         defaultInitialize();    
      //Starts the main thread
         mainThread = new Thread( this );
         mainThread.start();
      	
      }
   	 
   //public Gameplay( Map map, ArrayList<Player> players, GameType type ){}
   
      //~~~~~~~~~~~~~~
      //DEFAULT Init method
      //~~~~~~~~~~~~~~
      public final void defaultInitialize(){
      
         System.out.println("Initializing Gameplay...");
      
         paused = false;
         gameOver = false;
         players = new Player[2];
       //Map Stuff
         mapSize = new Dimension( 32, 18 );
       //JPanel Stuff
         requestFocus();
         requestFocusInWindow();
         setMinimumSize( new Dimension( 800, 450 ) );
         setMaximumSize( new Dimension( 1600, 900 ) );
         setBackground( Color.BLACK );
       //Hookup keyboard polling
         addKeyListener( keyboard );
       //Initialize the Players Characters
          //TODO
      	 
       //Initialize the new Player
         p1 = new Player( 1, 5 );
         p2 = new Player( 2, 5 );
         players[0] = p1;
         players[1] = p2;
      	
         movingAcceleration = 10.0/(TICKS_PER_SECOND*0.6);
         movingDecceleration = -1.0/(TICKS_PER_SECOND*0.6);
         maxXVel = (10.0)/TICKS_PER_SECOND; //10 units/second
      	
         p1Width = 1;
         p1Height = 2;
         p1X = -1.0*( p1Width/2 );
         p1Y = 1.0*p1Height;
         p1XVelocity = 0.0;
         p1DisplayWidth = (int)( p1Width*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1Height*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      	
         p2Width = 1;
         p2Height = 2;
         p2X = -1.0*( p2Width/2 );
         p2Y = 1.0*p2Height;
         p2XVelocity = 0.0;
         p2DisplayWidth = (int)( p2Width*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p2DisplayHeight = (int)( p2Height*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      	
      	
      	
         p1Keys = players[0].getPlayerKeyCodes();
         p2Keys = players[1].getPlayerKeyCodes();
      	
       //TESTING ONLY
         try{
            testBackground = ImageIO.read( new File( "GAME/src/TESTING/sun.jpg" ));
            //testBackground = ImageIO.read( new File( "GAME/src/TESTING/testStageBackground.jpg" ));        
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
         return DESIRED_ASPECT_RATIO;
      }
   
      public boolean isMatchPaused(){
         return paused;
      }
   
      public boolean isGameOver(){ 
         return gameOver;
      }
   
      public void pauseMatch(){ }
   
      public void resumeMatch(){ }       	
   
   
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
      	
         running = true;      
         while( running ){
            
            loops = 0;
            while( System.nanoTime() > next_game_tick && loops < MAX_FRAMESKIP ){
               updateGame();
               
               next_game_tick += SKIP_TICKS;
               loops++;
            }
         	
            interpolation = (float)( System.nanoTime() + SKIP_TICKS - next_game_tick) / (float)( SKIP_TICKS );
            renderGame( interpolation );
         }
      	
      }
   
   
      public void updateGame(){
      
      // 1) Process Keyboard Input   
         
         keyboard.poll();
      	
      // 2) Simulate Physics for each Player & any moving game objects. 
      //    (Don't do anything for stationary objects, as those will be handled by the other objects collision detections).
      
      //Player 1//
      //~~~~~~~~~~~~~~~~~~~~//
         if( !( keyboard.keyDown( KeyEvent.VK_RIGHT ) && keyboard.keyDown( KeyEvent.VK_LEFT ))){
            if ( keyboard.keyDown( KeyEvent.VK_RIGHT )){
               if( p1XVelocity >= maxXVel )
                  p1XVelocity = maxXVel;
               else
                  p1XVelocity += movingAcceleration;
            }
            else if( keyboard.keyDown( KeyEvent.VK_LEFT )){
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
      	//Player 2//
         //~~~~~~~~~~~~~~~~~~~~//
         if( !( keyboard.keyDown( KeyEvent.VK_D ) && keyboard.keyDown( KeyEvent.VK_A ))){
            if ( keyboard.keyDown( KeyEvent.VK_D )){
               if( p2XVelocity >= maxXVel )
                  p2XVelocity = maxXVel;
               else
                  p2XVelocity += movingAcceleration;
            }
            else if( keyboard.keyDown( KeyEvent.VK_A )){
               if( p2XVelocity <= -1*maxXVel )
                  p2XVelocity = -1*maxXVel;
               else
                  p2XVelocity -= movingAcceleration;
            }
            else{
               if( p2XVelocity > 0 ){
                  if( p2XVelocity <= Math.abs(movingDecceleration) || (p2XVelocity + movingDecceleration )<= 0)
                     p2XVelocity = 0;
                  else
                     p2XVelocity += movingDecceleration;
               }
               else if( p2XVelocity < 0 ){
                  if( p2XVelocity >= movingDecceleration )
                     p2XVelocity = 0;
                  else
                     p2XVelocity -= movingDecceleration;
               }
               
            }
         }
         
      	
         p1X += p1XVelocity;
         p1XCoord = (int)(this.getWidth()/2.0 + ((this.getWidth()/2.0)*(p1X))/(mapSize.getWidth()/2.0) );
         p1YCoord = (int)(this.getHeight()/2.0);
      	
         p2X += p2XVelocity;
         p2XCoord = (int)(this.getWidth()/2.0 + ((this.getWidth()/2.0)*(p2X))/(mapSize.getWidth()/2.0) );
         p2YCoord = (int)(this.getHeight()/2.0);
      	
      	//INSTEAD OF THAT ^^^
      	        //DO THIS vvv
      	
         p1.doMove( new boolean[] { keyboard.keyDown( p1Keys[0] ), keyboard.keyDown( p1Keys[1] ), 
               keyboard.keyDown( p1Keys[2] ), keyboard.keyDown( p1Keys[3] ), 
               keyboard.keyDown( p1Keys[4] ), keyboard.keyDown( p1Keys[5] ), 
               keyboard.keyDown( p1Keys[6] ) } );
      			
         p2.doMove( new boolean[] { keyboard.keyDown( p2Keys[0] ), keyboard.keyDown( p2Keys[1] ), 
               keyboard.keyDown( p2Keys[2] ), keyboard.keyDown( p2Keys[3] ), 
               keyboard.keyDown( p2Keys[4] ), keyboard.keyDown( p2Keys[5] ), 
               keyboard.keyDown( p2Keys[6] ) } );
      	
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
      
      
      
      // 4) Update Game Stats (e.g. stock, deaths, respawns, time elapsed, 
      //                       other info included in Match Results, etc.)
      	
      	
         //System.out.println("UPDATE GAME: \t" + System.nanoTime() / 1000000000 );
      
      }
   
      public void renderGame( float interpolation ){
      
      // 1) Draw Background Image
      
      // 2) Draw Sprites (non-constant):
      //    a) Players
      //    b) Other Items/ Objects
      //    c) Effects
      
      // 3) Draw GUI Overlay
      
         //System.out.println("RENDERGAME: " + interpolation );
      	
         repaint();
      
      }
   	
      public void resizeGraphics(){
         p1DisplayWidth = (int)( p1Width*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1Height*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      	
         p2DisplayWidth = (int)( p2Width*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p2DisplayHeight = (int)( p2Height*this.getPreferredSize().getHeight()/mapSize.getHeight() );
      	
         displayBackground = testBackground.getScaledInstance( (int) this.getPreferredSize().getWidth(), (int) this.getPreferredSize().getHeight(), Image.SCALE_SMOOTH );
         displayMario = testMario.getScaledInstance( p1DisplayWidth, p1DisplayHeight, Image.SCALE_SMOOTH );
      }
   	
    //TEST ONLY
      @Override
      public void paint(Graphics g) {
         super.paintComponent(g);
         g.drawImage( displayBackground, 0, 0, null );
      
         g.setColor( Color.RED );
         //g.fillRect( p1XCoord, p1YCoord, p1DisplayWidth, p1DisplayHeight );
         g.drawImage( displayMario, p1XCoord, p1YCoord, null);
      	
         g.setColor( Color.BLUE );
         g.fillRect( p2XCoord, p2YCoord, p2DisplayWidth, p2DisplayHeight );     
      }
      
   }