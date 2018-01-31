レポート生成機能
==============
.. index::
   single: Jasperreport
   single: レイアウトファイル
   single: FWPDFReport
   single: FWPDFReportWriter

.. pdf-report:

-------------
PDFレポート生成
-------------
**Handywedge** では、PDF生成エンジンJasper Reportを使用してPDFファイルを生成する機能を提供する。

レイアウトファイル
--------------
レイアウトファイルとはレポートのフォーマットをデザインするファイルで、用紙のサイズや、枠線や画像、固定文言、データを表示する位置などをXML形式で定義する。
`Jasperreport Studio <https://community.jaspersoft.com/wiki/jaspersoft-studiotoha>`_ 等を使用して作成する。


レイアウトファイルは、warファイルの「WEB-INF/classes/report」ディレクトリに配備する。

warファイルのプロパティ配置例を  :numref:`tab-war-template` に示す。

.. code-block:: text
   :name: tab-war-template
   :caption: warファイルのレイアウトファイル配置例

   handywedge-sample.war
   ├── WEB-INF
   │ ・・・
   │ ├── classes
   │ │ ├── report
   │ │   ├── invoice.jasper
   │ │   ├── purchase.jasper
   │ ・・・
   …


.. hint:: WEB-INF/classes/reportの配下には任意のディレクトリを作成することができる。

使用方法
--------
レイアウトファイルを指定してFWPDFReportのインスタンスを生成し、マッピングするデータをMapに詰めてFWPDFReportにセットする、
FWPDFReportWriterにFWPDFReportと出力先のOutputStreamを指定しPDFを作成する。

.. seqdiag::
   :name: seq-report

   seqdiag {
      span_height = 10;
              業務プログラム; Map; List; OutputStream; FWPDFReport; FWPDFReportWriter;

              業務プログラム => FWPDFReport [label="new()", rightnote="インスタンス生成"];
              業務プログラム => Map [label="put(key, value)", rightnote="レイアウトファイルにマッピングするデータを登録"];
              業務プログラム => List [label="add()", rightnote="明細行などはListで登録"];
              業務プログラム => Map [label="put(key, List)"];
              業務プログラム => OutputStream [label="new()"];
              業務プログラム => FWPDFReport [label="setParameters(Map)", rightnote="Mapをセット"];
              業務プログラム => FWPDFReportWriter [label="addReport（FWPDFReport）", rightnote="PDFレポートをセット"];
              業務プログラム => FWPDFReportWriter => OutputStream [label="print（OutputStream）", rightnote="PDF生成"];

              業務プログラム [color=pink]
      FWPDFReportWriter [color=palegreen]
      FWPDFReport [color=palegreen]
   }


FWPDFReportWriterインターフェイスの変数を定義し、＠Injectアノテーションを付けてオブジェクトを注入する。

.. code-block:: java
   :emphasize-lines: 1, 2

    @Inject
    private FWPDFReportWriter writer;


レイアウトファイル名を指定してFWPDFReportのインスタンスを生成する。

.. code-block:: java
   :emphasize-lines: 1

        FWPDFReport report = new FWPDFReport(layoutFile);


.. important:: 「WEB-INF/classes/report」のディレクトリまではフレームワークが自動的にセットするので、それ以降のパスを指定する。

Mapにレイアウトファイルで指定したフィールド名と出力する値のペアを登録し、FWPDFReportのインスタンスにセットする。

.. code-block:: java
   :emphasize-lines: 1-3

        Map<String, Object> parameter = new HashMap<>();
        parameter.put("testHeader", paramValue);
        report.setParameters(parameter);


明細行のように繰り返し出力するものは1行分のデータを１つのオブジェクト（フィールド名と紐付くアクセサを持ったDTO）として、
Listに登録してFWPDFReportのインスタンスにセットする。

.. code-block:: java
   :emphasize-lines: 13

        List <TestEntity> list = new ArrayList<>();

        TestEntity entity1 = new TestEntity();
        entity1.setId("ID00001");
        entity1.setName("AXXXX1");
        list.add(entity1);

        TestEntity entity2 = new TestEntity();
        entity2.setId("ID00002");
        entity2.setName("AXXXX2");
        list.add(entity2);

        report.addAllDetails(list);


FWPDFReportWriterにFWPDFReportのインスタンスを登録する。

.. code-block:: java
   :emphasize-lines: 1

        writer.addReport(report);


PDFを出力させるOutputStreamを用意しprintメソッドを実行する。

.. code-block:: java
   :emphasize-lines: 6, 9

        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ec.responseReset();
        ec.setResponseContentType("application/pdf");
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + reportFile + "\"");
        OutputStream os = ec.getResponseOutputStream();

        try {
            writer.print(os);
        } catch (Exception e) {
            logger.error("帳票出力でエラーが発生しました。", e);
            // エラー処理
        } finally {
            fc.responseComplete();
            os.close();
        }


.. hint:: この例ではメモリを節約するため、HttpResponseのOutputStreamに出力している。


サンプルコード
-----------

.. code-block:: java

    package jp.cstudio.handywedge.test.app.report.pdf;

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

    import com.handywedge.config.FWMessageResources;
    import com.handywedge.log.FWLogger;
    import com.handywedge.report.pdf.FWPDFReport;
    import com.handywedge.report.pdf.FWPDFReportWriter;
    import com.handywedge.user.FWUser;

    import lombok.Getter;
    import lombok.Setter;
    import net.sf.jasperreports.engine.JRParameter;

    @ViewScoped
    @Named
    public class SeikyushoAction implements Serializable {

      private static final long serialVersionUID = 1L;

      @Inject
      private transient FWLogger logger;

      @Inject
      private FWUser user;

      @Inject
      private FWMessageResources msg;

      @Inject
      private transient FWPDFReportWriter pdfWriter;

      @Setter
      @Getter
      private String name = "handywedgeテストアプリ";

      @Setter
      @Getter
      private String memo;

      @Setter
      @Getter
      private Integer count = 20;

      public void createPdf() throws IOException {

        Map<String, Object> params = new HashMap<>();
        params.put(JRParameter.REPORT_LOCALE, user.getLocale());
        params.put(JRParameter.REPORT_RESOURCE_BUNDLE, msg.getBundle());
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
