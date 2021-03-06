
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rft
 */
public class SocketServidor implements Runnable{
    
    private MenuActionListener actionListener = new MenuActionListener();       // variavel que vai receber a referencia do MenuActionListener
    private int porto;                                                          // variavel que recebe informação do porto de escuta a usar                                   
    private volatile boolean stop = false;                                      // variavel que vai guardar um valor boleano que vai controlar o termino da Thread do socket
    private ServerSocket socket_servidor;                                       // variavel que vai guardar a instancia do ServerSocket
    private DataInputStream in_s;                                               // reserva endereço de memória para canal de receber dados       
    private DataOutputStream out_s;                                             // reserva endereço de memória para canal de enviar dados 
    private boolean is_servidor = false;                                        // variavel boooleana que guarda informação sobre o papel da aplicação (servidor ou cliente)
    private ArrayList<Conexao> vetor_conexoes= new ArrayList<Conexao>();        // cria um ArrayList com informação de todas as conexões que vao sendo criadas
    private JFrameCustom conversacao;
    
    
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////Gets e Sets/////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public void stopThread(){                                                   // Metodo que recebe informação sobre se é suposto terminar a Thread, por ex para mudar de porto de escuta.
        stop = true;
    }
    
    public void setIsServidor(boolean b){                                       // Recebe o valor booleano a atribuir á variavel "is_servidor"
        this.is_servidor = b;
    }
    
    public boolean getIsServidor(){                                             // Retorna a variavel booleana "is_servidor"
        return is_servidor;
    } 
    
    public void closeServerSocket(){                                            // Metodo que força o fecho do socket de escuta
        try{
            socket_servidor.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    public ArrayList<Conexao> getVetorConexoes(){                               // Retorna o vetor de conexoes
        return vetor_conexoes;
    } 
    
    public void showJanelaConversacao(String ip){                               // Metodo para fazer request da janela de conversação entre o servidor e um determinado cliente
        for (int i = 0; i  < vetor_conexoes.size(); i++ ){                      // Percorre o array de conexoes
            if (vetor_conexoes.get(i).getClientIp().compareTo(ip) == 0)          // Se encontrar a conexao com o ip do cliente selecionado
                vetor_conexoes.get(i).getJanelaConversacao();                    // invoca o método da classe Conexao "getJanelaConversacao"
        }
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    public SocketServidor(MenuActionListener actionListener, int porto){        // Construtor com parametros para receber a referencia do objeto MenuActionListener e porto
        this.actionListener = actionListener;     
        this.porto = porto;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    
    public void run(){                                                          // Sobreposição de método run da interface Runnable
             
    
        while(!stop){                                                           // corre enquanto a ordem de paragem nao for dada, de modo a ficar á escuta de vários clientes
           try{
               socket_servidor = new ServerSocket(porto);                       // cria um novo server socket com a porta 4444 


               /////////////////Ciclo de espera//////////////////////////////////////////
               long t0, t1;                                                     // variaveis temporais para controlar uma espera
               t0 =  System.currentTimeMillis();                                // variavel que guarda o momemnto actual em ml para usar no ciclo "do" de espera
                                                                                // Esta espera é meramente estética
               do{
                   t1 = System.currentTimeMillis();
               }while((t1-t0) < (500));
               //////////////////////////////////////////////////////////////////////////


               actionListener.appendInfo("A criar socket de escuta...");        // envia info para a JTexArea info
               actionListener.appendInfo("A espera de conexões...");            // envia info e para a JTexArea info

               Socket socket = socket_servidor.accept();                        // Aguarda por uma ligação ao socket de escuta e cria um socket activo
               socket.setSoTimeout(1000);                                       // Especifica o timeout de espera pelo imputstream para nao bloquear a espera de dados
               setIsServidor(true);                                             // Se o socket aceitou uma conexão entao é servidor

               actionListener.appendInfo("Socket activo criado no porto: "+socket.getPort());    //Envia para a JTextAreaInfo a informação do Porto do socket activo gerado
               actionListener.appendInfo("Utilizador com o endereço "+socket.getInetAddress().toString().replace("/", "") +" conectado...");                // Envia info para a JTexArea info
               actionListener.addClientesToList(socket.getInetAddress().toString().replace("/", ""));     // adiciona o ip do cliente conectado á java.awt.List

               /////////////////////////////////////////////////////////////////
               ///////////Constroi um JFrame para a conversação com o cliente///   

               conversacao = new JFrameCustom(actionListener);
               conversacao.setTamanho(400, 400);
               conversacao.setTitulo("Conversação com "+ socket.getInetAddress().toString().replace("/", "") );
               conversacao.constroiFrame();
               conversacao.show();

              //////////////////////////////////////////////////////////////////       

              Conexao con_c= new Conexao(socket, actionListener, socket.getInetAddress().toString().replace("/", ""), conversacao);// Cria uma instancia do objecto "conexao" que recebe a referência do socket activo, do MenuActionListener, e informação do ip do cliente e da janela de conversação
              Thread trd = new Thread(con_c);                                        // cria uma nova Thread para a instancia de conexao
              trd.start();                                                           // inicia a thread
              vetor_conexoes.add(con_c);                                             // Adiciona a conexao ao arrayListe de conexoes  
              actionListener.setConexaoAtiva();                                      // Invoca o método setConexaoActiva do menuActionListener
              conversacao.setConexao(con_c);                                         // Envia a referência da conexao para instancia de JFrameCustom conversacao

              socket_servidor.close();                                                  // termina o servidor de escuta

          }catch(SocketException se){
              System.out.println("Socket Excepion "+ se);
              ////////////////Ciclo de espera para dar tempo ao Menu ///////////////
              //////////////////para mudar o porto do servidor//////////////////////
              long t0, t1;                                                          // variaveis temporais para controlar uma espera
              t0 =  System.currentTimeMillis();                                     // variavel que guarda o momento actual em ml para usar no ciclo "do" de espera

              do{
                  t1 = System.currentTimeMillis();
              }while((t1-t0) < (500));
              ////////////////////////////////////////////////////////////////////////// 
          }catch(IOException e){                                                
              System.out.println("IO error Server "+ e);
              e.printStackTrace();
          }

        }
    }
    
}
