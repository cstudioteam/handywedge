package com.handywedge.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.ObjectUtils;
import org.jodconverter.core.office.OfficeException;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class LocalTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LocalTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LocalTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAppDocx()
    {
        System.out.println( "======[DOCX -> SVG]======" );
        String docName = "doc/Talendジョブ保守ガイド.docx";
        File file = new File(docName);

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppDoc()
    {
        System.out.println( "======[DOC -> SVG]======" );
        //File file = new File("doc/情報システム運用規程.doc");
        File file = new File("doc/情報システム運用規程2.doc");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppDot()
    {
        System.out.println( "======[DOT -> SVG]======" );
        File file = new File("doc/このテキストのような.dot");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppXlsx()
    {
        System.out.println( "======[XLSX -> SVG]======" );
        File file = new File("doc/年次会計報告書.xlsx");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppXls()
    {
        System.out.println( "======[XLS -> SVG]======" );
        File file = new File("doc/申込書.xls");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppPptx()
    {
        System.out.println( "======[PPTX -> SVG]======" );
        File file = new File("doc/ppt-sample.pptx");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppPpt()
    {
        System.out.println( "======[PPT -> SVG]======" );
        File file = new File("doc/プレゼンテーション.ppt");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }

    public void testAppPdf()
    {
        System.out.println( "======[PDF -> SVG]======" );
        File file = new File("doc/DockerEngine導入ガイド(Linux).pdf");

        DocumentConverter converter = new DocumentConverter();
        try {
            File pdfFile = converter.fileToPdf(file);
            if(ObjectUtils.isEmpty(pdfFile)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OfficeException e) {
            e.printStackTrace();
        }

        assertTrue( true );
    }
}
