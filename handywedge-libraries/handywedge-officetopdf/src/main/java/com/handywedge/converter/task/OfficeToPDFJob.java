package com.handywedge.converter.task;

import com.handywedge.converter.utils.ConverterConst;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormat;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.ExternalOfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.jodconverter.remote.RemoteConverter;
import org.jodconverter.remote.office.RemoteOfficeManager;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * MS Office(ワード、エクセル、パワポイント)ドキュメントをPDFファイルへ変換
 */
public class OfficeToPDFJob {

	private static final Logger logger = LogManager.getLogger(OfficeToPDFJob.class);


	public File converter(File officeFile) throws IOException, OfficeException {
		final long startTime = System.currentTimeMillis();

		final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(officeFile.getName()));

		final String pdfExtension = ConverterConst.EXTENSION_PDF;
		final File pdfFile = File.createTempFile(baseName, "." + pdfExtension);
		FileUtils.deleteQuietly(pdfFile);

		//final OfficeManager officeManager = ExternalOfficeManager.make();
		final OfficeManager officeManager = LocalOfficeManager.install();
		try {
			officeManager.start();

			final DocumentConverter converter = LocalConverter.make(officeManager);
			converter.convert(officeFile)
					.to(pdfFile)
					.execute();

			logger.info("Successful conversion: {} [{}b] to {} in {}ms",
					officeFile, officeFile.length(),
					pdfFile,
					System.currentTimeMillis() - startTime);

		} catch (OfficeException oException) {
			logger.error( "Failed conversion: {} [{}b] to {}; {}; input file: {}",
					officeFile, officeFile.length(),
					pdfFile,
					oException,
					officeFile.getName());
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

		final String pdfExtension = ConverterConst.EXTENSION_PDF;
		final File pdfFile = File.createTempFile(baseName, "." + pdfExtension);
		FileUtils.deleteQuietly(pdfFile);

		// TODO: pooling
		// TODO: URIEncode
		final OfficeManager manager = RemoteOfficeManager.builder()
						.urlConnection(endpoint + "/lool/convert-to/pdf")
						.build();
		try {
			manager.start();

			final DocumentFormat targetFormat =
					DefaultDocumentFormatRegistry.getFormatByExtension(ConverterConst.EXTENSION_PDF);
			RemoteConverter.make(manager)
					.convert(officeFile).to(pdfFile).as(targetFormat).execute();

			logger.info("Successful conversion: {} [{}b] to {} in {}ms",
					officeFile, officeFile.length(),
					pdfFile,
					System.currentTimeMillis() - startTime);

		} catch (OfficeException oException) {
			logger.error( "Failed conversion: {} [{}b] to {}; {}; input file: {}",
					officeFile, officeFile.length(),
					pdfFile,
					oException,
					officeFile.getName());
			throw oException;
		} finally {
			OfficeUtils.stopQuietly(manager);
		}

		final long endTime = System.currentTimeMillis();
		logger.info(" [Office => PDF] Converter ExecutionTime: {}ms", endTime - startTime);

		return pdfFile;
	}

	public void deleteFile(File pdfFile){
		if(ObjectUtils.isEmpty(pdfFile)){
			return;
		}

		if(pdfFile.exists() ){
			pdfFile.delete();
		}

		return;
	}
}
