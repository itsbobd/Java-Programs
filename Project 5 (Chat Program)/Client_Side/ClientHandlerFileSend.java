package Client_Side;
//============================================

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JOptionPane;

//============================================
public class ClientHandlerFileSend
                        implements Runnable
{
    DataOutputStream netOutStream;
    FileInputStream inFileStream;
    Vector<File> fileVector;
    String ip;
    String port;
    String fileName;
    UserFrame frame;
    final int BLOCKSIZE = 64;


public ClientHandlerFileSend(Vector<File> fileVector, String ip, String port, String fileName, UserFrame frame)
{
    this.fileVector = fileVector;
    this.ip = ip;
    this.port = port;
    this.fileName = fileName;
    this.frame = frame;
}

public void run()
{
    try
    {
        Socket newSocket = new Socket(InetAddress.getByName(ip), Integer.parseInt(port));

        netOutStream = new DataOutputStream(new BufferedOutputStream(newSocket.getOutputStream())); //construct net out stream

        //first, send file name
        netOutStream.writeUTF(fileName);

        for (int i = 0; i < frame.fileVector.size(); i++)
        {
            if (frame.fileVector.elementAt(i).getName().startsWith(fileName))
            {
                inFileStream = new FileInputStream(frame.fileVector.elementAt(i).getAbsolutePath()); //gets entire path of file, construct file in stream
            }
        }

        //remove file from vector

        if (inFileStream != null)
        {
            byte[] buffer;

            buffer = new byte[BLOCKSIZE];

            int numBytesRead = inFileStream.read(buffer, 0, 64);

            while (numBytesRead >= 0)
            {
                netOutStream.write(buffer, 0, numBytesRead);

                numBytesRead = inFileStream.read(buffer, 0, 64);
            }

            inFileStream.close();
            netOutStream.close(); //close streams
        }

        else //file must have been moved or deleted
        {
            JOptionPane.showMessageDialog(frame, "Could not find file, " + fileName + ", to send...");
        }
    }

    catch (IOException ex)
    {
        JOptionPane.showMessageDialog(frame, "Error when attempting to send file: " + fileName);
    }
       
}

}
