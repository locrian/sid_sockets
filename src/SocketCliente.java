
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rft
 */
public class SocketCliente implements Runnable{
    
    private MenuActionListener actionListener = new MenuActionListener();       // Variavel que vai conter a referência ao MenuActionListener
    private String endereco = new String();                                     // variavel que vai guardar o endereco do servidor
    private int porto;                                                          // variavel que vai guardar o porto do servidor
    private volatile String mensagem;                                           // variavel que vai receber a mensagem escrita pelo cliente
    private boolean stop = false;                                               // variavel booleana que define se o ciclo de escuta escrita se mantem em execução
    private String recebido;                                                    // variavel que armazena o conteudo do inputstream
    
    public synchronized void setMensagemCliente(String mensagem){               // metodo que recebe a mensagem a enviar pelo cliente
        this.mensagem = mensagem;
    }
    
    public synchronized String getMensagemCliente(){                            // metodo que retorna a mensagem armazenada para enviar
        return mensagem;
    }
    
    public SocketCliente(MenuActionListener actionListener, String endereco, int porto, String mensagem){   // Construtor com parametros para receber a referencia do objeto MenuActionListener
        this.actionListener = actionListener;     
        this.endereco = endereco;
        this.porto = porto;
        this.mensagem = mensagem;
    }
    
    
    public void run(){                                                          // Sobreposição do método run da interface Runnable
        
            //Conectar ao servidor.
try{
        Socket socket_client = new Socket(endereco, porto);                     // Criar um novo Socket 
        socket_client.setSoTimeout(1000);                                       // Especifica um timeout para o inputstream, para nao ficar bloquado a espera de dados
     
      
         DataInputStream in_c = new DataInputStream(socket_client.getInputStream());    //Cria um canal para receber dados.
     
         DataOutputStream out_c = new DataOutputStream(socket_client.getOutputStream()); //Cria um canal para enviar dados
       
   while(!stop){
       
            try{                                                                // uma vez que especificamos um timeout para o inputstream necessitamos do try/catch
                recebido = in_c.readUTF();                                      // verifica se há mensagens a receber no inputStream
            }catch(SocketTimeoutException sto){
                System.out.println("C Timeout do inputstream");                 
            }
            
                System.out.println("C Socket cliente: "+ getMensagemCliente() );// debug
                
             if (recebido != null){                                             // Se recebeu informação no imputstream
                   System.out.println("C Mensagem que chegou ao cliente:"+ recebido); //debug
                   actionListener.appendInfo("C Servidor: " + recebido);        // envia a informação para a JTextAreaInfo
                   recebido = null;                                             // Coloca a mensagem a null para nao voltar a repetir
                    
                             }
             
             
                 if (getMensagemCliente() != null && getMensagemCliente().length() > 0){                            // caso haja uma mensagem nova a enviar
                    out_c.writeUTF( getMensagemCliente() );                     //Envia a string mensagem.
                    out_c.flush();  
                    setMensagemCliente(null);                                   // coloca a string a null para nao repetir envio
                                                    }
                 
      System.out.println("C Fim de ciclo");                                     //debug
        }   
      
 
         
         in_c.close();                                                          //Fecha os canais de entrada e saída.
         out_c.close();
         
         socket_client.close();                                                 //Fecha o socket.
        
         
        }catch(UnknownHostException e){
             actionListener.appendInfo("Host Desconhecido");
          
        }catch(IOException e){
             actionListener.appendInfo("Host selecionado não está disponível");
             
        }catch(IllegalArgumentException Ia){
            actionListener.appendInfo("Porto mal inserido");
        }

        
    }
    
}
