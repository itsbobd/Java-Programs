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
public class EmailVector extends Vector<Email> 
{
    EmailVector()
    {
        //empty constructor
    }

    public void addEmail(Email email)
    {
        if (isEmpty())
        {
            addElement(email); //adds the first email to the vector
        }

        else 
        {
            boolean done = false;
            int n = this.size();
            addElement(email); //add new email to back of vector

            while (n > 0 && !done)
            {

                if (email.getDomain().compareTo((elementAt(n-1).getDomain())) < 0) //place email in correct domain postition
                {
                    Email temp = this.get(n-1);
                    set(n-1, email);
                    set(n, temp);

                    n--;
                }

                else if (email.getDomain().equals(this.elementAt(n-1).getDomain())) //places email in correct user position
                {
                    if(email.getUser().compareTo((this.elementAt(n-1).getUser())) < 0)
                    {
                        Email temp = this.get(n-1);
                        set(n-1, email);
                        set(n, temp);

                        n--;
                    }
                    else
                        done = true;
                }
                else
                    done = true;
                     
            }

        }
        
    }
}
