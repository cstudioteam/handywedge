/*
 * Copyright (c) 2016-2018 C Studio Co.,Ltd.
 *
 * This software is released under the MIT License.
 *
 * http://opensource.org/licenses/mit-license.php
 */
package com.handywedge.report.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.handywedge.common.FWConstantCode;
import com.handywedge.log.FWLogger;
import com.handywedge.report.pdf.FWPDFReport;
import com.handywedge.report.pdf.FWPDFReportException;
import com.handywedge.report.pdf.FWPDFReportWriter;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@RequestScoped
class FWPDFReportWriterImpl implements FWPDFReportWriter {

  @Inject
  private FWLogger logger;

  private List<JasperPrint> pages = new ArrayList<>();
  private List<FWPDFReport> reports = new ArrayList<>();

  @Override
  public int addReport(FWPDFReport report) {

    reports.add(report);
    return reports.size();
  }

  @Override
  public void print(OutputStream result) {

    long startTime = logger.perfStart("print");
    try {
      for (FWPDFReport report : reports) {
        Map<String, Object> parameters = report.getParameters();

        // 明細行の出力内容
        List<?> details = report.getDetails();
        JRDataSource ds = null;
        if (details.isEmpty()) {
          ds = new JREmptyDataSource();
        } else {
          // Listの中身がMapでもDTOでも吸収
          ds = new JRBeanCollectionDataSource(details);
        }

        // Mapの中身がListの場合はJRBeanCollectionDataSourceに変換
        if (parameters != null) {
          parameters = convertJRDataSource(parameters);
        }

        // jasperファイルの取得
        JasperReport jasper = loadJasperFile(report.getTemplateFile());

        // ページの追加
        pages.add(JasperFillManager.fillReport(jasper, parameters, ds));

      }
    } catch (JRException | IOException ex) {
      throw new FWPDFReportException(FWConstantCode.PDF_TEMPLATE_LOAD_FAIL, ex);
    }

    if (!pages.isEmpty()) {
      try {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(result));
        exporter.setExporterInput(SimpleExporterInput.getInstance(pages));
        exporter.exportReport();
      } catch (JRException ex) {
        throw new FWPDFReportException(FWConstantCode.PDF_OUTPUT_FAIL, ex);
      }
    } else {
      throw new FWPDFReportException(FWConstantCode.PDF_DATA_MISSING);
    }
    logger.perfEnd("print", startTime);
  }

  /*
   * クラスパス上から、指定されたテンプレートのjasperファイルを取得します。
   */
  private JasperReport loadJasperFile(String templateFile) throws JRException, IOException {

    JasperReport jasperReport = null;
    // Streamの取得
    try (InputStream stream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(templateFile);) {
      if (stream == null) {
        throw new FWPDFReportException(FWConstantCode.PDF_TEMPLATE_FILE_MISSING, templateFile);
      }
      jasperReport = (JasperReport) JRLoader.loadObject(stream);
    }

    return jasperReport;
  }


  /*
   * 引数のMapにListインスタンスが含まれる場合は、 JasperReport出力用にJRBeanCollectionDataSourceに変換します。
   */
  private Map<String, Object> convertJRDataSource(Map<String, Object> map) {

    Set<Entry<String, Object>> entries = map.entrySet();

    for (Entry<String, Object> entry : entries) {
      Object o = entry.getValue();
      if (o instanceof List) {
        map.put(entry.getKey(), new JRBeanCollectionDataSource((List<?>) o));
      }
    }

    return map;
  }

}
