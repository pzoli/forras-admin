package hu.infokristaly.utils;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.TransformerException;

public class ImportXML {
	private String fromXLSSource; //"users.xls"
	private String toXSLSource; //"user2Transform.xsl"

    public String transformToCsv(String fileRootPath) {
        File xmlFile = new File(fileRootPath + File.separatorChar + fromXLSSource);
        File xsltFile = new File(fileRootPath + File.separatorChar + toXSLSource);

        javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(xmlFile);
        javax.xml.transform.Source xsltSource = new javax.xml.transform.stream.StreamSource(xsltFile);
        StringWriter xsltResult = new StringWriter();
        javax.xml.transform.Result result = new javax.xml.transform.stream.StreamResult(xsltResult);

        javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer trans;
        try {
            trans = transFact.newTransformer(xsltSource);
            trans.transform(xmlSource, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return xsltResult.toString();
    }

}
