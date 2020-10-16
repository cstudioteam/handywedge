package com.handywedge.converter;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.ObjectUtils;
import org.jodconverter.core.office.OfficeException;

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

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppDoc() {
    System.out.println("======[DOC -> SVG]======");
    // File file = new File("doc/情報システム運用規程.doc");
    File file = new File("doc/情報システム運用規程2.doc");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppDot() {
    System.out.println("======[DOT -> SVG]======");
    File file = new File("doc/このテキストのような.dot");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppXlsx() {
    System.out.println("======[XLSX -> SVG]======");
    File file = new File("doc/年次会計報告書.xlsx");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppXls() {
    System.out.println("======[XLS -> SVG]======");
    File file = new File("doc/申込書.xls");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppPptx() {
    System.out.println("======[PPTX -> SVG]======");
    File file = new File("doc/ppt-sample.pptx");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppPpt() {
    System.out.println("======[PPT -> SVG]======");
    File file = new File("doc/プレゼンテーション.ppt");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }
  }

  public void testAppPdf() {
    System.out.println("======[PDF -> SVG]======");
    File file = new File("doc/DockerEngine導入ガイド(Linux).pdf");

    FWDocumentConverter converter = new FWDocumentConverter();
    try {
      File pdfFile = converter.fileToPdf(file, ONLINE_SERVICE_ENDPOINT);
      if (ObjectUtils.isEmpty(pdfFile)) {
        System.out.println("SVG Converter is empty");
        assertTrue(false);
      } else {
        assertTrue(true);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (OfficeException e) {
      e.printStackTrace();
    }

    assertTrue(true);
  }
}
