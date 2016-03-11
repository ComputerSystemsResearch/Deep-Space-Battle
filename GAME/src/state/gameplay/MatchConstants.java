package GAME.src.state.gameplay;  
	
import java.awt.event.KeyEvent;  
	
public interface MatchConstants{
   final int CODE_P1_UP = KeyEvent.VK_W;
   final int CODE_P1_DOWN = KeyEvent.VK_S;
   final int CODE_P1_LEFT = KeyEvent.VK_A;
   final int CODE_P1_RIGHT = KeyEvent.VK_D;
   final int CODE_P1_BLOCK = KeyEvent.VK_I;
   final int CODE_P1_PRIMARY = KeyEvent.VK_O;
   final int CODE_P1_SECONDARY = KeyEvent.VK_P;

   final int CODE_P2_UP =  KeyEvent.VK_UP;
   final int CODE_P2_DOWN = KeyEvent.VK_DOWN;
   final int CODE_P2_LEFT = KeyEvent.VK_LEFT;
   final int CODE_P2_RIGHT = KeyEvent.VK_RIGHT;
   final int CODE_P2_BLOCK = KeyEvent.VK_NUMPAD1;
   final int CODE_P2_PRIMARY = KeyEvent.VK_NUMPAD2;
   final int CODE_P2_SECONDARY = KeyEvent.VK_NUMPAD3;
   	
   final int CODE_PAUSE = KeyEvent.VK_ESCAPE;
}