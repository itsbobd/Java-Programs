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
class Frame extends JFrame
            implements ActionListener
{
    LinkDataVector linkVector;
    EmailVector emailVector;
    LinkData link;
    int listCount;
    static final int seedLimit = 2;
    long startTime;
    static final long expansionLimit = 120000; //2 minutes
    static final long runtimeLimit = 300000; //5 minutes
    static Boolean runtimeLimitBool;
    Boolean goodFirstLink;
    Boolean notOutOfLinks;
    static Vector<Integer> tagVector;
    static int tagCount;
    
    JList<String> URLListBox;
    DisplayList displayList;
    MyTagHandler myTagHandler;
    JButton goButton;
    JButton exitButton;
    JTextField textField;
    URL url; 
    BufferedReader pageReader;
    String linkString;
    JOptionPane optionPane;

    //=========constructor====================
    Frame()
    {
      JScrollPane listBoxScrollPane;
      JPanel buttonPanel;
      JPanel textFieldPanel;
      optionPane = new JOptionPane();

      //creating buttons and adding them to panel
      goButton = new JButton("Go");
      exitButton = new JButton("Exit");

      goButton.addActionListener(this);
      exitButton.addActionListener(this);

      goButton.setActionCommand("Go");
      exitButton.setActionCommand("Exit");

      //create list and list box
      displayList = new DisplayList();
      URLListBox = new JList<String>(displayList);

      //initializing scroll pane and button panel
      listBoxScrollPane = new JScrollPane(URLListBox);
      buttonPanel = new JPanel();
      textFieldPanel = new JPanel();

      //adding buttons to the button panel
      buttonPanel.add(goButton);
      buttonPanel.add(exitButton);
      buttonPanel.setLayout(new GridLayout(2, 1));

      //initializing textfield
      textField = new JTextField(16);
      textFieldPanel.add(textField);

      //placing button panel and scroll pane inside of frame
      add(buttonPanel, BorderLayout.EAST);
      add(listBoxScrollPane, BorderLayout.CENTER);
      add(textFieldPanel, BorderLayout.NORTH);

      setupMainFrame();

    } //end of Frame constructor

    void setupMainFrame()
    {
        Toolkit tk;
        Dimension d;

        setTitle("Trip Records List");

        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        setSize(d.width/2, d.height/2);
        setLocation(d.width/3, d.height/3);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

    } //end setupMainFrame

    public void actionPerformed (ActionEvent e)
    {
        if (e.getActionCommand().equals("Go"))
        {
            startCrawl();

            if (goodFirstLink)
                printList();
            
        }

        if (e.getActionCommand().equals("Exit"))
        {
            System.exit(0);
        }

    } //end of actionPerformed

    void startCrawl() 
    {
        initialize();

        //while loop for EXPANSION TIME, TOTAL TIME, SEED DISTANCE
        while (runtimeLimitBool && goodFirstLink && notOutOfLinks) 
        {
            if (linkVector.size() != listCount) //checks to see if there is even another link to check in the list
            {

                attemptParse(linkVector.elementAt(listCount).getLink(), linkVector.elementAt(listCount));

                if (pageReader != null)
                {
                    try
                    {
                        myTagHandler = new MyTagHandler(linkVector, listCount, startTime, tagVector);
                        new ParserDelegator().parse(pageReader, myTagHandler, true);
                    }

                    catch (IOException ioex)
                    {
                        System.out.println("IOException");
                    }

                    //increment to next link in vector
                    listCount++;
                }
            }

            else
                notOutOfLinks = false;
        }
    } //end of startCrawl

    void initialize()
    {
        tagCount = 0;
        notOutOfLinks = true;
        goodFirstLink = true;
        runtimeLimitBool = true;
        startTime = System.currentTimeMillis(); //collects start time for program
        linkVector = new LinkDataVector(); //creates new link vector
        tagVector = new Vector<Integer>();
        linkString = textField.getText(); //retrieve link from textfield

        //clear previous links/emails and textfield url
        displayList.clear();
        textField.setText("");

        try
            {
                url = new URL(linkString);
                url.toURI();
                pageReader = new BufferedReader(new InputStreamReader(url.openStream()));
            }

            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "The entered url is invalid");
                goodFirstLink = false;
            }

        tagVector.add(0);
        linkVector.addLink(new LinkData(linkString, 0, null), tagVector); //adding first link to vector

        //adding first tag as zero

        listCount = 0; //matching counter with start of link vector

    } //end of initialize

    void attemptParse(String string, LinkData l)
    {
        if (!testLink(string))
        {
            if(!testLink("http://" + string))
            {
                if(!testLink("https://" + string))
                {
                    if(!testLink(l.getLink() + string))
                    {
                        if(!testLink(l.getLink() + "/" + string))
                        {}

                        else
                        {} //give up
                    }
                }
            }  
        }    
    }

    Boolean testLink(String string)
    {
        try
            {
                url = new URL(string);
                url.toURI();
                pageReader = new BufferedReader(new InputStreamReader(url.openStream()));
            }

            catch (Exception ex)
            {
                return false;
            }

            return true;
    }

    void printList()
    {

        //this hopefully prints all the things out in the list

        for (int n = 0; n < linkVector.size(); n++)
        {
            displayList.addElement(linkVector.elementAt(tagVector.elementAt(n)).toString());

                for (int y = 0; y < linkVector.elementAt(tagVector.elementAt(n)).vector.size(); y++)
                {
                    displayList.addElement(linkVector.elementAt(tagVector.elementAt(n)).vector.elementAt(y).toString());
                }

                displayList.addElement("**************************************************"); //separator
            
        }
    }
} //end of Frame class