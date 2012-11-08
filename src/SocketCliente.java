
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
public class SocketCliente implements Runnable{
    
    private MenuActionListener actionListener = new MenuActionListener();       // Variavel que vai conter a referência ao MenuActionListener
    private String endereco = new String();                                     // variavel que vai guardar o endereco do servidor
    private int porto;                                                          // variavel que vai guardar o porto do servidor
    private volatile String mensagem;                                           // variavel que vai receber a mensagem escrita pelo cliente
    private boolean stop = false;                                               // variavel booleana que define se o ciclo de escuta escrita se mantem em execução
    private String recebido;                                                    // variavel que armazena o conteudo do inputstream
    private PrivateKey chave_privada_c;
    private Cipher cipher_s;                                                    // cipher para desencriptar no servidor
    private Cipher cipher_c;                                                    // cipher para ecriptar para o cliente
    private PublicKey chave_publica_c;
    private PublicKey chave_publica_s;
    private byte[] encrypted;
    private boolean first_con_envio = true;                                     // variavel que se for true envia a public key ao servidor como primeira mensagem
    private boolean first_con_recebido = true;                                  // variavel que se for true indica o recebimento da chave publica do servidor
    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////Gets e Sets////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    
    public synchronized void setMensagemCliente(String mensagem){               // metodo que recebe a mensagem a enviar pelo cliente
        this.mensagem = mensagem;
    }
    
    public synchronized String getMensagemCliente(){                            // metodo que retorna a mensagem armazenada para enviar
        return mensagem;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //////////////////Gerar as chaves publicas e privadas///////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private void generateKeyPair(){
    try {
        KeyPairGenerator keyPg = KeyPairGenerator.getInstance("RSA");           // Gerador de chaves pubicas e privadas
        keyPg.initialize(1024);                                                 // Inicializa o KeyPair especificando o tamanho da chave e o algoritmo
        KeyPair pair = keyPg.generateKeyPair();                                 // Gera a chaves
        chave_privada_c = pair.getPrivate();                                    // cria uma instancia de PrivateKey através da chave privada gerada
        chave_publica_c = pair.getPublic();                                     // cria uma instância de PublicKey através da chave pública gerada

    
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
        cipher_c.init(Cipher.ENCRYPT_MODE, chave_publica_s);                    // Inicializa o cipher com a chave publica do servidor     
        
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
      if (chave_publica_s == null)
          System.out.println("A chave de encriptação do servidor ainda nao esta disponivel");
      System.out.println("C Mensagem encriptada: "+encrypted);
      return encrypted;                                                         // retorna a mensagem encriptada
      
                                  }
    ////////////////////////////////////////////////////////////////////////////
    
    public SocketCliente(MenuActionListener actionListener, String endereco, int porto, String mensagem){   // Construtor com parametros para receber a referencia do objeto MenuActionListener
        this.actionListener = actionListener;     
        this.endereco = endereco;
        this.porto = porto;
        this.mensagem = mensagem;
    }
    
    
    public void run(){                                                          // Sobreposição do método run da interface Runnable
        
        generateKeyPair();                                                      // gera as chaves publica e privada
        
            //Conectar ao servidor.
        try{
                Socket socket_client = new Socket(endereco, porto);                     // Criar um novo Socket 
                socket_client.setSoTimeout(1000);                                       // Especifica um timeout para o inputstream, para nao ficar bloquado a espera de dados


                 BufferedReader in_c = new BufferedReader(new InputStreamReader(socket_client.getInputStream()));    //Cria um canal para receber dados.

                 PrintWriter out_c = new PrintWriter(socket_client.getOutputStream()); //Cria um canal para enviar dados

                 
           while(!stop){
          /*   try{   
                 if (first_con_recebido == true){                               // se for a primeira conexao
                    byte[] temp_chave_s = new byte[4];                          // cria um novo array de bytes com tamanho 4
                    socket_client.getInputStream().read(temp_chave_s,0,4);      // 
                    ByteBuffer bb = ByteBuffer.wrap(temp_chave_s);              // cria um buffer de bytes com tamanho baseado no byte temp_chave_c
                    int tamanho = bb.getInt();                                  // guarda o tamanho do bytebuffer
                    System.out.println(tamanho);                                // debug
                    byte[] temp_chave_s_bytes = new byte[tamanho];              // cria um novo array de bytes com o tamanho do bytebuffer 
                    socket_client.getInputStream().read(temp_chave_s_bytes);    //
                    System.out.println(DatatypeConverter.printHexBinary(temp_chave_s_bytes));         //debug
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(temp_chave_s_bytes);    //
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
                    chave_publica_s = keyFactory.generatePublic(keySpec);       // 
                    System.out.println(DatatypeConverter.printHexBinary(chave_publica_s.getEncoded()));
                    first_con_recebido = false;                                 // coloca a variavel booleana em false
                                                 }
             }catch(NoSuchAlgorithmException e){
                        System.out.println(e);
             }catch(InvalidKeySpecException e){
                         System.out.println(e);
                        }  
                        */
                    try{                                                        // uma vez que especificamos um timeout para o inputstream necessitamos do try/catch
                        recebido = in_c.readLine();                         // verifica se há mensagens a receber no inputStream
                    }catch(SocketTimeoutException sto){
                        System.out.println("C Timeout do inputstream");                 
                    }
                        System.out.println("C Socket cliente: "+ getMensagemCliente() );// debug

                    
                    if (recebido != null){                                      // Se recebeu informação no imputstream
                     /*  try{   
                        if (first_con_recebido){
                            System.out.println("C A receber public key do servidor"); //debug
                            byte[] temp_chave = recebido.getBytes();            // Recebe a chave publica do servidor como um array de bytes
                            chave_publica_s = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(temp_chave)); // Passa o array de bytes para a instancia de chave publica do servidor deste objeto
                            first_con_recebido = false;                         // coloca a variavel booleana em false
                            recebido = null;
                        }else{ */
                        
                           System.out.println("C Mensagem que chegou ao cliente:"+ recebido); //debug
                           actionListener.appendInfo("C Servidor: " + recebido);        // envia a informação para a JTextAreaInfo
                           recebido = null;                                             // Coloca a mensagem a null para nao voltar a repetir
                         /*    }  
                        }catch(NoSuchAlgorithmException e){
                        System.out.println(e);
                        }catch(InvalidKeySpecException e){
                         System.out.println("C Erro no try de recebimento da chave "+ e);
                        } */
                                          }
                    
                    

                     if (first_con_envio){                                      // caso seja o primeiro contacto com o servidor
                      try{  
                         System.out.println("C chave_publica: "+DatatypeConverter.printHexBinary(chave_publica_c.getEncoded()));
                         ByteBuffer bb = ByteBuffer.allocate(4);
                         bb.putInt(chave_publica_c.getEncoded().length);
                         socket_client.getOutputStream().write(bb.array());
                         socket_client.getOutputStream().write(chave_publica_c.getEncoded());
                         socket_client.getOutputStream().flush();
                        
                         byte[] temp_chave_s = new byte[4];                          // cria um novo array de bytes com tamanho 4
                         socket_client.getInputStream().read(temp_chave_s,0,4);      // 
                         ByteBuffer bb_s = ByteBuffer.wrap(temp_chave_s);              // cria um buffer de bytes com tamanho baseado no byte temp_chave_c
                         int tamanho = bb_s.getInt();                                  // guarda o tamanho do bytebuffer
                         System.out.println(tamanho);                                // debug
                         byte[] temp_chave_s_bytes = new byte[tamanho];              // cria um novo array de bytes com o tamanho do bytebuffer 
                         socket_client.getInputStream().read(temp_chave_s_bytes);    //
                         System.out.println(DatatypeConverter.printHexBinary(temp_chave_s_bytes));         //debug
                         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(temp_chave_s_bytes);    //
                         KeyFactory keyFactory = KeyFactory.getInstance("RSA"); 
                         chave_publica_s = keyFactory.generatePublic(keySpec);       // 
                         System.out.println(DatatypeConverter.printHexBinary(chave_publica_s.getEncoded()));
                         first_con_envio = false;                               // coloca a variavel first_con_envio em false
                    }catch(NoSuchAlgorithmException e){
                        System.out.println(e);
                    }catch(InvalidKeySpecException e){
                        System.out.println(e);    
                    }     
                     }else{

                         if (getMensagemCliente() != null && getMensagemCliente().length() > 0){                            // caso haja uma mensagem nova a enviar
                            System.out.println("C Mensagem a enviar nao encriptada "+ getMensagemCliente()); // debug
                            out_c.println( encrypt(getMensagemCliente()) );                      //Envia a string mensagem.
                            out_c.flush();  
                            setMensagemCliente(null);                                   // coloca a string a null para nao repetir envio
                                                                                               }
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
