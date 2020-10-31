package com.handywedge.converter.tosvg.task;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.handywedge.log.FWLogger;
import com.handywedge.log.FWLoggerFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

/**
 * pdftosvgサービス接続し、変換処理を行う管理クラス
 */
public class FWRemoteJobManager {
	private static final FWLogger logger = FWLoggerFactory.getLogger(FWRemoteJobManager.class);

	private static final String SVG_CONVERTER_URI = "/pdftosvg/webapi/file/converter/svg";
	private static final String SVG_DOWNLOAD_URI = "";

	private static final int DEFALUT_REQUEST_TIMEOUT = 3; 		// seconds
	private static final int DEFALUT_CONNECT_TIMEOUT = 3; 		// seconds
	private static final int CONVERTER_SOCKET_TIMEOUT = 30; 	// seconds
	private static final int DOWNLOAD_SOCKET_TIMEOUT = 3; 		// seconds

	private String endpoint;
	private Integer timeout;

	public FWRemoteJobManager(String endpoint, Integer timeout){
		this.endpoint = endpoint;
		this.timeout = timeout;
	}

	/**
	 * pdf2svg変換サービス呼び出し
	 * @param pdfFile PDFファイル
	 * @return SVGファイル一覧
	 * @throws IOException
	 */
	public List<String> convert(File pdfFile) throws IOException {

		final long startTime = logger.perfStart("ConverterService");

		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		connManager.setDefaultMaxPerRoute(2);
		connManager.setMaxTotal(10);

		CloseableHttpClient httpClient = HttpClients.createDefault();
		int socketTimeout = (this.timeout != null) ? this.timeout :CONVERTER_SOCKET_TIMEOUT;
		RequestConfig timeoutConfig = RequestConfig.custom()
			.setSocketTimeout(socketTimeout * 1000)
			.setConnectTimeout(DEFALUT_CONNECT_TIMEOUT * 1000)
			.setConnectionRequestTimeout(DEFALUT_REQUEST_TIMEOUT * 1000)
			.build();

		String jsonResult = "";
		try{
		  HttpPost httpPost = new HttpPost(endpoint + SVG_CONVERTER_URI);
		  httpPost.setConfig(timeoutConfig);
		  MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		  builder.addBinaryBody("file", pdfFile, ContentType.APPLICATION_OCTET_STREAM, pdfFile.getName());
		  HttpEntity multipart = builder.build();
		  httpPost.setEntity(multipart);

			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		  try{
				if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					jsonResult = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				}else{
					logger.error("http response error. status={}", httpResponse.getStatusLine().getStatusCode());
					throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(),
							httpResponse.getStatusLine().getReasonPhrase());
				}
		  }finally{
				try {
					httpResponse.close();
				} catch (IOException e) {
				}
		  }
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
			}
		}

		List<String> fileList = extractFileList(jsonResult);

		logger.perfEnd("ConverterService", startTime);
    return fileList;
	}

	private List<String> extractFileList(String jsonResult) {
		List<String> fileList = new LinkedList<String>();
		if(StringUtils.isEmpty(jsonResult)){
				return fileList;
		}

		JsonArray pareList =  (JsonArray) new Gson().fromJson(jsonResult, JsonArray.class);
		for(JsonElement pare : pareList){
			JsonArray targets = pare.getAsJsonObject().getAsJsonArray("targets");
			if(!ObjectUtils.isEmpty(targets)){
				targets.forEach(target -> fileList.add(target.getAsString()));
			}
		}

		return fileList;
	}

	public List<File> download(List<String> downloadUrls, File tempDir, String prefix, String extension) throws IOException {
		final long startTime = logger.perfStart("DownloadService");

		if(ObjectUtils.isEmpty(downloadUrls)){
			return new LinkedList<File>();
		}

		CloseableHttpClient httpClient = HttpClients.createDefault();
		int socketTimeout = (this.timeout != null) ? this.timeout :DOWNLOAD_SOCKET_TIMEOUT;
		socketTimeout = (socketTimeout/downloadUrls.size() > DOWNLOAD_SOCKET_TIMEOUT) ? socketTimeout/downloadUrls.size() : DOWNLOAD_SOCKET_TIMEOUT;
		RequestConfig timeoutConfig = RequestConfig.custom()
			.setSocketTimeout(socketTimeout * 1000)
			.setConnectTimeout(DEFALUT_CONNECT_TIMEOUT * 1000)
			.setConnectionRequestTimeout(DEFALUT_REQUEST_TIMEOUT * 1000)
			.build();

		List<File> svgFiles = new LinkedList<File>();
		int count = 0;

		try {
			for(String downloadUrl: downloadUrls) {
				count++;
				String svgTempFileName = prefix + String.format("_%04d.", count) + extension;
				File svgTempFile = new File(tempDir.getAbsolutePath() + File.separator + svgTempFileName);

				HttpGet httpGet = new HttpGet(downloadUrl);
				httpGet.setConfig(timeoutConfig);
				CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
				try {
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity responseEntity = httpResponse.getEntity();

						try (InputStream inputStream = responseEntity.getContent()) {
							Files.copy(inputStream, svgTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						}
					} else {
						logger.error("http response error. status={}", httpResponse.getStatusLine().getStatusCode());
						throw new HttpResponseException(httpResponse.getStatusLine().getStatusCode(),
								httpResponse.getStatusLine().getReasonPhrase());
					}
				} finally {
						try {
							 httpResponse.close();
						} catch (IOException e) {
						}
				}
				svgFiles.add(svgTempFile);
			}
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
			}
		}

		logger.perfEnd("DownloadService", startTime);

		return svgFiles;
	}
}
