package Client_Side;
//============================================

import java.io.*;

//============================================
public class ClientTalker
{
    ClientTalker()
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
        System.out.println("waiting to receive");
        str = inStream.readLine();
        System.out.println("<<" + str);
        return str;
     }

   
    }

