package com.handywedge.converter.topdf.wrapper;

import org.checkerframework.checker.nullness.qual.NonNull;

import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.document.DocumentFormatRegistry;
import org.jodconverter.core.job.AbstractConversionJob;
import org.jodconverter.core.job.AbstractConversionJobWithSourceFormatUnspecified;
import org.jodconverter.core.job.AbstractConverter;
import org.jodconverter.core.job.AbstractSourceDocumentSpecs;
import org.jodconverter.core.job.AbstractTargetDocumentSpecs;
import org.jodconverter.core.office.InstalledOfficeManagerHolder;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.remote.office.RemoteOfficeManager;

/**
 * A remote converter will send conversion request to a LibreOffice Online server. It must be used
 * with an RemoteOfficeManager in order to work as expected.
 *
 * @see org.jodconverter.core.DocumentConverter
 * @see RemoteOfficeManager
 */
public class FWRemoteConverter extends AbstractConverter {

	/**
	 * Creates a new builder instance.
	 *
	 * @return A new builder instance.
	 */
	public static FWRemoteConverter.Builder builder() {
		return new FWRemoteConverter.Builder();
	}

	/**
	 * Creates a new {@link FWRemoteConverter} with default configuration. The {@link
	 * org.jodconverter.core.office.OfficeManager} that will be used is the one holden by the {@link
	 * org.jodconverter.core.office.InstalledOfficeManagerHolder} class, if any.
	 *
	 * @return A {@link FWRemoteConverter} with default configuration.
	 */
	@NonNull
	public static FWRemoteConverter make() {
		return builder().build();
	}

	/**
	 * Creates a new {@link FWRemoteConverter} using the specified {@link
	 * org.jodconverter.core.office.OfficeManager} with default configuration.
	 *
	 * @param officeManager The {@link org.jodconverter.core.office.OfficeManager} the converter will
	 *     use to convert document.
	 * @return A {@link FWRemoteConverter} with default configuration.
	 */
	@NonNull
	public static FWRemoteConverter make(@NonNull final OfficeManager officeManager) {
		return builder().officeManager(officeManager).build();
	}

	private FWRemoteConverter(
		final OfficeManager officeManager, final DocumentFormatRegistry formatRegistry) {
		super(officeManager, formatRegistry);
	}

	@NonNull
	@Override
	protected AbstractConversionJobWithSourceFormatUnspecified convert(
		@NonNull final AbstractSourceDocumentSpecs source) {

		return new FWRemoteConverter.RemoteConversionJobWithSourceFormatUnspecified(source);
	}

	/** Remote implementation of a conversion job with source format unspecified. */
	private class RemoteConversionJobWithSourceFormatUnspecified
		extends AbstractConversionJobWithSourceFormatUnspecified {

		private RemoteConversionJobWithSourceFormatUnspecified(
			final AbstractSourceDocumentSpecs source) {
			super(source, FWRemoteConverter.this.officeManager, FWRemoteConverter.this.formatRegistry);
		}

		@NonNull
		@Override
		protected AbstractConversionJob to(@NonNull final AbstractTargetDocumentSpecs target) {
			return new FWRemoteConverter.RemoteConversionJob(source, target);
		}
	}

	/** Remote implementation of a conversion job. */
	private class RemoteConversionJob extends AbstractConversionJob {

		private RemoteConversionJob(
			final AbstractSourceDocumentSpecs source, final AbstractTargetDocumentSpecs target) {
			super(source, target);
		}

		@Override
		public void doExecute() throws OfficeException {
			// Create a default conversion task and execute it
			final FWRemoteConversionTask task = new FWRemoteConversionTask(source, target);
			officeManager.execute(task);
		}
	}

	/**
	 * A builder for constructing a {@link FWRemoteConverter}.
	 *
	 * @see FWRemoteConverter
	 */
	public static final class Builder extends AbstractConverterBuilder<FWRemoteConverter.Builder> {
		// Private constructor so only FWRemoteConverter can create an instance of this builder.
		private Builder() {
				super();
		}

		@NonNull
		@Override
		public FWRemoteConverter build() {

			// An office manager is required.
			OfficeManager manager = officeManager;
			if (manager == null) {
				manager = InstalledOfficeManagerHolder.getInstance();
				if (manager == null) {
					throw new IllegalStateException(
						"An office manager is required in order to build a converter.");
				}
			}

			// Create the converter
			return new FWRemoteConverter(
				manager,
				formatRegistry == null ? DefaultDocumentFormatRegistry.getInstance() : formatRegistry);
		}
	}
}
