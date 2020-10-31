package com.handywedge.converter.tosvg;

import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.exceptions.FWUnsupportedFormatException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class RemotePdf2SVGTest extends TestCase {
  private static final String ONLINE_SERVICE_ENDPOINT = "http://localhost:8080";

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public RemotePdf2SVGTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(RemotePdf2SVGTest.class);
  }

  /**
   * Rigourous Test :-)
   */
  public void testAppPdf() {
    System.out.println("======[PDF -> SVG]======");
    File file = new File("doc/DockerEngine導入ガイド(Linux).pdf");

    FWPdfConverter converter = new FWPdfConverter();
    List<File> svgFiles = null;
    try {
      svgFiles = converter.pdfToSvg(file, ONLINE_SERVICE_ENDPOINT, 30);
      svgFiles.forEach(svg -> System.out.println("Result: " + svg.getAbsolutePath()));
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
    if (ObjectUtils.isEmpty(svgFiles)) {
      System.out.println("SVG Converter is empty");
      assertTrue(false);
    } else {
      assertTrue(true);
    }
  }

  public void testAppPdf2() {
    System.out.println("======[PDF -> SVG]======");
    File file = new File("doc/DockerEngine.pdf");

    FWPdfConverter converter = new FWPdfConverter();
    List<File> svgFiles = null;
    try {
      svgFiles = converter.pdfToSvg(file, ONLINE_SERVICE_ENDPOINT, 30);
      svgFiles.forEach(svg -> System.out.println("Result: " + svg.getAbsolutePath() ));
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }

    if (ObjectUtils.isEmpty(svgFiles)) {
      System.out.println("SVG Converter is empty");
      assertTrue(false);
    } else {
      assertTrue(true);
    }

  }

  public void testAppPdf3() {
    System.out.println("======[PDF -> SVG]======");
    File file = new File("/Users/licj/Projects/murc/workspace/TestData/demo/pdf/redshift-mgmt.pdf");

    FWPdfConverter converter = new FWPdfConverter();
    List<File> svgFiles = null;
    try {
      svgFiles = converter.pdfToSvg(file, ONLINE_SERVICE_ENDPOINT, 30);
      svgFiles.forEach(svg -> System.out.println("Result: " + svg.getAbsolutePath() ));
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }

    if (ObjectUtils.isEmpty(svgFiles)) {
      System.out.println("SVG Converter is empty");
      assertTrue(false);
    } else {
      assertTrue(true);
    }
  }
}
