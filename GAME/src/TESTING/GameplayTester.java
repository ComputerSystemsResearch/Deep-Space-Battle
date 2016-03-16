   package GAME.src.TESTING;  

   import javax.swing.JFrame;
   import javax.swing.JPanel;

   import java.awt.GridBagConstraints;
   import java.awt.GridBagLayout;
   import java.awt.BorderLayout;
   import java.awt.Color;
   import java.awt.EventQueue;
   import java.awt.Dimension;
   import java.awt.Component;
   import java.awt.event.ComponentListener;
   import java.awt.event.ComponentEvent;
   import java.awt.event.ComponentAdapter;
   import java.awt.Rectangle;

   import java.util.Scanner;
   import java.lang.Thread;

   import GAME.src.state.gameplay.*;

   public class GameplayTester extends JFrame {
   
      private JFrame frame;
      private JPanel container;
      private Gameplay gameplay; 
   
      public static void main(String[] args) {
      
         EventQueue.invokeLater(
               new Runnable() {
                  public void run() {
                     try {
                        GameplayTester window = new GameplayTester();
                        window.frame.setVisible(true);
                     } 
                        catch (Exception e) {
                           e.printStackTrace();
                        }
                  }
               });
      
      }
      public GameplayTester(){
         initializeGame();
      }
   
      private void initializeGame(){
      
         System.out.println("Initializing GameplayTester...");
      
      //main frame
         frame = new JFrame( "GameplayTester" );
         frame.setSize( new Dimension( 1200, 800 ) );
         frame.setMinimumSize( new Dimension( 400, 225 ) );
         frame.setLocation( 50, 50 );
         frame.setResizable( true );
         frame.setVisible( true );
         frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
         frame.setBackground( Color.WHITE );
      
      //gameplay screen
         gameplay = new Gameplay();
         gameplay.setFocusable( true );
      
      //container border frame
         container = new JPanel();
         container.setLayout( new GridBagLayout() );      
         container.setPreferredSize( new Dimension( 800, 450 ) );  
         container.setBackground( Color.BLACK ); 
         container.add( gameplay );
                        
         container.addComponentListener( 
               new ComponentAdapter()
               {
                  @Override
                  public void componentResized(ComponentEvent evt) {
                     resizeInnerPanel();
                  }
               });
      
      //Displaying the panels    
         frame.getContentPane().add(container);
      
         frame.pack();
      
         System.out.println("GameplayTester Initialized.");
      
      }
   
      public void resizeInnerPanel(){
      
      //Could send this off to a method within the Gameplay class
      //and then return back once done.
         
         //gameplay.pauseMatch();
      	
         int parentX = (int)( container.getWidth() );
         int parentY = (int)( container.getHeight() );
         double currentAspectRatio = 1.0*parentX/parentY;
         Dimension max = gameplay.getMaximumSize();
         int xMax = (int)( max.getWidth() );
         int yMax = (int)( max.getHeight() );
         Dimension d;        	
      
         if( parentX >= xMax && parentY >= yMax ){
            gameplay.setPreferredSize( gameplay.getMaximumSize() );
         }
         else {
         
            if( currentAspectRatio >= gameplay.getDesiredAspectRatio() )
               d = new Dimension( (int)( 1.0*parentY * gameplay.getDesiredAspectRatio() ), parentY );
            else
               d = new Dimension( parentX, (int)(1.0*parentX / gameplay.getDesiredAspectRatio() ) );
         
            if( d.getWidth() <= xMax && d.getHeight() <= yMax ){ 
               gameplay.setPreferredSize( d );
               gameplay.setMinimumSize( d );
            }
         
         }
      
         container.revalidate();
         gameplay.resizeGraphics();
      
      }
   
   }