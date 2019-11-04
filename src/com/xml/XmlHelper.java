package com.xml;

import com.dataflow.ConnectionManager;
import com.dataflow.DataFlow;
import com.model.Column;
import com.model.Excel;
import com.model.SqlServer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;

public class XmlHelper {
    public static void importExcelToXml(String filePath, List<Column> listColumn, boolean isFirstRow, int sheetIndex) {
        Excel excel = new Excel(filePath, listColumn, isFirstRow, sheetIndex);
        File file = new File("config/test.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Excel.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(excel, file);
            marshaller.marshal(excel, System.out);
        } catch (JAXBException e) {
            System.out.println("Loi khoi tao");
            e.printStackTrace();
        }
    }

    public static Excel readExcelXml(String filePath) {
        File xmlFile = new File(filePath);
        Excel excel = null;
        JAXBContext jaxbContext;
        try
        {
            jaxbContext = JAXBContext.newInstance(Excel.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            excel = (Excel) jaxbUnmarshaller.unmarshal(xmlFile);
            System.out.println(excel);
            return excel;
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void SqlServerToXml(SqlServer sqlServer) {
        File file = new File("config/sql_server.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SqlServer.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(sqlServer, file);
            marshaller.marshal(sqlServer, System.out);
        } catch (JAXBException e) {
            System.out.println("Loi khoi tao");
            e.printStackTrace();
        }
    }

    public static SqlServer XmlToSqlServer(String filePath) {
        File sqlFile = new File(filePath);
        SqlServer sqlServer = null;
        JAXBContext jaxbContext;
        try
        {
            jaxbContext = JAXBContext.newInstance(SqlServer.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            sqlServer = (SqlServer) jaxbUnmarshaller.unmarshal(sqlFile);
            System.out.println(sqlServer);
            return sqlServer;
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void Dataflow2Xml(DataFlow dataFlow) {
        File file = new File("config/dataflow.xml");
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataFlow.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(dataFlow, file);
            marshaller.marshal(dataFlow, System.out);
        } catch (JAXBException e) {
            System.out.println("Loi khoi tao");
            e.printStackTrace();
        }
    }
}
