/**
 *  Copyright 2016 SmartBear Software
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.handywedge.o365.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.o365.exception.ApiException;
import com.handywedge.o365.model.SendMailInfo;
import com.handywedge.o365.service.EWSMApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Path("/api/sendmail")
@Produces({ "application/json" })
@Server(description = "Dev(localhost)",
        url = "http://localhost:8080/handywedge-calendar/api")
//@Consumes({ "application/json" })
public class EmailResource {
	private static final Logger logger = LoggerFactory.getLogger(EmailResource.class);
	private EWSMApiService service = new EWSMApiService();

	@POST
	@Consumes({ "application/json" })
	@Operation(summary = "メール送信処理", tags = { "SendMail" }, responses = {
			@ApiResponse(responseCode = "201", description = "Send mail Successed"),
			@ApiResponse(responseCode = "400", description = "Invalid EmailAddress supplied"),
			@ApiResponse(responseCode = "404", description = "EmailAddress not found"),
			@ApiResponse(responseCode = "415", description = "HTTP 415 Unsupported Media Type")})
	public Response sendMail(
			@RequestBody(
					description = "メール送信情報", required = true,
					content = @Content( schema = @Schema(implementation = SendMailInfo.class))) final SendMailInfo sendMailInfo)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

        SendMailInfo emailInfo = sendMailInfo;
        service.sendMail(emailInfo);

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);
		return Response.ok().entity("").build();
	}
}
