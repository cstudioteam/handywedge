package com.handywedge.converter.tosvg.rest;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handywedge.converter.tosvg.FWPdfConverter;
import com.handywedge.converter.tosvg.exceptions.FWConvertProcessException;
import com.handywedge.converter.tosvg.exceptions.FWUnsupportedFormatException;
import com.handywedge.converter.tosvg.rest.requests.FWErrorResponse;
import com.handywedge.converter.tosvg.utils.FWConstantCode;
import com.handywedge.converter.tosvg.utils.FWConverterConst;
import com.handywedge.converter.tosvg.utils.FWConverterUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/file")
public class FWPDFToSVGJobService {
	private static final Logger logger = LogManager.getLogger(FWPDFToSVGJobService.class);

	@POST
	@Path("/converter/svg")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response converterToSVGs(FormDataMultiPart multiPart, @Context UriInfo uriInfo) {
		final long startTime = System.currentTimeMillis();
		logger.info("{}() start.", "Converter Service");

		List<BodyPart> bodyPartList = multiPart.getBodyParts();

		if(CollectionUtils.isEmpty(bodyPartList)){
			FWErrorResponse errorResponse = new FWErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "cannot specify multiple files.");
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}

		BodyPart bodyPart = bodyPartList.stream().findFirst().get();
		JsonArray resultJson = new JsonArray();
		FWPdfConverter converter = new FWPdfConverter();

		BodyPartEntity bodyPartEntity = (BodyPartEntity) bodyPart.getEntity();
		String fileName = bodyPart.getContentDisposition().getFileName();

		logger.info("Converter File={}", fileName);

		File pdfFile = null;
		List<File> svgFiles = new ArrayList<>();
		try {
			pdfFile = savePDFFile(bodyPartEntity.getInputStream(), fileName);
			svgFiles = converter.pdfToSvg(pdfFile);
			resultJson.add(filesToGson(pdfFile, svgFiles, uriInfo));
		} catch (FWUnsupportedFormatException | FWConvertProcessException e) {
			FileUtils.deleteQuietly(pdfFile);
			svgFiles.stream()
					.filter(File::isFile)
					.forEach(svg ->{FileUtils.deleteQuietly(svg);});
			FWErrorResponse errorResponse = new FWErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}

		logger.info("{}() end.\tElapsedTime[{}]ms", "Converter Service",
					System.currentTimeMillis() - startTime);

		String resultData = new GsonBuilder().setPrettyPrinting().create().toJson(resultJson);

		return Response.ok(resultData).build();
	}

	@GET @Path("/{file}/") @Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadSVGFile(@PathParam("file") String fileName) {
		final long startTime = System.currentTimeMillis();
		logger.info("{}() start.", "Download Service");

		if(StringUtils.isEmpty(fileName)){
			FWErrorResponse errorResponse = new FWErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), "invalid request.");
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}

		logger.info("Download File={}", fileName);

		File downloadSVGFile = new File(FWConverterUtils.getWorkDirectory() + File.separator + fileName);
		if(ObjectUtils.isEmpty(downloadSVGFile) || !downloadSVGFile.isFile()){
				FWErrorResponse errorResponse = new FWErrorResponse(Response.Status.NOT_FOUND.getStatusCode(), String.format("not found. file=%s", fileName));
				return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
		}

		logger.info("{}() end.\tElapsedTime[{}]ms", "Download Service",
					System.currentTimeMillis() - startTime);

		return Response.ok(downloadSVGFile)
			.header("Content-Disposition", "attachment; filename=" + downloadSVGFile.getName())
			.build();
	}

	private JsonObject filesToGson(File source, List<File> targets, UriInfo uriInfo) {
		JsonObject node = new JsonObject();
		String baseURL = StringUtils.stripEnd(uriInfo.getBaseUri().toASCIIString(), "/");

		if (ObjectUtils.isEmpty(source)) {
			node.add("source", null);
		} else {
			final URI downloadUri =
				UriBuilder.fromResource(FWPDFToSVGJobService.class).path("{file}")
					.build(source.getName());
			node.addProperty("source", baseURL + downloadUri.toASCIIString());
		}

		JsonArray fileList = null;
		if (ObjectUtils.isEmpty(targets)) {
			node.add("targets", null);
		} else {
		  fileList = new JsonArray();
		  for (File svg : targets) {
				if (ObjectUtils.isEmpty(svg)) {
						continue;
				}
				final URI downloadUri =
						UriBuilder.fromResource(FWPDFToSVGJobService.class).path("{file}")
								.build(svg.getName());
				fileList.add(baseURL + downloadUri.toASCIIString());
		  }
		  node.add("targets", fileList);
		}

		return node;
	}

	private File savePDFFile(InputStream inStream, String fileName)
		throws FWConvertProcessException {

		File tempPDFFile = null;
		try {
			final String newBaseName = UUID.randomUUID().toString().replace("-", "");
			tempPDFFile = new File(FWConverterUtils.getWorkDirectory() + File.separator + newBaseName + "."
				+ FWConverterConst.EXTENSION_PDF);
			Files.copy(inStream, tempPDFFile.toPath());
		} catch (IOException e) {
			FileUtils.deleteQuietly(tempPDFFile);
			throw new FWConvertProcessException(FWConstantCode.PDF_TO_SVG_FAIL, e);
		}

		return tempPDFFile;
	}


}
