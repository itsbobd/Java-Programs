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
public class Email 
{
    String email;
    String domain;
    String user;

    Email(String input) //constructs email
    {
        email = input;

        String [] tempArr = email.split("@"); //splits email

        user = tempArr[0];
        domain = tempArr[1];
    }

    public String getUser()
    {
        return user;
    }

    public String getDomain()
    {
        return domain;
    }

    public String toString()
    {
        return email;
    }
}
