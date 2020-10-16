package com.handywedge.converter.task;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.jodconverter.remote.RemoteConverter;
import org.jodconverter.remote.office.RemoteOfficeManager;

import com.handywedge.converter.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;

/**
 * MS Office(ワード、エクセル、パワポイント)ドキュメントをPDFファイルへ変換
 */
public class FWOfficeToPDFJob {

  private static final FWLogger logger = FWLoggerFactory.getLogger(FWOfficeToPDFJob.class);


  public File converter(File officeFile) throws IOException, OfficeException {
    final long startTime = System.currentTimeMillis();

    final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(officeFile.getName()));

    final String pdfExtension = FWConverterConst.EXTENSION_PDF;
    final File pdfFile = File.createTempFile(baseName, "." + pdfExtension);
    FileUtils.deleteQuietly(pdfFile);

    // final OfficeManager officeManager = ExternalOfficeManager.make();
    final OfficeManager officeManager = LocalOfficeManager.install();
    try {
      officeManager.start();

      final DocumentConverter converter = LocalConverter.make(officeManager);
      converter.convert(officeFile).to(pdfFile).execute();

      logger.info("Successful conversion: {} [{}b] to {} in {}ms", officeFile, officeFile.length(),
          pdfFile, System.currentTimeMillis() - startTime);

    } catch (OfficeException oException) {
      logger.error("Failed conversion: {} [{}b] to {}; {}; input file: {}", officeFile,
          officeFile.length(), pdfFile, oException, officeFile.getName());
      throw oException;
    } finally {
      OfficeUtils.stopQuietly(officeManager);
    }

    final long endTime = System.currentTimeMillis();
    logger.info(" [Office => PDF] Converter ExecutionTime: {}ms", endTime - startTime);

    return pdfFile;
  }

  public File converter(File officeFile, String endpoint) throws IOException, OfficeException {
    final long startTime = System.currentTimeMillis();

    final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(officeFile.getName()));

    final String pdfExtension = FWConverterConst.EXTENSION_PDF;
    final File pdfFile = File.createTempFile(baseName, "." + pdfExtension);
    FileUtils.deleteQuietly(pdfFile);

    // TODO: pooling
    // TODO: URIEncode
    final OfficeManager manager =
        RemoteOfficeManager.builder().urlConnection(endpoint + "/lool/convert-to/pdf").build();
    try {
      manager.start();

      final DocumentFormat targetFormat =
          DefaultDocumentFormatRegistry.getFormatByExtension(FWConverterConst.EXTENSION_PDF);
      RemoteConverter.make(manager).convert(officeFile).to(pdfFile).as(targetFormat).execute();

      logger.info("Successful conversion: {} [{}b] to {} in {}ms", officeFile, officeFile.length(),
          pdfFile, System.currentTimeMillis() - startTime);

    } catch (OfficeException oException) {
      logger.error("Failed conversion: {} [{}b] to {}; {}; input file: {}", officeFile,
          officeFile.length(), pdfFile, oException, officeFile.getName());
      throw oException;
    } finally {
      OfficeUtils.stopQuietly(manager);
    }

    final long endTime = System.currentTimeMillis();
    logger.info(" [Office => PDF] Converter ExecutionTime: {}ms", endTime - startTime);

    return pdfFile;
  }

  public void deleteFile(File pdfFile) {
    if (ObjectUtils.isEmpty(pdfFile)) {
      return;
    }

    if (pdfFile.exists()) {
      pdfFile.delete();
    }

    return;
  }
}
