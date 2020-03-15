
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import java.io.*;


public final class TomitaParser
{
    public Document run(String[] inputText) throws IOException
    {
        final String command = "./tomita-parser config.proto";
        final ProcessBuilder b = new ProcessBuilder("/bin/bash", "-c", command);
        final Process p = b.start();
        final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        Document document = null;
        for(String s: inputText)
        {
            w.write(s);
            w.newLine();
        }
        w.flush();
        p.getOutputStream().flush();
        w.close();
        p.getOutputStream().close();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из инпут стрима
            document = documentBuilder.parse(p.getInputStream());
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        p.getInputStream().close();
        try {
            p.waitFor();
            if (p.exitValue() != 0)
                throw new IOException("tomita-parser has returned " + String.valueOf(p.exitValue()));
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return document;
    }
}
