package jp.cstudio.csframe.test.app.report.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.csframe.log.FWLogger;
import com.csframe.report.pdf.FWPDFReport;
import com.csframe.report.pdf.FWPDFReportWriter;

import lombok.Getter;
import lombok.Setter;

@ViewScoped
@Named
public class SeikyushoAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject
  private transient FWLogger logger;

  @Inject
  private FWPDFReportWriter pdfWriter;

  @Setter
  @Getter
  private String name = "csfテストアプリ";

  @Setter
  @Getter
  private String memo;

  @Setter
  @Getter
  private Integer count = 20;

  public void createPdf() throws IOException {

    Map<String, Object> params = new HashMap<>();
    params.put("NO", UUID.randomUUID().toString().substring(0, 13));
    params.put("DATE", new Date());
    params.put("NAME", name);
    params.put("MEMO", memo);

    List<Seikyusho> seikyushoList = new ArrayList<>();
    Random rnd = new Random();
    for (int i = 0; i < count; i++) {
      seikyushoList.add(new Seikyusho("品目" + (i + 1), rnd.nextInt(1000) + 50, rnd.nextInt(20) + 1));
    }
    int subTotal = 0;
    for (Seikyusho s : seikyushoList) {
      subTotal += s.getPrice() * s.getQuantity();
    }
    int tax = (int) (subTotal * 0.08);
    params.put("SUB_TOTAL", subTotal);
    params.put("TAX", tax);
    FWPDFReport report = new FWPDFReport("report/pdf/seikyusho.jasper");
    report.addAllDetails(seikyushoList);
    report.setParameters(params);
    pdfWriter.addReport(report);

    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext ec = fc.getExternalContext();
    ec.responseReset();
    ec.setResponseContentType("application/pdf");
    ec.setResponseHeader("Content-Disposition",
        "attachment; filename=\"" + params.get("NO") + ".pdf\"");
    OutputStream os = ec.getResponseOutputStream();
    try {
      pdfWriter.print(os);
    } catch (Exception e) {
      logger.error("帳票出力テストでエラーが発生しました。", e);
      try {
        ec.responseReset();
        ((HttpServletResponse) ec.getResponse()).sendError(500);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } finally {
      fc.responseComplete();
      os.close();
    }
  }
}
