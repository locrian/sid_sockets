

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class MenuActionListener implements ActionListener{

  private Menu menu;                                                          // variavel que vai receber a referência do Menu principal
  
  // Declaração dos elementos gráficos
  private JPanel painel = new JPanel();                                         // novo JPanel
  private JLabel hostName = new JLabel();                                       // vai receber o hostname da maquina local
  private JLabel porto_label_s = new JLabel();                                  // vai receber informação sobre o porto a ser usado na comunicação
  private JTextArea info = new JTextArea();                                     // JTextArea para informações
  private JScrollPane scrollPane = new JScrollPane(info);                       // JScrollPane para colocar a JTextArea Info
  private Border border = new LineBorder(Color.GRAY, 1);                        // border para as JTextArea
  private JLabel aEnviar = new JLabel();                                        // JLabel para texto "Texto a enviar para"
  private JTextField nome_servidor = new JTextField();                          // JTextField para receber o nome do servidor remoto
  private JLabel porto_label_c = new JLabel();                                  // JLabel para texto "Texto a enviar para"
  private JTextField porto_servidor = new JTextField();                         // JTextField para receber o numero do porto do servidor
  private JTextArea mensagem = new JTextArea();                                 // JTextArea para receber a mensagem a enviar ao servidor
  private JButton botao = new JButton();                                        // JButton para dar ordem de envio de mensagem ao servidor remoto
  private JLabel led_servidor = new JLabel();                                   // JLabel que vai servir para identificar visualmente se a aplicação está em modo servidor
  private JLabel led_cliente = new JLabel();                                    // JLabel que vai servir para identificar visualmente se a aplicação está em modo cliente
  private JTextArea clientes_conectados = new JTextArea();                                        // JTextArea para mostrar utilizadores conectados
  private JScrollPane scrollPane_c = new JScrollPane(clientes_conectados);      // JScrollPane para colocar a JTextArea clientes_conectados
  
  private JTextField tempField;                                                 // variavel que é usada na janela temporaria de configuração
  private JFrameCustom tempFrame;                                               // variavel que é usada na janela temporaria de configuração
  private int contador = 0;
  private SocketCliente socket_c;
  private SocketServidor socket_s;
  private int same_machine = 1;                                                 // variavel que define se o cliente e servidor estão na mesma máquina
  private Conexao con;                                                          // variavel que recebe a referencia da Conexao criada pelo socket servidor
  
  //////////////////////////////////////////////////////////////////////////////
  /////////////////////GETS E SETS /////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////
  public void appendInfo(String s){                                             // método que recebe informações para e acrescenta á JTexArea info
      info.append(s+"\n");
  }
  
  public void setInfo(String s){                                                // método que recebe informações para a JTextArea             
      info.setText(s);
  }
  
  public void setPortoLabel(String porto){                                      // metodo que recebe informações sobre o porto actual de escuta do servidor
      porto_label_s.setText(porto);
  }
  
  public void setSocketServidor(SocketServidor socket){
      this.socket_s = socket;
  }
  
  public void setConexaoAtiva(Conexao con){                                     // Recebe a referencia da conexao activa
      this.con = con;
      led_servidor.setBackground(Color.green);                                  // activa o led indicativo como servidor
  } 
  
  
 public MenuActionListener(){
 
 }
          
  public MenuActionListener(Menu menu) {
    this.menu = menu;                                                           // recebe a referência do Menu principal
    
    
    String localHost = "";                                                      // String que vai receber o hostname do pc que inicia a aplicação              
    //ImageIcon paint = new ImageIcon("paint.png");                             // Adiciona uma imagem à janela principal

    try {                                                                       
        java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();// resolve o localhost da maquina local
        localHost = (localMachine.getHostName() +" "+ localMachine.getHostAddress()); // retorna o hostname e ip adress da maquina local
        }catch (java.net.UnknownHostException e) { 
                  System.out.println(e);
                                                 }

    
    //configuração da JLabel hostName
    hostName.setText(localHost);                                                // coloca o hostname na JLabel
    hostName.setBounds(10, 10, 200, 15);                                        // tamanho e localização da JLabel
    
    //configuração da JLabel porto_label_s
    porto_label_s.setText("A receber no porto:");                               // coloca a legenda na JLabel juntamente com o porto
    porto_label_s.setBounds(380, 10, 200, 15);                                  // tamanho e localização da JLabel
    
    //configuração da JTextArea info
    scrollPane.setBounds(20, 40, 400, 180);                                     // configura a posição e tamanho da JTextArea
    scrollPane.setBorder(border);                                               // define o contorno da JTextArea
    info.setEditable(false);                                                    // desablita a edição do componente uma vez que é só de informação
    
    //configuração da JTextArea clientes_conectados
    scrollPane_c.setBounds(430, 40, 140, 180);                                   // configura a posição e tamanho da JTextArea
    scrollPane_c.setBorder(border);                                             // define o contorno da JTextArea
    clientes_conectados.setEditable(false);                                     // desablita a edição do componente uma vez que é só de informação
    
    //configuração da JLabel aEnviar
    aEnviar.setText("Texto a enviar para:");                                    // coloca a legenda na JLabel aEnviar
    aEnviar.setBounds(10, 240, 150, 15);                                        // configura a posição e tamanho da JLabel aEnviar
    
    //configuração do JTextField nome_servidor
    nome_servidor.setBounds(160, 240, 200, 20);                                 // configura a posição e tamanho do JTextField
   

      
    //configuração da JLabel porto_label_c
    porto_label_c.setText("Porto:");                                            // coloca a legenda na JLabel porto_label_c
    porto_label_c.setBounds(450, 240, 50, 15);                                  // configura a posição e tamanho da JLabel porto_label_c
    
    //configuração do JTextField porto_servidor
    porto_servidor.setBounds(500, 240, 70, 20);                                 // configura a posição e tamanho do JTextField
    
    //configuração do JTextArea mensagem
    mensagem.setBounds(20, 280, 400, 140);                                      // configuração da posição e tamanho da JTextArea mensagem
    mensagem.setBorder(border);                                                 // define a border da JtextArea mensagem
    mensagem.setLineWrap(true);                                                 // define a JTextArea como Wrappable
    mensagem.setWrapStyleWord(true);                                            // configura a JTextArea para fazer wrap ao texto se este for demasiado longo
    
    //configuração do JButton botao
    botao.setBounds(430, 280, 140, 140);                                        // configuração do tamanho e posição do JButton botao
    botao.setText("Enviar !");                                                  // define o texto do JButton botao
    botao.addActionListener(this);                                              // adiciona um action listener a este botao
    botao.setActionCommand("botao_enviar");                                     // adiciona um string de comando associado a este botao   
    
    //Configuração da JLabel led_servidor
    led_servidor.setText("");
    led_servidor.setBounds(5, 40, 10, 10);                                       // configura a posição e tamanho da Jlabel led_servidor                                   
    led_servidor.setOpaque(true);
    led_servidor.setBorder(border);
    led_servidor.setBackground(Color.GRAY);
    
    //Configuração da JLabel led_cliente
    led_cliente.setText("");
    led_cliente.setBounds(5, 280, 10, 10);                                      // configura a posição e tamanho da Jlabel led_servidor                                   
    led_cliente.setOpaque(true);
    led_cliente.setBorder(border);
    led_cliente.setBackground(Color.GRAY);
    
    //configuração do Jpanel painel
    painel.setSize(600, 500);                                                   // configura o tamanho do JPanel
    painel.setBounds(0, 0, 600, 500);                                           // define a posição e tamanho do JPanel
    painel.add(hostName);                                                       // adiciona a JLabel hostname ao JPanel
    painel.add(porto_label_s);                                                  // adiciona a JLabel porto ao JPanel
    painel.add(scrollPane);                                                     // adiciona a JScrollPane com a JTextArea info ao JPanel
    painel.add(scrollPane_c);                                                   // adiciona a JScrollPane com a JTextArea clientes_conectados ao JPanel
    painel.add(aEnviar);                                                        // adiciona a Jlabel aEnviar ao JPanel
    painel.add(nome_servidor);                                                  // adiciona o JTextField ao JPanel
    painel.add(porto_label_c);                                                  // adiciona a JTextLabel porto_label_c ao JPanel
    painel.add(porto_servidor);                                                 // adiciona o JTextField porto_servidor ao JPanel
    painel.add(mensagem);                                                       // adiciona o JTextField mensagem ao JPanel
    painel.add(botao);                                                          // adiciona o JButton botao ao JPanel
    painel.add(led_servidor);                                                   // Adiciona a Jlabel led_servidor ao Jpanel
    painel.add(led_cliente);                                                    // Adiciona a Jlabel led_cliente ao JPanel
    
    
    painel.setLayout(null);                                                     // Define o layout do JPanel como null pare se poder posicionar os outros componentes livremente
    
    menu.add(painel);                                                           // adiciona o JPanel ao JFrame principal
    
  }

  
  
  
  public void actionPerformed(ActionEvent e) {                                  // Activado sempre que haja um click ou enter
      
     //System.out.println(e.getActionCommand());                                // Instrução de debug
      
      
      
      
      
     if("botao_enviar".equals(e.getActionCommand())){                           // verifica se essa ativação é causada pelo botao enviar
       
       ///////////Quando cliente////////////////////////////////////////////////  
         
       if (contador == 0 && socket_s.getIsServidor() == false){
        led_cliente.setBackground(Color.green);                                 // Activia o led indicativo como cliente
        socket_c = menu.criarSocketCliente(nome_servidor.getText(), Integer.parseInt(porto_servidor.getText()), mensagem.getText()); // se sim cria um novo socket cliente 
        mensagem.setText("");                                                  // limpa o texto para se poder escrever o proximo texto
         contador++;               
                         }
       else if (contador > 0 && socket_s.getIsServidor() == false){                                                                    // se o contador for maior que 1 significa que já foi criado um socket cliente e por isso apena especifica a nova mensagem a enviar
         socket_c.setMensagemCliente(mensagem.getText());
         mensagem.setText("");
           }
       
       
       ///////////////////Quando Servidor///////////////////////////////////////
       
       if (socket_s.getIsServidor() == true){
           con.setMensagemServidor(mensagem.getText());
           mensagem.setText("");
                                            }
     
     
                                                            }
     
     
     
     if("Sair".equals(e.getActionCommand())){                                   // caso tenha sido pressionado o botão de menu Exit
         System.exit(0);
                                            }
     
      if("Porto".equals(e.getActionCommand())){                                 // caso tenha sido pressionado o botão Porto  
         System.out.println("porto");

         tempFrame = new JFrameCustom(this); 
         tempFrame.setTamanho(300, 100);
         tempFrame.setTitulo("Configuração de porta");
         tempFrame.constroiFrame();
         tempFrame.show();
                                            }
      
      if("Ok_porto".equals(e.getActionCommand())){                              // quando se carrega no ok depois de configurar novo porto...
          if (Integer.parseInt(tempFrame.getTextField().getText()) >= 1024 && Integer.parseInt(tempFrame.getTextField().getText())<= 65535) // testa se o porto escolhido está dentro dos limites aceitaveis
              menu.criarSocketServidor(Integer.parseInt(tempFrame.getTextField().getText()), Integer.parseInt(porto_label_s.getText().replace("A receber no porto: ", "")));      // volta a criar o socket com o novo porto
          else
              this.appendInfo("O porto escolhido é inválido");
          
          tempFrame.dispose();                                                  // fecha a jframe temporaria do menu configuração                                                 
        
                                                 }
      if("Instruções".equals(e.getActionCommand())){                            // caso tenha sido pressionado o botão de menu Ajuda
         tempFrame = new JFrameCustom(this); 
         tempFrame.setTamanho(400, 400);
         tempFrame.setTitulo("Ajuda");
         tempFrame.constroiFrame();
         tempFrame.show();
                                            }
      
      
      
                                            }
    
        
  }


 

