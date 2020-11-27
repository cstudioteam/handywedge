package com.handywedge.converter.topdf.wrapper;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jodconverter.core.office.OfficeException;

public class FWOfficeException extends OfficeException {
	public FWOfficeException(@NonNull String message) {
		super(message);
	}

	public FWOfficeException(@NonNull String message, @NonNull Throwable cause) {
		super(message, cause);
	}
}
