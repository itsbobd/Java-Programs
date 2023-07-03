package Server_Side;
//============================================

import java.io.*;
import java.util.*;

//============================================
public class ProfileTable extends Hashtable<String, Profile>
{
    Profile profile;

    ProfileTable()
    {
        //Empty Constructor
    }

    void store(DataOutputStream dos) throws IOException
    {
        dos.writeInt(this.size()); //writes size of hastable to file

        System.out.println(this.size());

        for (Enumeration<Profile> profiles = elements(); profiles.hasMoreElements();)
        {
            profiles.nextElement().store(dos);
        }
            
    }

    ProfileTable(DataInputStream dis) throws IOException
    {
        Integer tableSize = dis.readInt(); //reads in table size from file

        System.out.println(tableSize);

        for (int i = 0; i < tableSize; i++)
        {
            profile = new Profile(dis);
            this.put(profile.username, profile);
        }
            
    }

    Boolean verify()
    {
        return true; //TEMP
    }

    Boolean used()
    {
        return false; //TEMP
    }

    public String toString()
    {
        return " ";
        
    }



}
