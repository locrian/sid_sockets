

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class is for enabling the closing of a window.
 * 
 */
public class MyFinishWindow extends WindowAdapter {
  public void windowClosing(WindowEvent e) {
    System.exit(0);
  }
}
