package com.handywedge.converter;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class Pdf2SVGTest extends TestCase {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public Pdf2SVGTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(Pdf2SVGTest.class);
  }

  /**
   * Rigourous Test :-)
   */
  public void testAppPdf() {
    System.out.println("======[PDF -> SVG]======");
    File file = new File("doc/DockerEngine導入ガイド(Linux).pdf");

    FWDocumentConverter converter = new FWDocumentConverter();
    List<File> svgFiles = converter.pdfToSvg(file);
    if (ObjectUtils.isEmpty(svgFiles)) {
      System.out.println("SVG Converter is empty");
      assertTrue(false);
    } else {
      assertTrue(true);
    }

    assertTrue(true);
  }

  public void testAppPdf2() {
    System.out.println("======[PDF -> SVG]======");
    File file = new File("doc/DockerEngine.pdf");

    FWDocumentConverter converter = new FWDocumentConverter();
    List<File> svgFiles = converter.pdfToSvg(file);
    if (ObjectUtils.isEmpty(svgFiles)) {
      System.out.println("SVG Converter is empty");
      assertTrue(false);
    } else {
      assertTrue(true);
    }

    assertTrue(true);
  }
}
