   package GAME.src.state.gameplay;  
	
   import java.awt.event.KeyEvent;  
	
   public final class MatchConstants{
   	
      public static final double VIEWPORT_ASPECT_RATIO = (16.0/9);
   	
		public static final int PAUSE_KEY_CODE = KeyEvent.VK_ESCAPE;
      public static final int[] P1_KEY_CODES = new int[] { KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D,
                                        KeyEvent.VK_I, KeyEvent.VK_O, KeyEvent.VK_P };
      public static final int[] P2_KEY_CODES = new int[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                                        KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3 };
   	
      public static final int TICKS_PER_SECOND = 25;
      public static final int SKIP_TICKS = 1000000000 / TICKS_PER_SECOND;
      public static final int MAX_FRAMESKIP = 5;
   	
      private MatchConstants(){
         throw new AssertionError();
      }
   }