

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/////////////////MenuBar aparece na zona de cima do programa////////////////////
public class MenuBar extends JMenuBar {

  private MenuActionListener menuActionListener;

  public MenuBar(MenuActionListener menuActionListener) {                       // construtor que recebe a referencia do menuActionListener
    try {
      this.menuActionListener = menuActionListener;
      initialize();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void initialize() throws Exception {
    JMenu menuFile = new JMenu("Ficheiro");
       
    JMenuItem itemExit = new JMenuItem("Sair");                                 // cria um submenu
    itemExit.addActionListener(menuActionListener);                             // adiciona o actionLister ao item
    itemExit.setActionCommand("Sair");                                          // define a string pela qual vai responder o actionListener
    menuFile.add(itemExit);                                                     // adiciona o item ao menu

    add(menuFile);
    
    ////////////////////////////////////////////////////////////////////////////
    JMenu menuConf = new JMenu("Configurações");
    
    JMenuItem itemPorto = new JMenuItem("Porto");
    itemPorto.addActionListener(menuActionListener);
    itemPorto.setActionCommand("Porto");
    menuConf.add(itemPorto);
    
    add(menuConf);
    
    ////////////////////////////////////////////////////////////////////////////
    JMenu menuHelp = new JMenu("Ajuda");
    
    JMenuItem itemAjuda = new JMenuItem("Instruções");
    itemAjuda.addActionListener(menuActionListener);
    itemAjuda.setActionCommand("Instruções");
    menuHelp.add(itemAjuda);
    
    add(menuHelp);
    
    ////////////////////////////////////////////////////////////////////////////
    JMenu menuSobre = new JMenu("Sobre");
    menuSobre.addActionListener(menuActionListener);
    menuSobre.setActionCommand("Sobre");
    
    add(menuSobre);
  }
}