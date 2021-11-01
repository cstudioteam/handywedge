package com.handywedge.report.service.api.v1;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.report.service.api.v1.model.PdfRequest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.StreamingOutput;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

@Path("/pdf")
@RequestScoped
@Produces({"application/pdf", MediaType.APPLICATION_JSON})
public class ReportService {

  private Logger logger = LoggerFactory.getLogger(ReportService.class);

  private Response output(PdfRequest req) {
    logger.debug("リクエストパラメータ[{}]", req);
    InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(req.getJasperFileName());
    if (in == null) {
      Map<String, Object> body = new HashMap<>();
      body.put("message", req.getJasperFileName() + "が見つかりません。");
      return Response.status(Status.NOT_FOUND).entity(body).build();
    } else {
      JRDataSource jds = new JRMapArrayDataSource(req.getFields().toArray());
      byte[] pdf;
      try {
        pdf = JasperRunManager.runReportToPdf(in, req.getParameters(), jds);
        StreamingOutput so = out -> {
          BufferedOutputStream bos = new BufferedOutputStream(out);
          bos.write(pdf);
        };
        return Response.ok(so).header("Content-Disposition", "attachment").build();
      } catch (JRException | JRRuntimeException e) {
        logger.error("PDF生成でエラーが発生しました。", e);
        Map<String, Object> body = new HashMap<>();
        body.put("message", e.toString());
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(body).build();
      }
    }
  }

  @POST
  public Response doPost(PdfRequest req) throws JRException {
    return output(req);
  }
}
