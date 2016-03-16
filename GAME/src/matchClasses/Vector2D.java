   package GAME.src.matchClasses;

   public class Vector2D{
   
      private double xComponent;
      private double yComponent;
   	
      public Vector2D( double x, double y ){
         xComponent = x;
         yComponent = y;
      }
   	
      public double getXComponent(){
         return xComponent;
      }
   	
      public double getYComponent(){
         return yComponent;
      }
   	
      public double getMagnitude(){
         return Math.sqrt( Math.pow( getXComponent(), 2 ) + Math.pow( getYComponent(), 2 ) );
      }
   	
      public void set( double x, double y ){
         xComponent = x;
         yComponent = y;
      }
   	
      public void setXComponent( double x ){
         xComponent = x;
      }
   	
      public void setYComponent( double y ){
         yComponent = y;
      }
   	
      public void scale( double xScale, double yScale ){
         setXComponent( getXComponent() * xScale );
         setYComponent( getYComponent() * yScale );
      }
   }