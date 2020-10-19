package com.handywedge.converter.topdf;

import com.handywedge.converter.topdf.exceptions.FWConvertProcessException;
import com.handywedge.converter.topdf.exceptions.FWConvertTimeoutException;
import com.handywedge.converter.topdf.exceptions.FWUnsupportedFormatException;
import com.handywedge.converter.topdf.task.FWOfficeToPDFJob;
import com.handywedge.converter.topdf.utils.FWConverterConst;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * ドキュメント変換クラス
 * 変換対象ファイル： MS Office(ワード、エクセル、パワポイント)
 * 変換ファイル： PDF
 */
public class FWOfficeFileConverter {

	private static final FWLogger logger = FWLoggerFactory.getLogger(FWOfficeFileConverter.class);

	/**
	 * [ローカル]SVGファイルへ変換
	 *
	 * @param sourceFile 変換元MS Officeドキュメントファイル
	 * @return 変換したPDFファイル
	 */
	public File fileToPdf(File sourceFile)
			throws FWUnsupportedFormatException, FWConvertProcessException, FWConvertTimeoutException {
		File pdfFile = null;

		logger.info("=== Converter Start. ===");
		final long startTime = System.currentTimeMillis();

		if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
			String message = String.format("can not read source file: %s", sourceFile);
			logger.error(message);
			throw new FWConvertProcessException(message);
		}

		// 拡張子判別
		final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

		if (!isOfficeDocument(inputExtension)) {
			String message = String.format("Unsupported extension: [%s]", inputExtension);
			logger.error(message);
			throw new FWUnsupportedFormatException(message);
		}

		// MS OfficeドキュメントのPDF変換
		FWOfficeToPDFJob toPDFJob = new FWOfficeToPDFJob();
		pdfFile = toPDFJob.converter(sourceFile);

		final long endTime = System.currentTimeMillis();
		logger.info(" DocumentConverter ExecutionTime: {}ms", endTime - startTime);

		logger.info("=== Converter end. ===");
		return pdfFile;
	}

	/**
	 * [リモート]SVGファイルへ変換
	 *
	 * @param sourceFile 変換元ファイル
	 * @param endpoint   LibreOfficeサーバーIPアドレス
	 * @return 変換したsvgファイル
	 */
	public File fileToPdf(File sourceFile, String endpoint)
			throws FWUnsupportedFormatException, FWConvertProcessException, FWConvertTimeoutException {
		File pdfFile = null;

		logger.info("=== Converter Start. ===");
		final long startTime = System.currentTimeMillis();

		if ((sourceFile == null) || !sourceFile.exists() || !sourceFile.canRead()) {
			String message = String.format("can not read source file: %s", sourceFile);
			logger.error(message);
			throw new FWConvertProcessException(message);
		}

		// 拡張子判別
		final String inputExtension = FilenameUtils.getExtension(sourceFile.getName());

		if (!isOfficeDocument(inputExtension)) {
			String message = String.format("Unsupported extension: [%s]", inputExtension);
			logger.error(message);
			throw new FWUnsupportedFormatException(message);
		}

		// MS OfficeドキュメントのPDF変換
		FWOfficeToPDFJob toPDFJob = new FWOfficeToPDFJob();
		pdfFile = toPDFJob.converter(sourceFile, endpoint);

		final long endTime = System.currentTimeMillis();
		logger.info(" DocumentConverter ExecutionTime: {}ms", endTime - startTime);

		logger.info("=== Converter end. ===");
		return pdfFile;
	}

	private boolean isOfficeDocument(String extension) {
		if (StringUtils.isEmpty(extension)) {
			return false;
		}

		return FWConverterConst.OFFICE_DOCUMENT_EXTENSIONS
				.contains(StringUtils.lowerCase(extension));
	}

}
