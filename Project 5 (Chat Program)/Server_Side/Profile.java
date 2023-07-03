package Server_Side;
//============================================

import java.io.*;
import java.util.*;

//============================================
public class Profile
{
    String username;
    String password;
    Vector<String> friendsList; //will be added later
    ConnectionToClient CTC;
    String name;

//============================================ Constructor
Profile(String u, String p, ConnectionToClient ctc)
{
    username = u;
    password = p;
    CTC = ctc;
    friendsList = new Vector<String>(); //creates empty friends list
    
} //end of constructor

void store(DataOutputStream dos) throws IOException
{
    dos.writeUTF(username); //writes strings to file
    dos.writeUTF(password);

    dos.writeInt(friendsList.size()); //write in size of friends list

    if (!(friendsList.isEmpty()))
    {
        for (int i = 0; i < friendsList.size(); i++)
        {
            dos.writeUTF(friendsList.elementAt(i));
        }  
    }
}

Profile(DataInputStream dis) throws IOException
{
    username = dis.readUTF();
    password = dis.readUTF(); //reads strings from file

    Integer listSize = dis.readInt(); //reads size of list from file

    friendsList = new Vector<String>(); //creates empty friends list

    for (int i = 0; i < listSize; i++)
    {
        friendsList.add(dis.readUTF());
    }

}

public String sendFriends()
{
    String friends = Integer.toString(friendsList.size());

    for (int i = 0; i < friendsList.size(); i++)
    {
        friends = friends.concat(" " + friendsList.elementAt(i));
    }

    return friends; //returns list of friends and the size of the list
}

public String toString()
{
    return "username: " + username + " password: " + password;
}

}