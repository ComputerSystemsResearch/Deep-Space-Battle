   package GAME.src.state.gameplay;  

   import java.awt.*;
   import java.awt.event.KeyEvent;
   import java.awt.event.KeyListener;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import java.awt.geom.Area;
   import java.awt.geom.Path2D;
   import java.awt.geom.Rectangle2D;
   import java.awt.image.*;
   import java.awt.image.BufferStrategy;  
   import java.io.*;
   import java.lang.Runnable;
   import java.math.RoundingMode;
   import java.text.DecimalFormat;
   import java.util.*;

   import javax.imageio.ImageIO;
   import javax.swing.ImageIcon;
   import javax.swing.Timer;
   import javax.swing.JPanel;
   import javax.swing.JLabel;
   
   import GAME.resources.*;
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
   //Collection of GameObjects
   
   //DISPLAY / TESTING
      Image originalBackground = null; 
      Image displayBackground = null;
   
      Image originalStartText = null;
      Image displayStartText = null;
   
      BufferedImage originalMap = null;
      Image displayMap = null;
   
      BufferedImage originalMario = null;
      Image displayMario = null;
   
      BufferedImage originalLuigi = null;
      Image displayLuigi = null;
   
   //Imaging variables & objects
   
   
   //Players
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
   
      private Boot mapBoot;
      int[][] mapArray;
      Dimension mapSize;
   
      int[] mapPixelXCoords;
      int[] mapPixelYCoords;
      double[] mapXCoords;
      double[] mapYCoords;
   	
   //HUD
      JLabel timeLabel;
      
   //Stage collision troubleshooting
      Path2D stagePath = new Path2D.Double(); 
   
   //Other
      int[][] keyIDs; 
   	   
   //TIMING
      long tStart;
      Timer clock;
      int tCount = 0;
      DecimalFormat timeFormat;
   
   //~~~~~~~~~~~~~ 	
   //DEFAULT Gameplay Object
   //~~~~~~~~~~~~~
      public Gameplay( Boot boot ) { // needs to be changed to allow for customized games
      
         mapBoot = boot;
         numStock = MatchConstants.DEFAULT_STOCK;
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
         setLayout(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
      	
      //HUD
         timeLabel = new JLabel("");
         timeLabel.setFont(new Font( "Impact", Font.ITALIC, 24 ));
         timeLabel.setOpaque(true);
         timeLabel.setForeground( Color.WHITE );
         timeLabel.setHorizontalAlignment(JLabel.LEFT);
      	
         JLabel buffer;
      	
      //Adding the HUD to the panel
      
      //Buffers
         buffer = new JLabel("L BUFFER");
         buffer.setOpaque(true);
         buffer.setBackground(Color.GRAY);
         c.gridx = 0;
         c.gridy = 0;
         c.gridwidth = 1;      
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
         //c.weightx = 1.0;
         add(buffer, c);
      	
         buffer = new JLabel("R BUFFER");
         buffer.setOpaque(true);
         buffer.setBackground(Color.DARK_GRAY);
         c.gridx = 2;
         c.gridy = 0;
         c.gridwidth = 1;
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
         add(buffer, c);
      //Time
         c.gridx = 1;
         c.gridy = 0;
         c.weightx = 1.0;
         c.weighty = 1.0;
         c.gridwidth = 1;
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.FIRST_LINE_START;
         add(timeLabel, c);
      //Row - mid
         c.gridx = 0;
         c.gridy = 1;
         c.weightx = 1.0;
         c.weighty = 1.0;
         c.gridwidth = 3;
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
         add(new JLabel("ROW 2"), c);
      //Row - bottom
         c.gridx = 0;
         c.gridy = 2;
         c.weightx = 1.0;
         c.weighty = 1.0;
         c.gridwidth = 3;
         c.fill = GridBagConstraints.BOTH;
         c.anchor = GridBagConstraints.CENTER;
         add(new JLabel("ROW 3"), c);
      
      //Hookup keyboard polling
         this.addKeyListener( keyboard );
      
      //Initialize the Players Characters
      //TODO
      
      //IMAGE TESTING ONLY
         try{
            originalBackground = new ImageIcon(getClass().getClassLoader().getResource( "GAME/src/maps/res/bg.gif" )).getImage();
            originalStartText = new ImageIcon(getClass().getClassLoader().getResource( "GAME/resources/matchCountdown.gif" )).getImage();        
            originalMap = ImageIO.read( new File( "GAME/src/maps/res/map 1.png" ));
            originalMario = ImageIO.read( new File( "GAME/src/TESTING/8-bit-mario.jpg"));
            originalLuigi = ImageIO.read( new File( "GAME/src/TESTING/8-bit-luigi.png"));
            displayBackground = originalBackground;
            displayStartText = originalStartText;
            displayMap = originalMap;
            displayMario = originalMario;
            displayLuigi = originalLuigi;
         }
            catch(IOException e){
               e.printStackTrace();
            }
      
      //Map Stuff
         mapArray = mapBoot.getMap();
         mapSize = new Dimension( mapArray[0].length, mapArray.length );
      
         mapPixelXCoords = new int[]{73, 827, 700, 190};
         mapPixelYCoords = new int[]{250, 250, 450, 450};
         mapXCoords = new double[4];
         mapYCoords = new double[4];
      
         for( int i=0; i<4; i++ ){
            mapXCoords[i] = (1.0*mapSize.getWidth()/originalMap.getWidth())*(mapPixelXCoords[i]-originalMap.getWidth()*0.5);
            mapYCoords[i] = (1.0*mapSize.getHeight()/originalMap.getHeight())*(originalMap.getHeight()*0.5-mapPixelYCoords[i]);
         }
      	
         stagePath.moveTo( mapXCoords[0], mapYCoords[0] );
         for( int i=1; i<mapXCoords.length; i++ ){
            stagePath.lineTo( mapXCoords[i], mapYCoords[i] );
         }
         stagePath.closePath();
      
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
      
         clock = new Timer( 1000, 
               new ActionListener(){
                  public void actionPerformed( ActionEvent e ){
                     tCount++;
                  }
               });
      
         timeFormat = new DecimalFormat( "0.00" );
         timeFormat.setRoundingMode(RoundingMode.DOWN);
      	
         System.out.println("Gameplay Initialized");
      
      }
   
      public double getDesiredAspectRatio(){
         return MatchConstants.VIEWPORT_ASPECT_RATIO;
      }
   
      public void resizePanel(){
      
         int parentX = (int)( this.getParent().getWidth() );
         int parentY = (int)( this.getParent().getHeight() );
         double currentAspectRatio = 1.0*parentX/parentY;
         int xMax = (int)( this.getMaximumSize().getWidth() );
         int yMax = (int)( this.getMaximumSize().getHeight() );
         Dimension d;        	
      
         if( parentX >= xMax && parentY >= yMax ){
            this.setPreferredSize( this.getMaximumSize() );
         }
         else {
         
            if( currentAspectRatio >= this.getDesiredAspectRatio() )
               d = new Dimension( (int)( 1.0*parentY * this.getDesiredAspectRatio() ), parentY );
            else
               d = new Dimension( parentX, (int)(1.0*parentX / this.getDesiredAspectRatio() ) );
         
            if( d.getWidth() <= xMax && d.getHeight() <= yMax ){ 
               this.setPreferredSize( d );
               this.setMinimumSize( d );
            }
         
         }
      
         this.revalidate();
         this.resizeGraphics();
      
      }
   
      public boolean isMatchPaused(){
         return paused;
      }
   
      public boolean isGameOver(){ 
         return gameOver;
      }
   
      public void startGame( Timer startTimer){
         
         startTimer.start();
         while( tCount <=3 ){
            renderGame( 0 );
         }
         startTimer.stop();
         tStart = System.nanoTime();
         System.out.println("GAME START");
      //Do starting sequence/ countdown
      }
   
      public void endGame(){
         running = false;
         System.out.println("GAME OVER");
      //Do end sequence 
      //Finalize any endgame data necessary
      }    
   
      public void pauseMatch(){ 
         paused = true;
         System.out.println("PAUSED");
      }
   
      public void resumeMatch(){
         paused = false;
         System.out.println("RESUMED");
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
      
         while( running ){
         //Before game
            startGame( clock );
            while( !(gameOver) ){
            
               while(!(paused || gameOver)){
               
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
      
         updatePlayers(); 
      
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
   
      public void updatePlayers(){
      
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
               if( p.getLivesLost() < numStock ){
                  p.respawn();
               }
               else{
                  System.out.println("P" + p.getPlayerNumber() + " DEFEATED");
                  gameOver = true;
               }
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
            //Checks and Resolves Player-Stage Collisions
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
            penHalfWidth = ((p1.getPos().getX() + p1.width()) - p2.getPos().getX()) *0.5;
         else
            penHalfWidth = ((p2.getPos().getX() + p2.width()) - p1.getPos().getX()) *0.5;
      //PENETRATION HEIGHT
         if( p1.getVel().getY() > p2.getVel().getY() )
            penHalfHeight = ((p1.getPos().getY() + p1.height()) - p2.getPos().getY()) *0.5;
         else
            penHalfHeight = ((p2.getPos().getY() + p2.height()) - p1.getPos().getY()) *0.5;
            
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
      
         Area cross = new Area(new Rectangle2D.Double(p.getPos().getX(), p.getPos().getY(), p.width(), p.height()));
         cross.intersect(new Area(stagePath));
         if(cross.isEmpty())
            return;
      
         for( int i=0; i<mapXCoords.length; i++ ){
         
            double nearX, farX, nearY, farY;
            double Ax = mapXCoords[i];
            double Ay = mapYCoords[i];
            double Bx, By;
         
            if( i == mapXCoords.length-1 ){
               Bx = mapXCoords[0];
               By = mapYCoords[0];
            }
            else{
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
            
            double x = p.getPos().getX();
            double y = p.getPos().getY();
         	
         	outerloop:
            switch (i){
            //TOP
               case 0:
                  if( p.getVel().getY() <= 0 ){
                     if( x > 0 ){
                        if( x < mapXCoords[1] && mapXCoords[1] < x+p.width() ){
                           double penX = mapXCoords[1] - x;
                           double penY = mapYCoords[1] - y;
                           if( penX <= penY )
                              break outerloop;
                        } 
                     }
                     else if( x < 0){
                        if( x < mapXCoords[0] && mapXCoords[0] < x+p.width() ){
                           double penX = x+p.width()-mapXCoords[0];
                           double penY = mapYCoords[0] - y;
                           if( penX <=penY )
                              break outerloop;
                        }            
                     }
                     p.setPos( x, mapYCoords[0] );
                     p.setVel( p.getVel().getX(), 0.0 );
                     p.setVerticalMotion( Player.VerticalMotion.NONE );
                  }
                  break;
            
            //RIGHT
               case 1:
                  p.setPos( Ax + nearTimeY*deltaX, y );
                  double magScale = 1.0/(Math.sqrt( deltaX*deltaX + deltaY*deltaY ) );
                  double unitABx = -1 * deltaX * magScale;
                  double unitABy = -1 * deltaY * magScale;
                  double newXVel = unitABx * p.getVel().getX();
                  double newYVel = unitABy * p.getVel().getY();
                  p.setVel( newXVel, newYVel );
                  break;
            
            //BOTTOM
               case 2:
                  p.setPos( x, mapYCoords[2] - p.height() );
                  p.setVel( p.getVel().getX(), 0.0 );
                  p.setVerticalMotion( Player.VerticalMotion.NONE );
                  break;
            
            //LEFT
               case 3:
                  p.setPos( (Ax + farTimeY*deltaX) - p.width(), y );
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
   
      public synchronized void renderGame( float interpolation ){
      // 1) Draw Map
      
      // 2) Draw Sprites (non-constant):
      //    a) Players
      //    b) Other Items/ Objects
      //    c) Effects
      
      // 3) Draw GUI Overlay
      
      //Calculate Display Coords for Moving Objects
         p1XDisplayCoord = (int)(this.getWidth()*0.5 + (this.getWidth()*(p1.getPos().getX() + p1.getVel().getX()*interpolation))/mapSize.getWidth() );      
         p1YDisplayCoord = (int)(this.getHeight()*0.5 - (this.getHeight()*(p1.getPos().getY() + p1.getVel().getY()*interpolation))/mapSize.getHeight()-p1DisplayHeight );
      
         p2XDisplayCoord = (int)(this.getWidth()*0.5 + (this.getWidth()*(p2.getPos().getX() + p2.getVel().getX()*interpolation))/mapSize.getWidth() );      
         p2YDisplayCoord = (int)(this.getHeight()*0.5 - (this.getHeight()*(p2.getPos().getY() + p2.getVel().getY()*interpolation))/mapSize.getHeight()-p2DisplayHeight );
      
         repaint();
      
      }
   
      public void resizeGraphics(){
      
         pauseMatch();
      	
         System.out.println("RESIZING GRAPHICS");
      
      //Just tell the SpriteLoader to resize all of its graphical elements
      //That way, images get handled outside of the Gameplay Class
      //Also helps to disjoint hitboxsize/playercoordinates from displaysize/ display coordinates
      //and allows for hitbox resizing without messing with the images.
               
      //PLAYERS
         p1DisplayWidth = (int)( p1.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p1DisplayHeight = (int)( p1.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
         displayMario = originalMario.getScaledInstance( p1DisplayWidth, p1DisplayHeight, Image.SCALE_SMOOTH );
      
         p2DisplayWidth = (int)( p2.width()*this.getPreferredSize().getWidth()/mapSize.getWidth() );
         p2DisplayHeight = (int)( p2.height()*this.getPreferredSize().getHeight()/mapSize.getHeight() );
         displayLuigi = originalLuigi.getScaledInstance( p2DisplayWidth, p2DisplayHeight, Image.SCALE_SMOOTH );
      
      //BACKGROUND
         bgDispWidth = (int) this.getPreferredSize().getWidth();
         bgDispHeight = (int) this.getPreferredSize().getHeight();
         displayBackground = originalBackground.getScaledInstance( bgDispWidth, bgDispHeight, Image.SCALE_FAST );
      //Start text gif
         displayStartText = originalStartText.getScaledInstance( (int)(bgDispWidth*0.5), (int)(bgDispHeight*0.5), Image.SCALE_FAST );
      //MAP TEXTURE
         displayMap = originalMap.getScaledInstance( bgDispWidth, bgDispHeight, Image.SCALE_SMOOTH );
      
         resumeMatch();
      
      }
   
   //TEST ONLY
      @Override
      public synchronized void paintComponent(Graphics g) {
         super.paintComponent(g);
      
         Graphics2D g2d = (Graphics2D) g;
      
      //Background
         g2d.drawImage( displayBackground, 0, 0, null );
      //Stage Image
         g2d.drawImage( displayMap, 0, 0, null );
      //Players (Hitboxes, Images, and Stats)
         for( Player p: players ){
            if( p.getPlayerNumber() == 1 ){   
               g2d.drawImage( displayMario, p1XDisplayCoord, p1YDisplayCoord, null);
            }
            else{
               g2d.drawImage( displayLuigi, p2XDisplayCoord, p2YDisplayCoord, null);
            }
         }
      
      //Intro
         if( tCount <= 3 ){
            g2d.drawImage( displayStartText, (int)((bgDispWidth-displayStartText.getWidth( null ))*0.5), 0, null );  
         }
         else{    
         //Match Time     
            //g2d.setColor( Color.GRAY ); 
            //g2d.drawString( "ELAPSED TIME (s): " + timeFormat.format((System.nanoTime()-tStart)*0.000000001), 425, 25);
            timeLabel.setText( "ELAPSED TIME (s): " + timeFormat.format((System.nanoTime()-tStart)*0.000000001) + "  " );      
         }
      	
      }
   
   
   }
