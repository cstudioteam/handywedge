package com.handywedge.converter.task;

import com.handywedge.converter.utils.ConverterConst;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PDFファイルをSVGファイルへ変換
 */
public class PDFToSVGJob {
	private static final Logger logger = LogManager.getLogger(PDFToSVGJob.class);

	private static final String SPACE = " ";

	public PDFToSVGJob(){}

	public List<File> converter(File pdfFile) {
		final long startTime = System.currentTimeMillis();

		final String baseName = Objects.requireNonNull(FilenameUtils.getBaseName(pdfFile.getName()));

		Path svgTempDir = null;
		try {
			svgTempDir = Files.createTempDirectory(baseName);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("create temporary folder error.");
			return null;
		}
		String svgTempDirName = svgTempDir.toAbsolutePath().toString();
		String svgTempFileName = baseName + "_%05d." + ConverterConst.EXTENSION_SVG;

		String[] commands = buildCommand(
				pdfFile.getAbsolutePath(),
				svgTempDirName + File.separator + svgTempFileName);

		logger.info("Converter Command: {}", String.join(" ", commands));
		ProcessBuilder pb = new ProcessBuilder(commands);

		try {
			Process process = pb.start();
			int ret = process.waitFor();

			//標準出力
			InputStream is = process.getInputStream();
			printInputStream(is);

			//標準エラー
			InputStream es = process.getErrorStream();
			printInputStream(es);

			int result = process.exitValue();
		} catch (InterruptedException | IOException e ) {
			e.printStackTrace();
			logger.error("pdf2svg converter error.");
			return null;
		}

		List<File> svgFiles = (List<File>) FileUtils.listFiles(
				svgTempDir.toFile()
				, FileFilterUtils.suffixFileFilter("." + ConverterConst.EXTENSION_SVG)
				, FileFilterUtils.trueFileFilter()
			);

		svgFiles.stream().sorted( Comparator.comparing(File::getAbsolutePath));
		if(ObjectUtils.isEmpty(svgFiles)){
			logger.info("Successful conversion: {} [{}b] to \n {}",
					pdfFile, pdfFile.length(), svgFiles.stream().map(f -> f.getAbsolutePath()).collect(Collectors.joining("\n")));
		}

		final long endTime = System.currentTimeMillis();
		logger.info(" [PDF => SVG] Converter ExecutionTime: {}ms", (endTime - startTime));

		return svgFiles;
	}

	private static String[] buildCommand(String inputFile, String outputFile){
		List<String> commands = new ArrayList<String>();

		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
		if (isWindows) {
			commands.add("cmd.exe");
			commands.add("/c");
		} else {
			commands.add("sh");
			commands.add("-c");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("pdf2svg").append(SPACE)
				.append(inputFile).append(SPACE)
				.append(outputFile).append(SPACE)
				.append("all").append(SPACE);

		commands.add(sb.toString());

		return commands.toArray(new String[0]);
	}

	private static void printInputStream(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} finally {
			br.close();
		}
	}

	public void deleteFiles(List<File> svgFiles){
		if(ObjectUtils.isEmpty(svgFiles)){
			return;
		}

		File path = svgFiles.stream().findFirst().get().getParentFile();

		svgFiles.stream().forEach(svg -> {
			if(svg != null && svg.exists()){
				svg.delete();
			}
		});

		if(path.exists() && path.isDirectory()){
			path.delete();
		}

		return;
	}
}
