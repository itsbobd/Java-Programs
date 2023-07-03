package Client_Side;
//============================================

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

//============================================
public class ChatViewer extends JEditorPane
{

    //FIGURE OUT WRAPPING FOR INSIDE OF SCROLL PANE FOR CHAT VIEWER

    //============================================ Constructor
    ChatViewer (String username)
    {
        setEditable(false);
        setContentType("text/html");
        setEditorKit(new HTMLEditorKit());
        
        setText("<div align = \"center\">" + 
                        "<font color = \"green\">" + 
                                username + 
                                        "</font></div>");
    }

    public void addTextMessage(String msg, String color, String alignment)
    {
        addText("<div align = \"" + alignment + "\">" + 
                        "<font color = \"" + color + "\">" + 
                                    msg + 
                                                "</font></div>");
    }

    public void addText(String txt)
    {
        HTMLDocument doc;
        Element html;
        Element body;

        doc = (HTMLDocument) getDocument();
        html = doc.getRootElements() [0];
        body = html.getElement(1);

        try
        {
            doc.insertBeforeEnd (body, txt);
            setCaretPosition(getDocument().getLength());
        }
        
        catch(Exception ex)
        {
            System.out.println("Error inserting text");
        }
        
    }
}
