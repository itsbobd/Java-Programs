package Server_Side;
//============================================

import java.io.*;
import java.net.*;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

//============================================
public class Server 
{
    ServerSocket serverSocket;
    Socket normalSocket;
    ProfileTable profiles;
    int port = 12345;
    File file;
    DataInputStream dis;
    DataOutputStream dos;

    SSLContext              sslContext;
    KeyManagerFactory       keyManagerFactory;
    KeyStore                keyStore;
    char[]                  keyStorePassphrase;

    SSLServerSocketFactory  sslServerSocketFactory;
    SSLServerSocket         sslServerSocket;
    SSLSocket               sslNormalSocket;

//============================================ Constructor
Server()
{
    try
    {
        file = new File("hastable.txt");
        
        if (!file.exists()) //if a file doesn't exist, it'll create one
        {
            file.createNewFile();
            System.out.println("Created new hashtable file: " + file.getName());
        }

        dis = new DataInputStream(new FileInputStream(file)); //creating input stream for hashtable file

        dos = new DataOutputStream(new FileOutputStream(file, true)); //creating output stream for hashtable file

        if (file.length() != 0) //if the file isn't empty (has users) then load it to hastable
        {
            profiles = new ProfileTable(dis);
        }

        else //then create an empty hastable
        {
            profiles = new ProfileTable();
        }

    }

    catch (Exception ex)
    {
        System.out.println("Error when trying to create/open hashtable file");
    }
        
    try
    {
        sslContext = SSLContext.getInstance("SSL");

        keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyStore  = KeyStore.getInstance("JKS");
        keyStorePassphrase = "passphrase".toCharArray();
        keyStore.load(new FileInputStream("testkeys"), keyStorePassphrase);

        keyManagerFactory.init(keyStore, keyStorePassphrase);

        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        sslServerSocketFactory = sslContext.getServerSocketFactory();
        System.out.println("Got a server socket factory...");

        sslServerSocket =(SSLServerSocket)sslServerSocketFactory.createServerSocket(port);
        System.out.println("Got a secure server socket...");
        try {Thread.sleep(1000);} catch(Exception ee){}

        System.out.println("Server started on port " + port);

    
        while (true) //waiting for new clients to connect
        {
            sslNormalSocket = (SSLSocket)sslServerSocket.accept();
            System.out.println("Got a secure normal socket...");

            System.out.println("A new client is attempting to connect");

            ConnectionToClient CTC = new ConnectionToClient(sslNormalSocket, new ServerTalker(), profiles, file);
            new Thread(CTC).start();
        }
    }

    catch(Exception ex)
    {
        System.out.println("Could not listen on port " + port);
        ex.printStackTrace();
    }

} //end of constructor


}    