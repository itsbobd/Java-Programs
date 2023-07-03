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
class MyTagHandler extends HTMLEditorKit.ParserCallback
{
    HTML.Tag tag;
    MutableAttributeSet aSet;
    Object attribute;
    String string;
    Boolean multipleBool;
    long startTime;
    Vector<Integer> tagVector;

    EmailVector emailVector;
    LinkDataVector linkVector;
    LinkData link;
    Pattern pattern;
    Matcher matcher;
    String patternStr = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" 
    + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    //64 length limit, upper and lower, 0-9, and hypen and dot, and same for the domain (no dots/hypens at beg and end).

    public MyTagHandler(LinkDataVector l, int c, long s, Vector<Integer> t) //constructor to retrieve list
    {
        linkVector = l;
        link = linkVector.elementAt(c);
        startTime = s;
        tagVector = t;
    }

    public void handleStartTag(HTML.Tag tag, MutableAttributeSet aSet, int pos)
    {
        this.tag = tag;
        this.aSet = aSet;

        if (tag == HTML.Tag.A)
        {
            attribute = aSet.getAttribute(HTML.Attribute.HREF);

            if (attribute != null)
            {
                string = attribute.toString();

                if (string.startsWith("mailto:")) //takes care of MailTo links
                {
                    string = string.replaceFirst("mailto:", "");
                    String[] temp = string.split("\\?cc"); //just pulling first email off long mailto: to make it easier
                    string = temp[0];

                    ((EmailVector) link.getVector()).addEmail(new Email(string));
                    System.out.println("email added");

                    if ((getCurrentTime() - startTime) > Frame.runtimeLimit) //terminates program in runtime is exceeded
                        Frame.runtimeLimitBool = true;
                }

                else
                {
                    //checking for multiples in the link vector
                    multipleBool = true;
                    int i;
                    for (i = 0; i < linkVector.size(); i++)
                    {
                       if (linkVector.elementAt(i).getLink().equals(string))
                       multipleBool = false;
                    }  

                    if (multipleBool)
                    {
                        if ((getCurrentTime() - startTime) > Frame.runtimeLimit) //terminates program in runtime is exceeded
                            Frame.runtimeLimitBool = true;

                        else
                        {
                            if (!((getCurrentTime() - startTime) > Frame.expansionLimit)) //doesn't add a link if the expansion limit is exceeded
                            {
                                if (!(link.seedDistance >= Frame.seedLimit)) //doesn't add a link if its distance is too far from the seed
                                {
                                    tagVector.add(tagVector.size(), tagVector.size()); 
                                    linkVector.addLink(new LinkData(string, link.seedDistance + 1, link.toString()), tagVector);
                                    System.out.println("link added");
                                }
                                     
                            }
                        }     
                    }                                        
                }                       
            }      
        }
                
    } //end of handStartTag

    public void handleText(char[] data, int pos)
    {
        pattern = Pattern.compile(patternStr);
        matcher = pattern.matcher(String.valueOf(data));

        while (matcher.find())
        {
            ((EmailVector) link.getVector()).addEmail(new Email(matcher.group()));
            System.out.println("email added");

            if ((getCurrentTime() - startTime) > Frame.runtimeLimit) //terminates program in runtime is exceeded
                Frame.runtimeLimitBool = true;
        }
        
    } //end of handleText 

    long getCurrentTime()
    {   
        return System.currentTimeMillis();
    }

} //end of class MyTagHandler
