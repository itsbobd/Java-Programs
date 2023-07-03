package Server_Side;
//============================================

import java.io.*;

//============================================
public class ServerTalker 
{
    ServerTalker()
    {

    } 
     
     void send(String msg, DataOutputStream outStream) throws IOException
     {
        outStream.writeBytes(msg + '\n');
        System.out.println(">>" + msg);
     }

     String receive(BufferedReader inStream) throws IOException
     {
        String str;
        System.out.println("waiting to receive (ServerSide)");
        str = inStream.readLine();
        System.out.println("<<" + str);
        return str;
     }

   
    }
