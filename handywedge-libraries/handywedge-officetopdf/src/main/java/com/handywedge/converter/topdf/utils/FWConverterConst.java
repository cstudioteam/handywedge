package com.handywedge.converter.topdf.utils;

import java.util.Arrays;
import java.util.List;

public class FWConverterConst {

	public static final String EXTENSION_DOC = "doc"; // Word 2007 より前の Microsoft Word 文書
	public static final String EXTENSION_DOCX = "docx"; // Microsoft Word 文書

	public static final String EXTENSION_XLS = "xls"; // Excel 2007 より前の Microsoft Excel ブック
	public static final String EXTENSION_XLSX = "xlsx"; // Excel 2007 以降の Microsoft Excel ブック

	public static final String EXTENSION_PPT = "ppt"; // PowerPoint 2007 より前の Microsoft PowerPoint
	public static final String EXTENSION_PPTX = "pptx"; // Microsoft PowerPoint プレゼンテーション



	public static final String EXTENSION_PDF = "pdf";

	public static final List<String> OFFICE_DOCUMENT_EXTENSIONS = Arrays
		.asList(EXTENSION_DOC, EXTENSION_DOCX, EXTENSION_XLS, EXTENSION_XLSX, EXTENSION_PPT,
			EXTENSION_PPTX);
}
