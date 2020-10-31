package com.handywedge.converter.tosvg;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.exceptions.FWUnsupportedFormatException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class LocalePdf2SVGTest extends TestCase {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public LocalePdf2SVGTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(LocalePdf2SVGTest.class);
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
      svgFiles = converter.pdfToSvg(file);
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
      svgFiles = converter.pdfToSvg(file);
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
