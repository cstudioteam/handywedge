package com.handywedge.converter.topdf.task;

import com.handywedge.converter.topdf.exceptions.FWConvertProcessException;
import com.handywedge.converter.topdf.exceptions.FWConvertTimeoutException;
import com.handywedge.converter.topdf.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

/**
 * MS Office(ワード、エクセル、パワポイント)ドキュメントをPDFファイルへ変換
 */
public class FWOfficeToPDFJob {

	private static final FWLogger logger = FWLoggerFactory.getLogger(FWOfficeToPDFJob.class);

	private static final int POOL_SIZE = 5;
	private static final long QUEUE_TIME_OUT = 10_000L;
	private static final long EXECUTION_TIME_OUT = 10_000L;

	/**
	 * [ローカル変換] LibreOfficeのローカルサービスを利用し、MS OfficeファイルのPDF化変換を行う
	 * @param officeFile MS Officeファイル
	 * @return 変換したPDFファイル
	 * @throws FWConvertProcessException
	 * @throws FWConvertTimeoutException
	*/
  public File converter(File officeFile)
			throws FWConvertTimeoutException, FWConvertProcessException {
		final long startTime = System.currentTimeMillis();

    final String baseName =
    	Objects.requireNonNull(FilenameUtils.getBaseName(officeFile.getName()));

    final String pdfExtension = FWConverterConst.EXTENSION_PDF;
    File pdfFile = null;
		try {
			pdfFile = File.createTempFile(baseName, "." + pdfExtension);
		} catch (IOException e) {
			e.printStackTrace();
			String message = String.format("create temporary file error. %s", e.getMessage());
			logger.error(message);
			throw new FWConvertProcessException(message);
		}
		FileUtils.deleteQuietly(pdfFile);

    // final OfficeManager officeManager = ExternalOfficeManager.make();
    final OfficeManager officeManager = LocalOfficeManager.install();
    try {
      officeManager.start();

      final DocumentConverter converter = LocalConverter.make(officeManager);
      converter.convert(officeFile).to(pdfFile).execute();

      logger.info("Successful conversion: {} [{}b] to {} in {}ms", officeFile,
        officeFile.length(), pdfFile, System.currentTimeMillis() - startTime);

    } catch (OfficeException e) {
			e.printStackTrace();
			String message = String.format("Failed conversion: {} [{}b] to {}; {}; input file: {}", officeFile,
					officeFile.length(), pdfFile, e, officeFile.getName());
			logger.error(message);
			throw new FWConvertTimeoutException(message);
    } finally {
      OfficeUtils.stopQuietly(officeManager);
    }

    final long endTime = System.currentTimeMillis();
    logger.info(" [Office => PDF] Converter ExecutionTime: {}ms", endTime - startTime);

    return pdfFile;
  }

	/**
	 * [リモート変換] LibreOfficeのオンラインサービスを利用し、MS OfficeファイルのPDF化変換を行う
	 * @param officeFile MS Officeファイル
	 * @return 変換したPDFファイル
	 * @throws FWConvertProcessException
	 * @throws FWConvertTimeoutException
	 */
	public File converter(File officeFile, String endpoint)
			throws FWConvertProcessException, FWConvertTimeoutException {
		final long startTime = System.currentTimeMillis();

		// office file
		final String baseName =
			Objects.requireNonNull(FilenameUtils.getBaseName(officeFile.getName()));
		final String baseExtension = FilenameUtils.getExtension(officeFile.getName());

		Path pdfTempDir = null;
		try {
			pdfTempDir = Files.createTempDirectory(null);
		} catch (IOException e) {
			e.printStackTrace();
			String message = String.format("create temporary folder error. %s", e.getMessage());
			logger.error(message);
			throw new FWConvertProcessException(message);
		}

		// office file rename
		final String newBaseName = UUID.randomUUID().toString().replace("-","");
		File newOfficeFile = new File(pdfTempDir.toString() + File.separator + newBaseName + "." + baseExtension);
		try {
			FileUtils.copyFile(officeFile, newOfficeFile);
		}catch(IOException e){
			e.printStackTrace();
			logger.error("file copy error.");
			return null;
		}

		// pdf file
		final String pdfExtension = FWConverterConst.EXTENSION_PDF;
		File pdfFile = null;
		try {
			pdfFile = File.createTempFile(baseName, "." + pdfExtension);
		} catch (IOException e) {
			e.printStackTrace();
			String message = String.format("create temporary file error. %s", e.getMessage());
			logger.error(message);
			throw new FWConvertProcessException(message);
		}finally {
			FileUtils.deleteQuietly(pdfFile);
		}

		final OfficeManager manager =
			RemoteOfficeManager.builder().urlConnection(endpoint + "/lool/convert-to/pdf")
				.poolSize(POOL_SIZE).taskQueueTimeout(QUEUE_TIME_OUT)
				.taskExecutionTimeout(EXECUTION_TIME_OUT).build();
		try {
			manager.start();

			final DocumentFormat targetFormat =
				DefaultDocumentFormatRegistry.getFormatByExtension(FWConverterConst.EXTENSION_PDF);
			RemoteConverter.make(manager).convert(newOfficeFile).to(pdfFile).as(targetFormat)
				.execute();

			logger.info("Successful conversion: {} [{}b] to {} in {}ms", officeFile,
				officeFile.length(), pdfFile, System.currentTimeMillis() - startTime);

		} catch (OfficeException e) {
			e.printStackTrace();
			String message = String.format("Failed conversion: {} [{}b] to {}; {}; input file: {}", officeFile,
						officeFile.length(), pdfFile, e, officeFile.getName());
			logger.error(message);
			throw new FWConvertTimeoutException(message);
		} finally {
			OfficeUtils.stopQuietly(manager);
			FileUtils.deleteQuietly(newOfficeFile);
		}

		final long endTime = System.currentTimeMillis();
		logger.info(" [Office => PDF] Converter ExecutionTime: {}ms", endTime - startTime);

		return pdfFile;
	}

}
