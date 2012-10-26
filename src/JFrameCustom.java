
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rft
 */
public class JFrameCustom extends JFrame{
    
         private int x;
         private int y;
         private String titulo;
         private MenuActionListener actionListener;
         private JTextField textField_porto;
         
         
   /////////////////////////////////////////////////////////////////////////////
   ////////////////////////////////////GETS E SETS//////////////////////////////
   public JTextField getTextField(){
       return textField_porto;
   }
  
   /////////////////////////////////////////////////////////////////////////////
         
   public JFrameCustom(MenuActionListener actionListener){
       this.actionListener = actionListener;
   }      
         
         
    
   public void setTamanho(int x, int y){
         this.x = x;
         this.y = y;
         }
           
   public void setTitulo(String titulo){
         this.titulo = titulo;
         }
                 

    public void constroiFrame(){
         setBounds(150,150,x,y);
         setTitle(titulo);
         setResizable(false);
         
         JPanel tempPanel = new JPanel();
         tempPanel.setBounds(0,0,x,y);
         tempPanel.setLayout(null);
         
         if (titulo.compareTo("Configuração de porta") == 0){
            JLabel tempLabel = new JLabel("Insira o porto a usar para o servidor.");
            tempLabel.setBounds(20,10,300,20);
            textField_porto = new JTextField();
            textField_porto.setBounds(20, 40, 100, 20);
            JButton button = new JButton("Ok");
            button.setBounds(140,40,60,20);
            button.addActionListener(actionListener);
            button.setActionCommand("Ok_porto"); 
            
            tempPanel.add(tempLabel);
            tempPanel.add(textField_porto);
            tempPanel.add(button);
            
         }
         else if (titulo.compareTo("Ajuda") == 0){
                JTextArea tempTextArea = new JTextArea("\nSocket Servidor: É activado na inicialização do programa.\n\nSocket Cliente: É activado quando se "
                                                        + "é pressionado o botão enviar, com os dados na janela \"texto a enviar para, \" \"Porto:\" "
                                                        + "e com o texto contido na janela de mensagem.\n\nPode mudar o porto de escuta do servidor, acedendo"
                                                        + " ao  menu configurações e selecionando a opção Porto. Tera de inserir um numero entre 1024 e 65535."
                                                        + "\n\nNa janela \"Texto a enviar para\" pode selecionar o Host de destino tanto pelo Host-name como pelo endereço IP."
                                                        + "\n\nPara sair do programa, aceda ao menu Ficheiro e selecione \"Sair\"");
                tempTextArea.setLineWrap(true);
                tempTextArea.setWrapStyleWord(true);
                tempTextArea.setEditable(false);
                tempTextArea.setBounds(0,0,400,400);
            
                tempPanel.add(tempTextArea);
                                                }

            
            
         else if (titulo.compareTo("Sobre") == 0){
                 JLabel tempLabel = new JLabel("lololol.");
                 tempLabel.setBounds(20,10,50,200);
                                                }
            
         
         
         add(tempPanel);
         
         
        
                                        }
}
