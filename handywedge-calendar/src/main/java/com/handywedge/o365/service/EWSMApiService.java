package com.handywedge.o365.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handywedge.o365.model.ADUserInfo;
import com.handywedge.o365.model.Meeting;
import com.handywedge.o365.model.MeetingFreeBusy;
import com.handywedge.o365.model.RoomFreeBusy;
import com.handywedge.o365.model.RoomInfo;
import com.handywedge.o365.model.SendMailInfo;
import com.handywedge.o365.model.UserInfo;
import com.handywedge.o365.util.DateTimeCalculat;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.availability.AvailabilityData;
import microsoft.exchange.webservices.data.core.enumeration.availability.FreeBusyViewType;
import microsoft.exchange.webservices.data.core.enumeration.availability.MeetingAttendeeType;
import microsoft.exchange.webservices.data.core.enumeration.availability.SuggestionQuality;
import microsoft.exchange.webservices.data.core.enumeration.misc.ConnectingIdType;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.misc.error.ServiceError;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.LegacyFreeBusyStatus;
import microsoft.exchange.webservices.data.core.enumeration.property.PhoneNumberKey;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.ResolveNameSearchLocation;
import microsoft.exchange.webservices.data.core.enumeration.service.ConflictResolutionMode;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsMode;
import microsoft.exchange.webservices.data.core.enumeration.service.SendInvitationsOrCancellationsMode;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.response.AttendeeAvailability;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.core.service.schema.ContactSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ImpersonatedUserId;
import microsoft.exchange.webservices.data.misc.NameResolution;
import microsoft.exchange.webservices.data.misc.NameResolutionCollection;
import microsoft.exchange.webservices.data.misc.OutParam;
import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;
import microsoft.exchange.webservices.data.misc.availability.AvailabilityOptions;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.misc.availability.TimeWindow;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.Mailbox;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.property.complex.availability.CalendarEvent;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

public class EWSMApiService {
	private static final Logger logger = LoggerFactory.getLogger(EWSMApiService.class);

	public static ExchangeService service = null;

	public EWSMApiService() {
		service = ConnectToService("","");
	}

	/// <summary>
	/// 偽装ユーザ
	/// </summary>
	/// <returns>偽装ユーザ</returns>
	private boolean isAutodiscover() {
		return false;	// TODO
		//return Boolean.parseBoolean(PropertiesUtil.get("ews.autodiscover"));
	}

	/// <summary>
	/// 偽装ユーザ
	/// </summary>
	/// <returns>偽装ユーザ</returns>
	private String getUri() {
		return "https://outlook.office365.com/EWS/Exchange.asmx"; // TODO
		//return PropertiesUtil.get("ews.uri");
	}

	/// <summary>
	/// 偽装ユーザ
	/// </summary>
	/// <returns>偽装ユーザ</returns>
	private String getAuthUser() {
		return "li@cstudioteam.onmicrosoft.com";
		//return PropertiesUtil.get("ews.auth.userid");	// TODO
	}

	/**
	 * ユーザIDを取得
	 * @return ユーザID
	 */
	private String getUserId(String stmpArrdess) {
		String tempArrdess = stmpArrdess;
		if(StringUtils.isBlank(tempArrdess)) {
			tempArrdess = getAuthUser();
		}
		if(!tempArrdess.contains("@")) {
			return tempArrdess;
		}
		String[] splits = tempArrdess.split("@");
		return splits[0];
	}

	/**
	 * ドメイン取得
	 * @return ドメイン
	 */
	private String getDomain(String stmpArrdess) {
		String tempArrdess = stmpArrdess;
		if(StringUtils.isBlank(tempArrdess)) {
			tempArrdess = getAuthUser();
		}
		if(!tempArrdess.contains("@")) {
			return "";
		}
		String[] splits = tempArrdess.split("@");
		return splits[1];
	}
//
//	/// <summary>
//	/// 暗号化キー
//	/// </summary>
//	/// <returns>暗号化キー</returns>
//	private String getAuthKey() {
//		return PropertiesUtil.get("ews.auth.key");
//	}
//
//	/// <summary>
//	/// 暗号化IV
//	/// </summary>
//	/// <returns>暗号化キー</returns>
//	private String getAuthIV() {
//		return PropertiesUtil.get("ews.auth.iv");
//	}
//
//	/// <summary>
//	/// 暗号化PassPhase
//	/// </summary>
//	/// <returns>暗号化キー</returns>
//	private String getAuthPassPhase() {
//		return PropertiesUtil.get("ews.auth.passphase");
//	}

	/// <summary>
	/// 偽装パスワード
	/// </summary>
	/// <returns>偽装パスワード</returns>
	private String getAuthPass() {
		return "p@Ss2017";		//TODO
//		return PropertiesUtil.get("ews.auth.passwd");
	}

	/// <summary>
	/// Exchangeサーババージョン
	/// </summary>
	/// <returns>Exchangeサーババージョン</returns>
	private ExchangeVersion GetExchangeVersion() {
		return ExchangeVersion.Exchange2010_SP2;
	}

	public ExchangeService ConnectToService(String userEmailAddress, String userPassword) {
		logger.info("  [START] class={}, method={}", 0, 1);

		ExchangeVersion version = GetExchangeVersion();
		ExchangeService service = new ExchangeService(version);
		logger.info("    EWS Version: {}", service.getRequestedServerVersion().toString());
//		logger.info("    EWS Timezones: {}", service.getServerTimeZones());

		// EWS のレスポンス情報をログに出力
		service.setTraceEnabled(true);

		if (StringUtils.isEmpty(userEmailAddress)) {
			userEmailAddress = getAuthUser ();
		}
		if (StringUtils.isEmpty(userPassword)) {
			userPassword = getAuthPass ();
		}

		logger.info("    EWS Auth Email: {}", userEmailAddress);
		logger.info("    EWS Auth Password: {}", "XXXXXXXXXXXXX");

		// 環境によっては Autodiscover が無理だが、以下に Autodiscover を推奨する記載がある
		// http://msdn.microsoft.com/en-us/library/dn467891(v=exchg.150).aspx
		if (isAutodiscover()) {
			try {
				service.autodiscoverUrl(userEmailAddress, new RedirectionUrlCallback());
			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		} else {
			try {
				service.setUrl(new URI(getUri()));
			} catch (URISyntaxException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		// Set specific credentials.
		ExchangeCredentials credentials = new WebCredentials(
				userEmailAddress,
				userPassword);
		service.setCredentials(credentials);
		try {
			service.validate();
		} catch (ServiceLocalException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		// オンプレミス Exchange サーバーを対象: true
		// Exchange Online または Office 365 開発者向けサイトのメールボックスを対象: false
		service.setUseDefaultCredentials(false);



		logger.info("    EWS Endpoint: {}", service.getUrl());

		service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, userEmailAddress));
		logger.info("    EWS ImpersonatedUserId: {}", service.getImpersonatedUserId().getId());

		logger.info("    EWS HttpHeaders size={}", service.getHttpHeaders().size());

		for (String key : service.getHttpHeaders().keySet()) {
			logger.info("    EWS HttpHeaders detail {}:{}", key, service.getHttpHeaders().get(key));
		}

		logger.info("    EWS HttpResponseHeaders: {}", service.getHttpResponseHeaders().size());
		for (String key : service.getHttpResponseHeaders().keySet()) {
			logger.info("    EWS HttpResponseHeaders detail {}:{}", key, service.getHttpResponseHeaders().get(key));
		}

		logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);

		return service;
	}

	/**
	 * ADからユーザー情報詳細を取得する。
	 *
	 * @param userid
	 *            ADユーザーID
	 * @return ADユーザ詳細情報
	 * @throws Exception
	 */
	public ADUserInfo findContact(String userid) throws Exception {
		logger.info("  [START] class={}, method={}", 0, 1);

		logger.debug("    Input STMP Address:{}", userid);
		if (StringUtils.isEmpty(userid)) {
			logger.warn("    The STMP Address is Not specified.");
			return null;
		}
		String[] alias = userid.split("@");

		PropertySet firstProps = new PropertySet(BasePropertySet.FirstClassProperties, ContactSchema.DisplayName,
				ContactSchema.Surname, ContactSchema.GivenName, ContactSchema.PhoneticFullName,
				ContactSchema.PhoneticFirstName, ContactSchema.PhoneticLastName, ContactSchema.OfficeLocation,
				ContactSchema.Department, ContactSchema.PhoneNumbers, ContactSchema.BusinessPhone);

		NameResolutionCollection coll = service.resolveName(alias[0], ResolveNameSearchLocation.DirectoryThenContacts,
				true, firstProps);

		ADUserInfo uInfo = null;

		for (NameResolution res : coll) {
			if ("SMTP".equals(res.getMailbox().getRoutingType())) {
				uInfo = new ADUserInfo();
				uInfo.setEmailAddress(res.getMailbox().getAddress());
				Contact contact = res.getContact();
				if (contact != null) {
					uInfo.setName(contact.getDisplayName());
					uInfo.setSurName(contact.getSurname());
					uInfo.setGivenName(contact.getGivenName());

					uInfo.setPhoneticName(contact.getPhoneticFullName());
					uInfo.setPhoneticFirstName(contact.getPhoneticFirstName());
					uInfo.setPhoneticLastName(contact.getPhoneticLastName());

					uInfo.setDepartment(contact.getDepartment());
					uInfo.setLocation(contact.getOfficeLocation());

					OutParam<String> phoneNumber = new OutParam<String>();
					boolean PhoneNumberFound = contact.getPhoneNumbers().tryGetValue(PhoneNumberKey.BusinessPhone, phoneNumber);
					logger.debug("PhoneNumbers : {}", phoneNumber.getParam());
					if (PhoneNumberFound) {
						if(StringUtils.isBlank(phoneNumber.getParam())) {
							uInfo.setPhoneNumber("");
						}else {
							uInfo.setPhoneNumber(phoneNumber.getParam().replace("[^0-9]", ""));
						}
						// uInfo.setPhoneNumber(Regex.Replace (phoneNumber.getParam(), "[^0-9]", ""));
						//uInfo.setPhoneNumber(phoneNumber.getParam().replace("[^0-9]", ""));
					} else {
						uInfo.setPhoneNumber("");
					}
				}
			}
		}
		logger.debug("    Output Contact Info:{}", uInfo);

		logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);

		return uInfo;
	}

	/**
	 * 空き時間取得
	 *
	 * @param room
	 *            会議室
	 * @param duration
	 *            間隔
	 * @param startTime
	 *            問い合わせ開始時間
	 * @param endTime
	 *            問い合わせ終了時間
	 * @return 会議室毎の空き時間一覧
	 */

	public List<RoomFreeBusy> getFreeBusyTime(String room, int duration, DateTime startTime, DateTime endTime) {

		logger.info("  [START] class={}, method={}", 0, 1);

		List<AttendeeInfo> attendees = new ArrayList<AttendeeInfo>();
		attendees.add(new AttendeeInfo(room, MeetingAttendeeType.Room, false));

		AvailabilityOptions availabilityOptions = new AvailabilityOptions();
		availabilityOptions.setGoodSuggestionThreshold(49); // 1~49
		availabilityOptions.setMaximumNonWorkHoursSuggestionsPerDay(0);
		availabilityOptions.setMaximumSuggestionsPerDay(10);

		availabilityOptions.setMeetingDuration(duration);
		availabilityOptions.setMinimumSuggestionQuality(SuggestionQuality.Good);
		availabilityOptions
				.setDetailedSuggestionsWindow(new TimeWindow(startTime.toDate(), endTime.plusDays(1).toDate()));
		availabilityOptions.setRequestedFreeBusyView(FreeBusyViewType.Detailed);

		GetUserAvailabilityResults results = null;
		try {
			results = service.getUserAvailability(attendees, availabilityOptions.getDetailedSuggestionsWindow(),
					AvailabilityData.FreeBusyAndSuggestions, availabilityOptions);
		} catch (Exception e) {
			logger.warn("    getUserAvailability not found. {}", e.getMessage());
			e.printStackTrace();
		}

		List<RoomFreeBusy> rFreeBusyList = new ArrayList<RoomFreeBusy>();
		int i = 0;

		for (AttendeeAvailability availability : results.getAttendeesAvailability()) {
			RoomFreeBusy rFreeBusy = new RoomFreeBusy();
			if (availability.getErrorCode() == ServiceError.NoError) {
				logger.info("Room information for {}", attendees.get(i).getSmtpAddress());
				rFreeBusy.setName(null);
				rFreeBusy.setEmailAddress(attendees.get(i).getSmtpAddress());
				rFreeBusy.setDuration(duration);
				rFreeBusy.setStartTime(startTime.toDate());
				rFreeBusy.setEndTime(endTime.toDate());
				if (rFreeBusy.getMeetingFreeBusies() == null) {
					rFreeBusy.setMeetingFreeBusies(new ArrayList<MeetingFreeBusy>());
				}
				for (CalendarEvent calEvent : availability.getCalendarEvents()) {
					MeetingFreeBusy mFreeBusy = new MeetingFreeBusy();

					if (calEvent.getFreeBusyStatus() == LegacyFreeBusyStatus.Busy
							|| calEvent.getFreeBusyStatus() == LegacyFreeBusyStatus.Tentative) {
						mFreeBusy.setStatus(LegacyFreeBusyStatus.Busy.name());
					} else {
						continue;
					}
					mFreeBusy.setStartTime(calEvent.getStartTime());
					mFreeBusy.setEndTime(calEvent.getEndTime());

					if (calEvent.getDetails() != null) {
						mFreeBusy.setSubject(calEvent.getDetails().getSubject());
						mFreeBusy.setLocation(calEvent.getDetails().getLocation());
						rFreeBusy.setName(calEvent.getDetails().getLocation());
						mFreeBusy.setMeeting(calEvent.getDetails().isMeeting());
						mFreeBusy.setCancelled(false);
					}

					rFreeBusy.getMeetingFreeBusies().add(mFreeBusy);
				}
			} else {
				logger.info("Room information for {}", attendees.get(i).getSmtpAddress());
				continue;
			}

			rFreeBusyList.add(rFreeBusy);
			i++;
		}

		logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);

		return rFreeBusyList;
	}

	/**
	 * 会議一覧取得
	 *
	 * @see 過ぎた時間帯において会議を予約すると、自動拒否されてしまいます。 そのため、会議室側の予約は登録されなく、予約者側の予約とも直される。
	 * @param mtParam
	 *            会議一覧取得パラメータ
	 * @return 会議一覧
	 */
	public List<Meeting> getRoomMeetingList(Meeting mtParam) {
		logger.info("  [START] class={}, method={}", 0, 1);

		String eMailAddress = "";
		if (null != mtParam.getResources() && mtParam.getResources().size() != 0) {
			Optional<RoomInfo> optEmailAddress = mtParam.getResources().stream().findFirst();
			if (optEmailAddress.isPresent()) {
				eMailAddress = optEmailAddress.get().getEmailAddress();
			}
		} else {
			eMailAddress = getAuthUser();
		}

		service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, eMailAddress));
		logger.debug("    ImpersonatedUserId: {}", service.getImpersonatedUserId().getId());

		List<Meeting> meetingList = new ArrayList<Meeting>();
		PropertySet pSet = new PropertySet(BasePropertySet.IdOnly);
		FolderId folderIdFromCalendar = new FolderId(WellKnownFolderName.Calendar, new Mailbox(eMailAddress));

		FindItemsResults<Appointment> findResults = null;
		try {
			CalendarFolder folder = CalendarFolder.bind(service, folderIdFromCalendar, pSet);
			CalendarView view = new CalendarView(DateTimeCalculat.plusMinutes(mtParam.getStartTime(), 1),
					mtParam.getEndTime());
			findResults = service.findAppointments(WellKnownFolderName.Calendar, view);
			if (findResults == null || findResults.getTotalCount() == 0) {
				logger.warn("    The Appointment is Not Founded.");
				return meetingList;
			}

			PropertySet propertySet = new PropertySet(BasePropertySet.IdOnly, AppointmentSchema.Subject,
					AppointmentSchema.Location, AppointmentSchema.Start, AppointmentSchema.End,
					AppointmentSchema.Sensitivity, AppointmentSchema.Organizer, AppointmentSchema.RequiredAttendees,
					AppointmentSchema.OptionalAttendees, AppointmentSchema.Resources);

			service.loadPropertiesForItems((Iterable) findResults, propertySet);

			for (Appointment appointment : findResults.getItems()) {
				Meeting meeting = new Meeting();

				meeting.setId(appointment.getId().toString());
				meeting.setLocation(appointment.getLocation());
				meeting.setSubject(appointment.getSubject());
				meeting.setStartTime(appointment.getStart());
				meeting.setEndTime(appointment.getEnd());
				meeting.setSensibility(appointment.getSensitivity().name());
				meeting.setOrganizer(
						new UserInfo(appointment.getOrganizer().getName(), appointment.getOrganizer().getAddress()));

				logger.debug("--------------------------------------");
				logger.debug("RequiredAttendees: " + appointment.getRequiredAttendees().getCount());
				logger.debug("OptionalAttendees: " + appointment.getOptionalAttendees().getCount());
				logger.debug("Resources:         " + appointment.getResources().getCount());
				logger.debug("--------------------------------------");
				for (Attendee rAttd : appointment.getRequiredAttendees()) {
					UserInfo userInfo = new UserInfo(rAttd.getName(), rAttd.getAddress());
					meeting.getRequiredAttendees().add(userInfo);
				}

				for (Attendee oAttd : appointment.getOptionalAttendees()) {
					UserInfo userInfo = new UserInfo(oAttd.getName(), oAttd.getAddress());
					meeting.getOptionalAttendees().add(userInfo);
				}

				for (Attendee resource : appointment.getResources()) {
					RoomInfo roomInfo = new RoomInfo(resource.getName(), resource.getAddress());
					if (null == meeting.getResources()) {
						meeting.setResources(new ArrayList<RoomInfo>());
					}
					meeting.getResources().add(roomInfo);
				}

				if (meeting.getResources() == null || meeting.getResources().isEmpty()) {
					meeting.setResources(new ArrayList<RoomInfo>());
					if (!StringUtils.isEmpty(appointment.getLocation())) {
						for (String roomName : appointment.getLocation().split(";")) {
							RoomInfo ri = findRoomByName(roomName);
							if (ri != null) {
								meeting.getResources().add(ri);
							}
						}
					}
				}

				meetingList.add(meeting);
			}
		} catch (Exception e) {
			logger.warn("    findAppointments not found. {}", e.getMessage());
			e.printStackTrace();
		}

		logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);

		return meetingList;
	}



	private RoomInfo findRoomByName(String name) {
		logger.info("  [START] class={}, method={}", 0, 1);

		logger.debug("    Input STMP Address:{}", name);
		if (StringUtils.isEmpty(name)) {
			logger.warn("    The STMP Address is Not specified.");
			return null;
		}
		String[] alias = name.split("@");

		PropertySet firstProps = new PropertySet(BasePropertySet.FirstClassProperties);
		NameResolutionCollection coll = null;
		try {
			coll = service.resolveName(alias[0], ResolveNameSearchLocation.ContactsThenDirectory, true, firstProps);
		} catch (Exception e) {
			logger.warn("    resolveName not found. {}", e.getMessage());
			e.printStackTrace();
		}

		RoomInfo rInfo = null;

		for (NameResolution res : coll) {
			if ("SMTP".equals(res.getMailbox().getRoutingType())) {
				rInfo = new RoomInfo(res.getMailbox().getName(), res.getMailbox().getAddress());
				break;
			}
		}
		logger.debug("    Output Contact Info:{}", rInfo);

		logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);

		return rInfo;
	}

    private String getResolveName (String smtpAddress) {
    		logger.info("  [START] class={}, method={}", 0, 1);

		logger.debug("    Input STMP Address:{}", smtpAddress);

        if(StringUtils.isBlank(smtpAddress)){
        	    logger.warn("    The STMP Address is Not specified.");
            return "";
        }
        String[] alias = smtpAddress.split("@");

        PropertySet firstProps = new PropertySet (BasePropertySet.FirstClassProperties);
        NameResolutionCollection ncCol = null;
		try {
			ncCol = service.resolveName (alias[0],
			    ResolveNameSearchLocation.DirectoryOnly,
			    true, firstProps);
		} catch (Exception e) {
			logger.warn("    resolveName not found. {}", e.getMessage());
			e.printStackTrace();
		}

        String name = "";
        for (NameResolution res : ncCol) {
            if("SMTP".equals(res.getMailbox().getRoutingType())){
                name = res.getMailbox().getName();
                break;
            }
        }

        logger.debug ("    Output Contact Name:{}", name);

        logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
        return name;
    }

	/**
	 * 会議IDより会議情報を取得
	 *
	 * @param mtParam
	 *            会議一覧取得パラメータ
	 * @return 会議情報
	 */
	public Meeting getMeetingById(Meeting mtParam) {
		logger.info("  [START] class={}, method={}", 0, 1);

		service.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, getAuthUser()));
		logger.debug("    ImpersonatedUserId: {0}", service.getImpersonatedUserId().getId());

		Appointment appointment = getAppointment(mtParam.getId());
		Meeting meeting = new Meeting();
		try {
			meeting.setId(appointment.getId().toString());
			meeting.setLocation(appointment.getLocation());
			meeting.setSubject(appointment.getSubject());
			meeting.setStartTime(appointment.getStart());
			meeting.setEndTime(appointment.getEnd());
			meeting.setSensibility(appointment.getSensitivity().name());
			meeting.setOrganizer(
					new UserInfo(appointment.getOrganizer().getName(), appointment.getOrganizer().getAddress()));

			logger.debug("--------------------------------------");
			logger.debug("RequiredAttendees: " + appointment.getRequiredAttendees().getCount());
			logger.debug("OptionalAttendees: " + appointment.getOptionalAttendees().getCount());
			logger.debug("Resources:         " + appointment.getResources().getCount());
			logger.debug("--------------------------------------");

			for (Attendee rAttd : appointment.getRequiredAttendees()) {
				UserInfo userInfo = new UserInfo(rAttd.getName(), rAttd.getAddress());
				meeting.getRequiredAttendees().add(userInfo);
			}

			for (Attendee oAttd : appointment.getOptionalAttendees()) {
				UserInfo userInfo = new UserInfo(oAttd.getName(), oAttd.getAddress());
				meeting.getOptionalAttendees().add(userInfo);
			}

			for (Attendee resource : appointment.getResources()) {
				RoomInfo roomInfo = new RoomInfo(resource.getName(), resource.getAddress());
				if (null == meeting.getResources()) {
					meeting.setResources(new ArrayList<RoomInfo>());
				}
				meeting.getResources().add(roomInfo);
			}

			if (meeting.getResources() == null || meeting.getResources().isEmpty()) {
				meeting.setResources(new ArrayList<RoomInfo>());
				if (!StringUtils.isEmpty(appointment.getLocation())) {
					for (String roomName : appointment.getLocation().split(";")) {
						RoomInfo ri = findRoomByName(roomName);
						if (ri != null) {
							meeting.getResources().add(ri);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
		return meeting;
	}

	private Appointment getAppointment(String meetingId) {
		Appointment appointment = null;

		try {
			int n = 0;
			while (n++ < 10) {
				appointment = Appointment.bind(service, new ItemId(meetingId),
						new PropertySet(BasePropertySet.IdOnly, AppointmentSchema.Subject, AppointmentSchema.Location,
								AppointmentSchema.Start, AppointmentSchema.End, AppointmentSchema.Sensitivity,
								AppointmentSchema.Organizer, AppointmentSchema.RequiredAttendees,
								AppointmentSchema.OptionalAttendees, AppointmentSchema.Resources));

				Thread.sleep(500);
				if (appointment.getOrganizer() != null) {
					break;
				}
			}
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return appointment;
	}

	/**
	 * メール送信処理
	 * @param mailInfo メール情報
	 */
	public void sendMail(SendMailInfo mailInfo) {
		logger.info("  [START] class={}, method={}", 0, 1);
		logger.info ("    メール送信開始: {}", mailInfo.getRecipients());


		try {
	        EmailMessage message = new EmailMessage(service);
	        service.setImpersonatedUserId(new ImpersonatedUserId (ConnectingIdType.SmtpAddress, mailInfo.getFrom()));
	        logger.debug ("    ImpersonatedUserId: {}", service.getImpersonatedUserId().getId());

	        // Set properties on the email message.
	        message.setFrom(new EmailAddress(mailInfo.getFrom()));
	        Iterator<EmailAddress> rcpnts = mailInfo.getRecipients().stream()
	            .map(r -> new EmailAddress(r))
	            .iterator();
	        message.getToRecipients().addEmailRange(rcpnts);
	        message.setSubject(mailInfo.getSubject());
	        message.setBody(new MessageBody(mailInfo.getBody()));
	        message.getBody().setBodyType(mailInfo.getType());


	        message.sendAndSaveCopy();

	        logger.info(
	                "    メール送信： subject '" + message.getSubject()
	                + "' has been sent to '" + message.getToRecipients()
	                + "' and saved in the SendItems folder.");
		}catch(Exception e) {
			logger.warn("    send mail error. {}", e.getMessage());
			e.printStackTrace();
		}

        logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
	}

	/**
	 * 会議予約
	 * @param reservInfo 予約する会議情報
	 */
	public void reservationMeeting(Meeting reservInfo) {
		logger.info("  [START] class={}, method={}", 0, 1);

		try {
            // -----------------------------------
            // 予約者側に会議を作成（会議室指定なし）
            // -----------------------------------
            service.setImpersonatedUserId(
            		new ImpersonatedUserId (ConnectingIdType.SmtpAddress, reservInfo.getOrganizer().getEmailAddress()));
            logger.debug ("    ImpersonatedUserId: {0}", service.getImpersonatedUserId().getId());

            Appointment rsvMeeting = new Appointment (service);
            rsvMeeting.setSubject(reservInfo.getSubject());
            rsvMeeting.setStart(reservInfo.getStartTime());
            rsvMeeting.setEnd (reservInfo.getEndTime());
            rsvMeeting.setLocation (getResolveName (reservInfo.getResources().get(0).getEmailAddress()));

            logger.debug ("    Organizer: {0}", reservInfo.getOrganizer().getEmailAddress());
            logger.debug ("    Meeting.Location: {0}", rsvMeeting.getLocation());

            for (RoomInfo resource : reservInfo.getResources()) {
                logger.debug ("    Meeting.Resource: {0}", resource);
                rsvMeeting.getResources().add (resource.getEmailAddress());
            }

            // 会議室なしで予約作成
            rsvMeeting.save (WellKnownFolderName.Calendar, SendInvitationsMode.SendToAllAndSaveCopy);
		}catch(Exception e) {
			logger.warn("    reservationMeeting error. {}", e.getMessage());
			e.printStackTrace();
		}

        logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
	}

	/**
	 * 会議終了
	 * @param reservInfo 会議終了情報
	 */
	public void endMeeting(Meeting endInfo) {
		logger.info("  [START] class={}, method={}", 0, 1);

		try {
			Appointment chMeeting = getAppointment(endInfo.getId());
            chMeeting.setEnd(DateTimeCalculat.CurrentTimeWithRest().toDate());

            chMeeting.update (ConflictResolutionMode.AlwaysOverwrite, SendInvitationsOrCancellationsMode.SendToAllAndSaveCopy);

		}catch(Exception e) {
			logger.warn("    reservationMeeting error. {}", e.getMessage());
			e.printStackTrace();
		}
        logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
	}

	/**
	 * 会議延長
	 * @param extenInfo 会議延長情報
	 */
	public void extensionMeeting(Meeting extenInfo) {
		logger.info("  [START] class={}, method={}", 0, 1);

		try {
			Appointment chMeeting = getAppointment(extenInfo.getId());
            Date newEndTime = DateTimeCalculat.plusMinutes(chMeeting.getEnd(), extenInfo.getExtensionTime());
            chMeeting.setEnd(newEndTime);

            chMeeting.update (ConflictResolutionMode.AlwaysOverwrite, SendInvitationsOrCancellationsMode.SendToAllAndSaveCopy);
		}catch(Exception e) {
			logger.warn("    reservationMeeting error. {}", e.getMessage());
			e.printStackTrace();
		}
        logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
	}

	/**
	 * 会議キャンセル
	 * @param extenInfo 会議キャンセル情報
	 */
	public void cancelMeeting(Meeting cancelInfo) {
		logger.info("  [START] class={}, method={}", 0, 1);

		try {
			Appointment chMeeting = getAppointment(cancelInfo.getId());

			chMeeting.delete(DeleteMode.HardDelete);

		}catch(Exception e) {
			logger.warn("    reservationMeeting error. {}", e.getMessage());
			e.printStackTrace();
		}
        logger.info("  [E N D] class={}, method={}, elapsed time={}", 0, 1);
	}

}
