
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rft
 */
public class JFrameCustom extends JFrame implements ActionListener{
    
         private int x;
         private int y;
         private String titulo;
         private MenuActionListener actionListener;
         private JTextField textField_porto;
         private Border border = new LineBorder(Color.GRAY, 1);                 // border para as JTextArea
         
         private JTextArea recebido = new JTextArea();
         private JScrollPane scrollPane = new JScrollPane(recebido);            // JScrollPane para colocar a JTextArea Info
         private JTextArea mensagem;
         private Conexao conexao;
         
   /////////////////////////////////////////////////////////////////////////////
   ////////////////////////////////////GETS E SETS//////////////////////////////
   public JTextField getTextField(){
       return textField_porto;
   }

    public JTextArea getRecebido() {
        return recebido;
    }

    public void setRecebido(JTextArea recebido) {
        this.recebido = recebido;
    }

    public JTextArea getMensagem() {
        return mensagem;
    }

    public void setEnviar(JTextArea mensagem) {
        this.mensagem = mensagem;
    }

 
    public void appendMensagem(String mensagem){
       recebido.append(mensagem+"\n");
    }

    public void setConexao(Conexao conexao) {
        this.conexao = conexao;
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
            
         
         else if (titulo.contains("Conversação")){
                scrollPane.setBounds(5, 50, 385, 200);
                scrollPane.setBorder(border);
                recebido.setLineWrap(true);
                recebido.setWrapStyleWord(true);
                recebido.setEditable(false);
            
                mensagem = new JTextArea();
                
                mensagem.setBounds(5, 270, 300, 100);
                mensagem.setBorder(border);                                                 // define a border da JtextArea mensagem
                mensagem.setLineWrap(true);                                                 // define a JTextArea como Wrappable
                mensagem.setWrapStyleWord(true);                                            // configura a JTextArea para fazer wrap ao texto se este for demasiado longo
                
                
            JButton botao = new JButton("Enviar");
                botao.setBounds(310, 270, 80, 100);
                botao.addActionListener(this);
                botao.setActionCommand("botao_enviar_s");
            
                
                tempPanel.add(scrollPane);
                tempPanel.add(mensagem);
                tempPanel.add(botao);
         }
         
         
         add(tempPanel);
 
                                        }
    
    //////////////////////////////////////////////////////////////////////////// 
    //////////////Captação de eventos///////////////////////////////////////////
    
    public void actionPerformed(ActionEvent e) {
        
       if ("botao_enviar_s".equals(e.getActionCommand())){
           conexao.setMensagemServidor(mensagem.getText());
           mensagem.setText("");
                                                         }
        
                                                }
}
