package com.handywedge.converter.tosvg;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.exceptions.FWConvertTimeoutException;
import com.handywedge.converter.tosvg.exceptions.FWUnsupportedFormatException;
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
    List<File> svgFiles = null;
    try {
      svgFiles = converter.pdfToSvg(file);
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertTimeoutException e) {
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

    FWDocumentConverter converter = new FWDocumentConverter();
    List<File> svgFiles = null;
    try {
      svgFiles = converter.pdfToSvg(file);
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertTimeoutException e) {
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
