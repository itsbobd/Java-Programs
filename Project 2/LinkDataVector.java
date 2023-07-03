//============================================

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.util.*;

//============================================
public class LinkDataVector extends Vector<LinkData>
{
    LinkDataVector()
    {
        //empty constructor
    }
    
    public void addLink(LinkData link, Vector<Integer> tagVector)
    {
        addElement(link);

        Boolean done = false;
        int m = this.size() - 1;

        String incomingLink = this.elementAt(tagVector.elementAt(m)).getLink();
       
        if(!(incomingLink.startsWith("http")))
        {
            incomingLink = this.elementAt(tagVector.elementAt(m)).getBaseDomain() + incomingLink;
        }

        while (m > 0 && !done)
        {
             String compareLink = this.elementAt(tagVector.elementAt(m-1)).getLink();

             if(!(compareLink.startsWith("http")))
            {
                compareLink = this.elementAt(tagVector.elementAt(m - 1)).getBaseDomain() + compareLink;
            }

            if (incomingLink.compareTo(compareLink) < 0)
            {
                int temp = tagVector.elementAt(m);
                tagVector.set(m, tagVector.elementAt(m-1));
                tagVector.set(m-1, temp);
            }

            else
                done = true;
        }
    }
}
