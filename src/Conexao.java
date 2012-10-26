
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rft
 */
public class Conexao implements Runnable{
    
    private Socket server;                                                      // variavel que vai receber a referência da instanccia do active Socket
    private String recebido;                                                    // Variavel que vai guardar as mensagens recebidas do inputStream
    private MenuActionListener actionListener = new MenuActionListener();       // variavel que vai receber a referencia do MenuActionListener
    private boolean stop = false;                                               // variavel booleana que define um criterio de paragem do ciclo while
    private volatile String mensagem;                                           // variavel que vai receber as strings de mensagem a serem enviadas do lado do servidor               
    private String client_ip;                                                   // variavel que vai guardar o endereço ip dos clientes
    
    public synchronized void setMensagemServidor(String mensagem){              // metodo que recebe a mensagem a enviar pelo cliente
        this.mensagem = mensagem;
    }
    
    public synchronized String getMensagemServidor(){                           // metodo que retorna a mensagem armazenada para enviar
        return mensagem;
    }
    
  
    
    
    Conexao(Socket server, MenuActionListener actionListener, String client_ip){ // construtor que recebe tres parametos, a instância do socket activo e a instancia do MenuActionListener, e informação do ip do cliente conectado
      this.server = server;
      this.actionListener = actionListener;
      this.client_ip = client_ip;
    }

    public void run () {
        System.out.println("Entrou na conexao");
        
 
      
      try {
        
        DataInputStream in = new DataInputStream (server.getInputStream());     // Cria um canal para receber dados.
        DataOutputStream out = new DataOutputStream(server.getOutputStream());  // Cria um canal para enviar

        
        //while((line = in.readUTF()) != null && !line.equals(".")) {
        while(!stop){
            
            try{   
                recebido = in.readUTF();
            }catch(SocketTimeoutException sto){
                System.out.println("S Timeout do inputstream do servidor");
            }

            if (recebido != null){                                              // Se recebeu informação no imputstream
                   System.out.println("S Mensagem que chegou ao servidor:"+ recebido);
                   actionListener.appendInfo("S Cliente: " + recebido);
                // }

                 // Now write to the client

                 System.out.println("S Overall message is:" + recebido);
                 out.writeUTF("Mensagem \""+ recebido +"\" recebida");          // Envia mensagem de recebimento ao socket cliente
                 recebido = null;                                               // Coloca a variavel recebido a null         
                                 }
            
            if (getMensagemServidor() != null && getMensagemServidor().length() > 0){                                // caso haja uma mensagem nova a enviar
                    out.writeUTF( getMensagemServidor() );                      //Envia a string mensagem.
                    out.flush();  
                    setMensagemServidor(null);                                  // coloca a string a null para nao repetir envio
                                               }
            
            
      System.out.println("S Fim de ciclo");
        }
        
        server.close();
      } catch (IOException ioe) {
        System.out.println("IOException on socket listen: " + ioe);
        ioe.printStackTrace();
      }
    }
}

