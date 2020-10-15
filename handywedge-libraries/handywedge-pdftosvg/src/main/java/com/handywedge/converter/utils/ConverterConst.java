package com.handywedge.converter.utils;

import java.util.Arrays;
import java.util.List;

public class ConverterConst {

	public static final String EXTENSION_DOC = "doc";   // Word 2007 より前の Microsoft Word 文書
	public static final String EXTENSION_DOCX = "docx"; // Microsoft Word 文書
	public static final String EXTENSION_DOT = "dot";   // Word 2007 より前の Microsoft Word テンプレート
	public static final String EXTENSION_DOTX = "dotx"; // Microsoft Word テンプレート
	public static final String EXTENSION_DOCM = "docm"; // Microsoft Word マクロ有効文書

	public static final String EXTENSION_XLS = "xls";   // Excel 2007 より前の Microsoft Excel ブック
	public static final String EXTENSION_XLSX = "xlsx"; // Excel 2007 以降の Microsoft Excel ブック
	public static final String EXTENSION_XLT = "xlt";   // Excel 2007 より前の Microsoft Excel テンプレート
	public static final String EXTENSION_XLTX = "xltx"; // Excel 2007 以降の Microsoft Excel テンプレート
	public static final String EXTENSION_XLM = "xlm";   // Excel 2007 より前の Microsoft Excel マクロ
	public static final String EXTENSION_XLTM = "xltm"; // Excel 2007 以降の Microsoft Excel マクロ有効テンプレート
	public static final String EXTENSION_XLSM = "xlsm"; // Excel 2007 以降の Microsoft Excel マクロ有効ブック
	public static final String EXTENSION_XLA = "xla";   // Microsoft Excel アドインまたはマクロ ファイル
	public static final String EXTENSION_XLAM = "xlam"; // Excel 2007 以降の Microsoft Excel アドイン
	public static final String EXTENSION_XLL = "xll";   // Microsoft Excel DLL ベースのアドイン

	public static final String EXTENSION_PPT = "ppt";   // PowerPoint 2007 より前の Microsoft PowerPoint テンプレート
	public static final String EXTENSION_PPTX = "pptx"; // Microsoft PowerPoint プレゼンテーション
	public static final String EXTENSION_PPTM = "pptm"; // Microsoft PowerPoint マクロ有効プレゼンテーション
	public static final String EXTENSION_PPS = "pps";   // PowerPoint 2007 より前の Microsoft PowerPoint スライドショー
	public static final String EXTENSION_PPSM = "ppsm"; // Microsoft PowerPoint マクロ有効スライドショー
	public static final String EXTENSION_PPAM = "ppam"; // Microsoft PowerPoint アドイン
	public static final String EXTENSION_PPSX = "ppsx"; // Microsoft PowerPoint スライドショー
	public static final String EXTENSION_POT = "pot";   // PowerPoint 2007 より前の Microsoft PowerPoint 形式
	public static final String EXTENSION_POTX = "potx"; // Microsoft PowerPoint テンプレート
	public static final String EXTENSION_POTM = "potm"; // Microsoft PowerPoint マクロ有効テンプレート


	public static final String EXTENSION_PDF = "pdf";

	public static final String EXTENSION_SVG = "svg";

	public static final List<String> OFFICE_DOCUMENT_EXTENSIONS = Arrays.asList(
			EXTENSION_DOC, EXTENSION_DOCX,
			EXTENSION_DOT, EXTENSION_DOTX, EXTENSION_DOCM,
			EXTENSION_XLS, EXTENSION_XLSX,
			EXTENSION_XLT, EXTENSION_XLTX,
			EXTENSION_XLM, EXTENSION_XLTM, EXTENSION_XLSM,
			EXTENSION_XLA, EXTENSION_XLAM, EXTENSION_XLL,
			EXTENSION_PPT, EXTENSION_PPTX,
			EXTENSION_PPTM, EXTENSION_PPS,
			EXTENSION_PPSM, EXTENSION_PPAM, EXTENSION_PPSX,
			EXTENSION_POT, EXTENSION_POTX, EXTENSION_POTM
	);

	public static final List<String> PDF_DOCUMENT_EXTENSIONS = Arrays.asList(EXTENSION_PDF);


//	public static final String PARAMETER_OFFICE_PORT = "office.port";
//	public static final String PARAMETER_OFFICE_HOME = "office.home";
//	public static final String PARAMETER_OFFICE_PROFILE = "office.profile";
//	public static final String PARAMETER_FILEUPLOAD_FILE_SIZE_MAX = "fileupload.fileSizeMax";

}
