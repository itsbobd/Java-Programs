package Client_Side;
//============================================

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

//============================================
public class ClientHandlerFileReceive 
                        implements Runnable 
{
    Socket socket;
    Vector<ClientHandlerFileReceive> clientHandlers;
    DataInputStream netInStream;
    FileOutputStream outFileStream;
    String fileName;
    final int BLOCKSIZE = 64;
    int numBytesRead;

    public ClientHandlerFileReceive(Socket socket, Vector<ClientHandlerFileReceive> clientHandlers)
    {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
    }

    public void run()
    {
        try
        {
            netInStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            fileName = netInStream.readUTF();

            File file = new File(fileName);
            file.createNewFile();

            outFileStream = new FileOutputStream(file);

            byte[] buffer;
            buffer = new byte[BLOCKSIZE];
            numBytesRead = netInStream.read(buffer, 0, 64);

            while (numBytesRead > 0)
            {
                outFileStream.write(buffer, 0, numBytesRead);

                numBytesRead = netInStream.read(buffer, 0, 64);   
            }

            netInStream.close();
            outFileStream.close();
        }

        catch(IOException ex)
        {
            //stuff
        }
    
        clientHandlers.remove(this);
    }
}


