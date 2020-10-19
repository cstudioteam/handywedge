package com.handywedge.converter.topdf;

import java.io.File;

import org.apache.commons.lang3.ObjectUtils;

import com.handywedge.converter.topdf.exceptions.FWConvertProcessException;
import com.handywedge.converter.topdf.exceptions.FWUnsupportedFormatException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class RemoteTest extends TestCase {

  private static final String ONLINE_SERVICE_ENDPOINT = "http://localhost:9980";

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public RemoteTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(RemoteTest.class);
  }

  /**
   * Rigourous Test :-)
   */
  public void testAppDocx() {
    System.out.println("======[DOCX -> SVG]======");
    String docName = "doc/Talendジョブ保守ガイド.docx";
    File file = new File(docName);

    FWOfficeFileConverter converter = new FWOfficeFileConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        System.out.println("Result: " + pdfFile.getAbsolutePath());
        assertTrue(true);
      }
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  public void testAppDoc() {
    System.out.println("======[DOC -> SVG]======");
    // File file = new File("doc/情報システム運用規程.doc");
    File file = new File("doc/情報システム運用規程2.doc");

    FWOfficeFileConverter converter = new FWOfficeFileConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        System.out.println("Result: " + pdfFile.getAbsolutePath());
        assertTrue(true);
      }
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  public void testAppXlsx() {
    System.out.println("======[XLSX -> SVG]======");
    File file = new File("doc/年次会計報告書.xlsx");

    FWOfficeFileConverter converter = new FWOfficeFileConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        System.out.println("Result: " + pdfFile.getAbsolutePath());
        assertTrue(true);
      }
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  public void testAppXls() {
    System.out.println("======[XLS -> SVG]======");
    File file = new File("doc/申込書.xls");

    FWOfficeFileConverter converter = new FWOfficeFileConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        System.out.println("Result: " + pdfFile.getAbsolutePath());
        assertTrue(true);
      }
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  public void testAppPptx() {
    System.out.println("======[PPTX -> SVG]======");
    File file = new File("doc/ppt-sample.pptx");

    FWOfficeFileConverter converter = new FWOfficeFileConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        System.out.println("Result: " + pdfFile.getAbsolutePath());
        assertTrue(true);
      }
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

  public void testAppPpt() {
    System.out.println("======[PPT -> SVG]======");
    File file = new File("doc/プレゼンテーション.ppt");

    FWOfficeFileConverter converter = new FWOfficeFileConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        System.out.println("Result: " + pdfFile.getAbsolutePath());
        assertTrue(true);
      }
    } catch (FWUnsupportedFormatException e) {
      e.printStackTrace();
      assertTrue(false);
    } catch (FWConvertProcessException e) {
      e.printStackTrace();
      assertTrue(false);
    }
  }

}
