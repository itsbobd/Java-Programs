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
public class LinkData
{
    String link;
    int seedDistance;
    EmailVector vector;
    String baseDomain;
    int tag;

    LinkData(String link, int seedDistance, String baseDomain)
    {
        this.link = link;
        this.seedDistance = seedDistance;
        this.baseDomain = baseDomain;
        vector = new EmailVector();
        tag = Frame.tagCount;
        Frame.tagCount++; //increments tag count

    }

    public Vector<Email> getVector() //returns email vector
    {
        return vector;
    }

    public String getLink()
    {
        return link;
    }

    public String getBaseDomain()
    {
        return baseDomain;
    }

    public String toString()
    {
            if (!link.startsWith("http"))
            {
                if(!link.startsWith("/"))
                {
                    return baseDomain+"/"+link;
                }

                else
                    return baseDomain+link;
            }
        
            else
                return link;     
    }
}
