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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.o365.exception.ApiException;
import com.handywedge.o365.model.ADUserInfo;
import com.handywedge.o365.service.EWSMApiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;

@Path("/api/userinfo")
@Produces({ "application/json" })
@Server(description = "Dev(localhost)",
        url = "http://localhost:8080/handywedge-calendar/api")
//@Consumes({ "application/json" })
public class UserInfoResource {
	private static final Logger logger = LoggerFactory.getLogger(UserInfoResource.class);
	private EWSMApiService service = new EWSMApiService();

	@GET
	@Path("/{userid}")
	@Operation(summary = "ADユーザー情報", tags = { "ADUser" }, responses = {
			@ApiResponse(description = "ADユーザー詳細情報", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ADUserInfo.class))),
			@ApiResponse(responseCode = "400", description = "Invalid userid supplied"),
			@ApiResponse(responseCode = "401", description = "User not found"),
			@ApiResponse(responseCode = "403", description = "User not found") })
	public Response getUserInfo(
			@Parameter(description = "ADユーザーID", required = true) @PathParam("userid") String userid)
			throws ApiException {
		logger.info("[START] {} controller={}, action={}, queryString={}", 0, 1);

		ADUserInfo userInfo = new ADUserInfo();
		try {
			userInfo = service.findContact(userid);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		if (null == userInfo) {
			logger.warn("ADユーザー情報が検出されませんでした。userid={0}", userid);
			throw new com.handywedge.o365.exception.NotFoundException(404, "User not found");
		}

		logger.info("[E N D] controller={}, action={}, elapsed time={}", 0, 1);
		return Response.ok().entity(userInfo).build();
	}
}
