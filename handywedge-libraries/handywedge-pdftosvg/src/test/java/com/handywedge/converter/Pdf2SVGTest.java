package com.handywedge.converter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class Pdf2SVGTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Pdf2SVGTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( Pdf2SVGTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAppPdf()
    {
        System.out.println( "======[PDF -> SVG]======" );
        File file = new File("doc/DockerEngine導入ガイド(Linux).pdf");

        DocumentConverter converter = new DocumentConverter();
        try {
            List<File> svgFiles = converter.pdfToSvg(file);
            if(ObjectUtils.isEmpty(svgFiles)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue( true );
    }

    public void testAppPdf2()
    {
        System.out.println( "======[PDF -> SVG]======" );
        File file = new File("doc/DockerEngine.pdf");

        DocumentConverter converter = new DocumentConverter();
        try {
            List<File> svgFiles = converter.pdfToSvg(file);
            if(ObjectUtils.isEmpty(svgFiles)){
                System.out.println("SVG Converter is empty");
                assertTrue( false );
            }else{
                assertTrue( true );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue( true );
    }
}
