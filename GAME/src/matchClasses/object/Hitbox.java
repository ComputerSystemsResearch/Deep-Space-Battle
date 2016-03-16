   package GAME.src.matchClasses.object;

   import java.awt.Dimension;
	
	import GAME.src.matchClasses.Mappable;

   public class Hitbox {
   
      private Dimension size;
      
      public Hitbox(){
         size = new Dimension( 0, 0 );
      }
   	
      public Hitbox( Dimension size ){
         this.size = size;
      }
   
      public Dimension getSize(){
         return size;   
      }
   
      public void resize( Dimension newSize ){
         if( !( newSize == null ) )
            size = newSize;
      }
   
   }