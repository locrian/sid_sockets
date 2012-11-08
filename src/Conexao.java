
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rft
 */
public class Conexao implements Runnable{
    
    private Socket socket_activo;                                               // variavel que vai receber a referência da instanccia do active Socket
    private String recebido;                                                    // Variavel que vai guardar as mensagens recebidas do inputStream
    private MenuActionListener actionListener = new MenuActionListener();       // variavel que vai receber a referencia do MenuActionListener
    private boolean stop = false;                                               // variavel booleana que define um criterio de paragem do ciclo while
    private volatile String mensagem;                                           // variavel que vai receber as strings de mensagem a serem enviadas do lado do servidor               
    private String client_ip;                                                   // variavel que vai guardar o endereço ip dos clientes
    private JFrameCustom conversacao;
    //private KeyGenerator keyGen;
    private Cipher cipher_s;                                                    // cipher para desencriptar no servidor
    private Cipher cipher_c;                                                    // cipher para ecriptar para o cliente
    private PrivateKey chave_privada_s;
    private PublicKey chave_publica_s;
    private PublicKey chave_publica_c;
    private byte[] encrypted;
    private byte[] decrypted;
    private int first_con = 1;                                                  // variavel que se não for null envia a public key ao cliente como primeira mensagem
    private boolean first_con_envio = true;                                     // variavel que se for true envia a public key ao cliente como primeira mensagem
    private boolean first_con_recebido = true;                                  // variavel que se for true indica o recebimento da chave publica do cliente
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////GETS E SETS//////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public synchronized void setMensagemServidor(String mensagem){              // metodo que recebe a mensagem a enviar pelo cliente
        this.mensagem = mensagem;
    }
    
    public synchronized String getMensagemServidor(){                           // metodo que retorna a mensagem armazenada para enviar
        return mensagem;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////////Gerar as chaves publicas e privadas//////////////
    ////////////////////////////////////////////////////////////////////////////
    private void generateKeyPair(){
    try {
        KeyPairGenerator keyPg = KeyPairGenerator.getInstance("RSA");           // Gerador de chaves pubicas e privadas
        keyPg.initialize(1024);                                                 // Inicializa o KeyPair especificando o tamanho da chave e o algoritmo
        KeyPair pair = keyPg.generateKeyPair();                                 // Gera a chaves
        chave_privada_s = pair.getPrivate();                                    // cria uma instancia de PrivateKey através da chave privada gerada
        chave_publica_s = pair.getPublic();                                     // cria uma instância de PublicKey através da chave pública gerada

    
    }catch(NoSuchAlgorithmException e){   
            System.out.println(e);
    }
    
                                  }
    
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////Encriptar dados///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private byte[] encrypt(String s){
    
      try {  
        //keyGen = KeyGenerator.getInstance("Blowfish");                        // Cria um Key generator baseado no algoritmo BlowFish
        //SecretKey secretkey = keyGen.generateKey();                           // Cria uma secret key 
        cipher_c = Cipher.getInstance("RSA");                                   // Cria um cipher baseado no algoritmo "RSA"
        cipher_c.init(Cipher.ENCRYPT_MODE, chave_publica_c);                    // Inicializa o cipher com a chave publica do cliente    
        
        encrypted = cipher_c.doFinal(s.getBytes());                             // encripta a mensagem a enviar
        
      }catch(NoSuchAlgorithmException e){
          System.out.println(e);
      }catch(NoSuchPaddingException e){
          System.out.println(e);
      }catch(InvalidKeyException e){
          System.out.println(e);
      }catch(IllegalBlockSizeException e){
          System.out.println(e);
      }catch(BadPaddingException e){
          System.out.println(e);
      }
      
      return encrypted;                                                         // retorna a mensagem encriptada
      
                                  }
 
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////Desencriptar dados//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private String decrypt(byte[] mensagem){
      try{
        cipher_s = Cipher.getInstance("RSA");
        cipher_s.init(Cipher.DECRYPT_MODE, chave_privada_s);
        decrypted = cipher_s.doFinal(mensagem);
    }catch(NoSuchAlgorithmException e){
        System.out.println(e);
    }catch(NoSuchPaddingException e){
        System.out.println(e);
    }catch(InvalidKeyException e){
        System.out.println(e);
    }catch(IllegalBlockSizeException e){
        System.out.println(e);
    }catch(BadPaddingException e){
        System.out.println(e);
    }
      return decrypted.toString();
                                          }
  
    ////////////////////////////////////////////////////////////////////////////
    
    Conexao(Socket server, MenuActionListener actionListener, String client_ip, JFrameCustom conversacao){ // construtor que recebe tres parametos, a instância do socket activo e a instancia do MenuActionListener, e informação do ip do cliente conectado
      this.socket_activo = server;
      this.actionListener = actionListener;
      this.client_ip = client_ip;
      this.conversacao = conversacao;
    }

    public void run () {
        System.out.println("Entrou na conexao");
        
        generateKeyPair();                                                     // Invoca o método para gerar as chaves privadas para desencriptação e públicas para encriptação
        
      
      try {
        
        BufferedReader in = new BufferedReader(new InputStreamReader(socket_activo.getInputStream()));     // Cria um canal para receber dados.
        PrintWriter out = new PrintWriter(socket_activo.getOutputStream());  // Cria um canal para enviar

        
        //while((line = in.readUTF()) != null && !line.equals(".")) {
        while(!stop){
            
            try{   
                 if (first_con_recebido == true){                               // se for a primeira conexao
                    byte[] temp_chave_c = new byte[4];                          // cria um novo array de bytes com tamanho 4
                    socket_activo.getInputStream().read(temp_chave_c,0,4);      // 
                    ByteBuffer bb_c = ByteBuffer.wrap(temp_chave_c);              // cria um buffer de bytes com tamanho baseado no byte temp_chave_c
                    int tamanho = bb_c.getInt();                                  // guarda o tamanho do bytebuffer
                    System.out.println(tamanho);                                // debug
                    byte[] temp_chave_c_bytes = new byte[tamanho];              // cria um novo array de bytes com o tamanho do bytebuffer 
                    socket_activo.getInputStream().read(temp_chave_c_bytes);    //
                    System.out.println(DatatypeConverter.printHexBinary(temp_chave_c_bytes));         //debug
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(temp_chave_c_bytes);    //
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
                    chave_publica_c = keyFactory.generatePublic(keySpec);       // 
                    System.out.println("S chave publica do cliente: "+DatatypeConverter.printHexBinary(chave_publica_c.getEncoded()));
                    first_con_recebido = false;                         // coloca a variavel booleana em false
                    
                    System.out.println("S chave_publica propria: "+DatatypeConverter.printHexBinary(chave_publica_s.getEncoded()));
                    ByteBuffer bb_s = ByteBuffer.allocate(4);
                    bb_s.putInt(chave_publica_s.getEncoded().length);
                    socket_activo.getOutputStream().write(bb_s.array());
                    socket_activo.getOutputStream().write(chave_publica_s.getEncoded());
                    socket_activo.getOutputStream().flush();
                    //recebido = null;
                    ///out.println(chave_publica_s.getEncoded());          // envia a chave publica 
                    //first_con_envio = false;                            // coloca a variavel first_con_envio em false 
                                                       }
            }catch(NoSuchAlgorithmException e){
                        System.out.println(e);
            }catch(InvalidKeySpecException e){
                         System.out.println(e);
                        }
            try{   
                recebido = in.readLine();                                        // a variavel "recebido", recebe informação do input buffer
            }catch(SocketTimeoutException sto){
                System.out.println("S Timeout do inputstream do servidor");
            }
            catch(SocketException se){                                          // caso o cliente feche a conexão ao disparar a excepção:
                actionListener.appendInfo("Cliente "+client_ip+" terminou a sessão");  // envia informação para a janela info
                actionListener.removeClientesFromList(client_ip);               // retira o ip do cliente da lista de clientes conectados
                stop = true;                                                    // termina o ciclo de escuta de ccomunicações
            }

            if (recebido != null){                                              // Se recebeu informação no inputstream
                //decrypt(recebido.getBytes());
                System.out.println("S Mensagem que chegou ao servidor encriptada:"+ recebido);
                conversacao.appendMensagem("Cliente: " + decrypt(recebido.getBytes()));
                System.out.println("S Overall message is:" + recebido);
                out.println("Mensagem \""+ recebido +"\" recebida");           // Envia mensagem de recebimento ao socket cliente
                recebido = null;                                               // Coloca a variavel recebido a null         
                                 }
            
            if (getMensagemServidor() != null && getMensagemServidor().length() > 0){                                // caso haja uma mensagem nova a enviar
                    try {
                                   
                        out.println(encrypt(getMensagemServidor()));            // envia a mensagem encriptada
                        //out.println( getMensagemServidor() );                      //Envia a string mensagem.
                        out.flush();  
                        conversacao.appendMensagem("Servidor: "+ getMensagemServidor()); // Coloca a propria mensagem na janela de conversação.
                        setMensagemServidor(null);                                  // coloca a string a null para nao repetir envio
                    }catch(Exception e){
                        System.out.println(e);
                    }
                                                                                    }
            
            
      System.out.println("S Fim de ciclo");
        }
        
        socket_activo.close();                                                         // fecha o socket activo
      } catch (IOException ioe) {
        System.out.println("IOException on socket listen: " + ioe);
        ioe.printStackTrace();
      }
    }
}

