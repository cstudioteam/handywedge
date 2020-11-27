package com.handywedge.converter.topdf.wrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jodconverter.core.job.SourceDocumentSpecs;
import org.jodconverter.core.job.TargetDocumentSpecs;
import org.jodconverter.core.office.OfficeContext;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.remote.office.RemoteOfficeContext;
import org.jodconverter.remote.office.RequestConfig;
import org.jodconverter.remote.task.AbstractRemoteOfficeTask;
import org.jodconverter.remote.task.RemoteConversionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Objects;

public class FWRemoteConversionTask extends AbstractRemoteOfficeTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteConversionTask.class);
	private static final String FILTER_DATA = "FilterData";
	private static final String FILTER_DATA_PREFIX_PARAM = "fd";
	private static final String LOAD_PROPERTIES_PREFIX_PARAM = "l";
	private static final String STORE_PROPERTIES_PREFIX_PARAM = "s";

	private final TargetDocumentSpecs target;

	/**
	 * Creates a new conversion task from a specified source to a specified target.
	 *
	 * @param source The source specifications for the conversion.
	 * @param target The target specifications for the conversion.
	 */
	public FWRemoteConversionTask(
		@NonNull final SourceDocumentSpecs source, @NonNull final TargetDocumentSpecs target) {
		super(source);

		this.target = target;
	}

	@SuppressWarnings("unchecked")
	private void addPropertiesToBuilder(
		final URIBuilder uriBuilder,
		final String parameterPrefix,
		final Map<String, Object> properties) {

		if (properties != null) {
			for (final Map.Entry<String, Object> entry : properties.entrySet()) {
				final String key = entry.getKey();
				final Object value = entry.getValue();

				// First, check if we are dealing with the FilterData property
				if (FILTER_DATA.equalsIgnoreCase(key) && value instanceof Map) {
					// Add all the FilterData properties
					for (final Map.Entry<String, Object> fdentry : ((Map<String, Object>) value).entrySet()) {
						uriBuilder.addParameter(
							parameterPrefix + FILTER_DATA_PREFIX_PARAM + fdentry.getKey(),
							fdentry.getValue().toString());
					}
				} else {
					uriBuilder.addParameter(parameterPrefix + key, value.toString());
				}
			}
		}
	}

	@Override
	public void execute(@NonNull final OfficeContext context) throws OfficeException {

		LOGGER.info("Executing remote conversion task...");
		final RemoteOfficeContext remoteContext = (RemoteOfficeContext) context;

		// Obtain a source file that can be loaded by office. If the source
		// is an input stream, then a temporary file will be created from the
		// stream. The temporary file will be deleted once the task is done.
		final File sourceFile = source.getFile();
		try {

			// Get the target file (which is a temporary file if the
			// output target is an output stream).
			final File targetFile = target.getFile();

			try {
				// TODO: Add the ability to pass on a custom charset to FileBody
				// See https://github.com/LibreOffice/online/blob/master/wsd/reference.txt
				final HttpEntity entity =
					MultipartEntityBuilder.create().addPart("data", new FileBody(sourceFile)).build();

				// Use the fluent API to post the file and save the response into the target file.
				final RequestConfig requestConfig = remoteContext.getRequestConfig();
				final URIBuilder uriBuilder = new URIBuilder(buildUrl(requestConfig.getUrl()));

				// We suppose that the server supports custom load properties, but LibreOffice Online
				// does not support custom load properties, only the sample web service do.
				addPropertiesToBuilder(
					uriBuilder,
					LOAD_PROPERTIES_PREFIX_PARAM,
					Objects.requireNonNull(target.getFormat()).getLoadProperties());

				// We suppose that the server supports custom store properties, but LibreOffice Online
				// does not support custom store properties, only the sample web service do.
				addPropertiesToBuilder(
					uriBuilder,
					STORE_PROPERTIES_PREFIX_PARAM,
					target
						.getFormat()
						.getStoreProperties(Objects.requireNonNull(source.getFormat()).getInputFamily()));

				HttpResponse httpResponse = Executor.newInstance(remoteContext.getHttpClient())
					.execute(
						// Request.Post(buildUrl(requestConfig.getUrl()))
						Request.Post(uriBuilder.build())
							.connectTimeout(Math.toIntExact(requestConfig.getConnectTimeout()))
							.socketTimeout(Math.toIntExact(requestConfig.getSocketTimeout()))
							.body(entity))
					.returnResponse();

				StatusLine statusLine = httpResponse.getStatusLine();
				if((statusLine.getStatusCode() == 401)
					&& hasPasswordError(httpResponse.headerIterator()) ){
					throw new HttpResponseException(400, "LockedWithPassword");
				} else if (statusLine.getStatusCode() >= 300) {
					throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
				} else {
					FileOutputStream out = new FileOutputStream(targetFile);
					try {
						HttpEntity responseEntity = httpResponse.getEntity();
						if (responseEntity != null) {
						  responseEntity.writeTo(out);
						}
					} finally {
						out.close();
					}
				}

				// onComplete on target will copy the temp file to
				// the OutputStream and then delete the temp file
				// if the output is an OutputStream
				target.onComplete(targetFile);

			} catch (Exception ex) {
				LOGGER.error("Remote conversion failed.", ex);
				if((ex instanceof HttpResponseException)
					&& "LockedWithPassword".equals(((HttpResponseException)ex).getReasonPhrase())) {
					final FWOfficeException fwOfficeEx = new FWOfficeException("Remote conversion failed", ex);
					target.onFailure(targetFile, fwOfficeEx);
					throw fwOfficeEx;
				}else{
					final OfficeException officeEx = new OfficeException("Remote conversion failed", ex);
					target.onFailure(targetFile, officeEx);
					throw officeEx;
				}
			}

		} finally {

			// Here the source file is no longer required so we can delete
			// any temporary file that has been created if required.
			source.onConsumed(sourceFile);
		}
	}

	private boolean hasPasswordError(HeaderIterator headerIterator){
		boolean hasError = false;

		while (headerIterator.hasNext()){
			Header header = headerIterator.nextHeader();
			if(!"X-ERROR-KIND".equals(header.getName())){
				continue;
			}

			if(header.getValue().equals("passwordrequired:to-view")
				|| header.getValue().equals("passwordrequired:to-modify")
			  || header.getValue().equals("wrongpassword")){
				hasError = true;
				break;
			}
		}

		return hasError;
	}

	private String buildUrl(final String connectionUrl) {

		// an example URL is like:
		// http://localhost:9980/lool/convert-to/docx
		return StringUtils.appendIfMissing(connectionUrl, "/")
				+ Objects.requireNonNull(target.getFormat()).getExtension();
	}

	@NonNull
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "source=" + source + ", target=" + target + '}';
	}
}
