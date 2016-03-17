   package GAME.src.matchClasses;

   public class Vector2D{
   
      private double xComponent;
      private double yComponent;
   	
      public Vector2D( double x, double y ){
         xComponent = x;
         yComponent = y;
      }
   	
      public double getX(){
         return xComponent;
      }
   	
      public double getY(){
         return yComponent;
      }
   	
      public double getMagnitude(){
         return Math.sqrt( Math.pow( getX(), 2 ) + Math.pow( getY(), 2 ) );
      }
   	
      public void set( double x, double y ){
         xComponent = x;
         yComponent = y;
      }
   	
      public void setX( double x ){
         xComponent = x;
      }
   	
      public void setY( double y ){
         yComponent = y;
      }
   	
      public void scale( double xScale, double yScale ){
         setX( getX() * xScale );
         setY( getY() * yScale );
      }
   }