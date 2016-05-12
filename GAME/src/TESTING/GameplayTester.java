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
   import java.awt.event.KeyListener;
   import java.awt.event.KeyEvent;
   import java.awt.Rectangle;

   import java.util.Scanner;
   import java.lang.Thread;

   import GAME.src.state.gameplay.*;
   import GAME.src.maps.data.*;

   public class GameplayTester {
   
      private JFrame frame;
      private JPanel container;
      private Gameplay gameplay;
      private Boot mapBoot;
   
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
         initializeTester();
      }
   
      private void initializeTester(){
      
         System.out.println("Initializing GameplayTester...");
      
      //main frame
         frame = new JFrame( "GameplayTester" );
         frame.setSize( new Dimension( 1200, 800 ) );
         frame.setMinimumSize( new Dimension( 400, 225 ) );
         frame.setLocation( 50, 50 );
         frame.setResizable( true );
         frame.setVisible( true );
         frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      
      //container border frame
         container = new JPanel();
         container.setLayout( new GridBagLayout() );      
         container.setPreferredSize( new Dimension( 800, 450 ) );  
      //container.setBackground( Color.WHITE ); 
         
         container.addComponentListener( 
               new ComponentAdapter()
               {
                  @Override
                  public void componentResized(ComponentEvent evt) {
                     resizeInnerPanel();
                  }
               });
      		
      //Boot
         mapBoot = new Boot(); // needs: chosen map
      
      //gameplay screen
         gameplay = new Gameplay( mapBoot ); // needs: characters, map, matchType and matchInfo ( time allotted or stock )
         gameplay.setFocusable( true ); 
      
      //Displaying the panels    
         frame.getContentPane().add(container);
         container.add( gameplay );
         gameplay.add( mapBoot );  //necessary while Boot is generatng a buffer strategy
         frame.pack();
      
         System.out.println("GameplayTester Initialized");
      
         gameplay.start();
      
      }
   
      public void resizeInnerPanel(){
      
         gameplay.resizePanel();
      
      }
   
   
   }