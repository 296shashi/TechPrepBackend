package com.example.techprep.service;

import com.example.techprep.entity.Interview;
import com.example.techprep.entity.User;
import com.example.techprep.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class GoogleService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.oauth.redirectUri}")
    private String redirectUri;

        private final UserRepository userRepository;
        private final com.example.techprep.repository.AdminRepository adminRepository;

    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

        public GoogleService(UserRepository userRepository, com.example.techprep.repository.AdminRepository adminRepository) {
                this.userRepository = userRepository;
                this.adminRepository = adminRepository;
        }

    public String buildAuthorizationUrl(String state) {
        GoogleAuthorizationCodeRequestUrl url =
                new GoogleAuthorizationCodeRequestUrl(
                        clientId,
                        redirectUri,
                        Collections.singletonList("https://www.googleapis.com/auth/calendar.events")
                );

        url.setAccessType("offline");
        if (state != null) url.setState(state);
        return url.build();
    }

    /** Store Google tokens after OAuth callback **/
        public void handleCallbackAndStoreTokens(String code, String userEmail) throws IOException {
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT,
                        JSON_FACTORY,
                        clientId,
                        clientSecret,
                        Collections.singletonList("https://www.googleapis.com/auth/calendar.events")
                ).setAccessType("offline").build();

        GoogleTokenResponse tokenResponse =
                flow.newTokenRequest(code)
                        .setRedirectUri(redirectUri)
                        .execute();

                // If an Admin row exists for this email, store tokens there; otherwise fall back to User
                java.util.Optional<com.example.techprep.entity.Admin> adminOpt = adminRepository.findByEmail(userEmail);
                if (adminOpt.isPresent()) {
                        com.example.techprep.entity.Admin admin = adminOpt.get();
                        admin.setGoogleAccessToken(tokenResponse.getAccessToken());
                        if (tokenResponse.getRefreshToken() != null) admin.setGoogleRefreshToken(tokenResponse.getRefreshToken());
                        if (tokenResponse.getExpiresInSeconds() != null) {
                                admin.setGoogleTokenExpiry(System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000));
                        }
                        adminRepository.save(admin);
                        return;
                }

                User user = userRepository.findByEmail(userEmail)
                                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

                user.setGoogleAccessToken(tokenResponse.getAccessToken());
                if (tokenResponse.getRefreshToken() != null)
                        user.setGoogleRefreshToken(tokenResponse.getRefreshToken());

                if (tokenResponse.getExpiresInSeconds() != null) {
                        user.setGoogleTokenExpiry(
                                        System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000)
                        );
                }

                userRepository.save(user);
    }

    /** Validate or refresh access token **/
    private boolean ensureValidAccessToken(User user) throws IOException {
        if (user.getGoogleAccessToken() == null &&
                user.getGoogleRefreshToken() == null) return false;

        Long expiry = user.getGoogleTokenExpiry();
        if (expiry != null &&
                System.currentTimeMillis() + 60_000 < expiry) return true;

        return refreshAccessToken(user);
    }

    /** Refresh token manually **/
    private boolean refreshAccessToken(User user) throws IOException {
        if (user.getGoogleRefreshToken() == null) return false;

        URL url = new URL("https://oauth2.googleapis.com/token");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String body =
                "client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&refresh_token=" + user.getGoogleRefreshToken() +
                        "&grant_type=refresh_token";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        InputStream is = conn.getResponseCode() >= 200 && conn.getResponseCode() < 300
                ? conn.getInputStream() : conn.getErrorStream();

        JsonObjectParser parser = new JsonObjectParser(JSON_FACTORY);
        Map<String, Object> respMap = parser.parseAndClose(is, StandardCharsets.UTF_8, Map.class);

        if (respMap == null) return false;

        if (respMap.get("access_token") instanceof String accessToken) {
            user.setGoogleAccessToken(accessToken);
        }

        if (respMap.get("expires_in") instanceof Number expiresIn) {
            user.setGoogleTokenExpiry(
                    System.currentTimeMillis() + expiresIn.longValue() * 1000
            );
        }

        userRepository.save(user);
        return true;
    }

        // Admin token helpers
        private boolean ensureValidAccessTokenForAdmin(com.example.techprep.entity.Admin admin) throws IOException {
                if (admin.getGoogleAccessToken() == null && admin.getGoogleRefreshToken() == null) return false;
                Long expiry = admin.getGoogleTokenExpiry();
                if (expiry != null && System.currentTimeMillis() + 60_000 < expiry) return true;
                return refreshAccessTokenForAdmin(admin);
        }

        private boolean refreshAccessTokenForAdmin(com.example.techprep.entity.Admin admin) throws IOException {
                if (admin.getGoogleRefreshToken() == null) return false;

                URL url = new URL("https://oauth2.googleapis.com/token");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String body =
                                "client_id=" + clientId +
                                                "&client_secret=" + clientSecret +
                                                "&refresh_token=" + admin.getGoogleRefreshToken() +
                                                "&grant_type=refresh_token";

                try (OutputStream os = conn.getOutputStream()) {
                        os.write(body.getBytes(StandardCharsets.UTF_8));
                }

                InputStream is = conn.getResponseCode() >= 200 && conn.getResponseCode() < 300
                                ? conn.getInputStream() : conn.getErrorStream();

                JsonObjectParser parser = new JsonObjectParser(JSON_FACTORY);
                Map<String, Object> respMap = parser.parseAndClose(is, StandardCharsets.UTF_8, Map.class);
                if (respMap == null) return false;

                if (respMap.get("access_token") instanceof String accessToken) {
                        admin.setGoogleAccessToken(accessToken);
                }
                if (respMap.get("expires_in") instanceof Number expiresIn) {
                        admin.setGoogleTokenExpiry(System.currentTimeMillis() + expiresIn.longValue() * 1000);
                }
                adminRepository.save(admin);
                return true;
        }

    /** Create Google Meet Link **/
    public String createGoogleMeetForInterview(User user, Interview interview) throws IOException {

        ensureValidAccessToken(user);

        HttpRequestInitializer initializer = request ->
                request.getHeaders().setAuthorization("Bearer " + user.getGoogleAccessToken());

        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, initializer)
                .setApplicationName("techprep")
                .build();

                Event event = new Event()
                                .setSummary(interview.getType() != null ? interview.getType() : "Interview")
                                .setDescription("Interview with " + (interview.getInterviewer() != null ? interview.getInterviewer().getName() : "interviewer"));

                // Datetime
                EventDateTime start = new EventDateTime();
                EventDateTime end = new EventDateTime();
                try {
                        Instant s = Instant.parse(interview.getStartDateTime());
                        Instant e = Instant.parse(interview.getEndDateTime());
                        start.setDateTime(new com.google.api.client.util.DateTime(Date.from(s)));
                        end.setDateTime(new com.google.api.client.util.DateTime(Date.from(e)));
                } catch (Exception ex) {
                        Date now = new Date();
                        start.setDateTime(new com.google.api.client.util.DateTime(now));
                        end.setDateTime(new com.google.api.client.util.DateTime(new Date(now.getTime() + 3600 * 1000)));
                }
                event.setStart(start);
                event.setEnd(end);

                // Add attendees only when the calling user is the dedicated admin and emails are provided
                try {
                        if (user != null && user.getEmail() != null && user.getEmail().equalsIgnoreCase("296shashi@gmail.com")) {
                                java.util.List<EventAttendee> attendees = new java.util.ArrayList<>();
                                if (interview.getInterviewerEmail() != null && !interview.getInterviewerEmail().isEmpty()) {
                                        EventAttendee a = new EventAttendee();
                                        a.setEmail(interview.getInterviewerEmail());
                                        attendees.add(a);
                                }
                                if (interview.getCandidateEmail() != null && !interview.getCandidateEmail().isEmpty()) {
                                        EventAttendee a = new EventAttendee();
                                        a.setEmail(interview.getCandidateEmail());
                                        attendees.add(a);
                                }
                                if (!attendees.isEmpty()) {
                                        event.setAttendees(attendees);
                                }
                        }
                } catch (Exception ignore) {}

                // Google Meet
                ConferenceData conferenceData = new ConferenceData();
                CreateConferenceRequest confReq = new CreateConferenceRequest();
                confReq.setRequestId(UUID.randomUUID().toString());
                conferenceData.setCreateRequest(confReq);

                event.setConferenceData(conferenceData);

        Calendar.Events.Insert request =
                service.events().insert("primary", event).setConferenceDataVersion(1);

        Event created = request.execute();
        String meetLink = null;

        if (created.getConferenceData() != null &&
                created.getConferenceData().getEntryPoints() != null) {

            for (EntryPoint ep : created.getConferenceData().getEntryPoints()) {
                if ("video".equalsIgnoreCase(ep.getEntryPointType())) {
                    meetLink = ep.getUri();
                    break;
                }
            }
        }

        if (meetLink == null)
            meetLink = created.getHangoutLink();

        interview.setMeetLink(meetLink);
        return meetLink;
    }

        /** Create a Google Meet using the dedicated admin account and invite participant emails. */
        public String createGoogleMeetUsingAdminAccount(Interview interview) throws IOException {
                // find admin record
                com.example.techprep.entity.Admin admin = adminRepository.findByEmail("296shashi@gmail.com").orElse(null);
                if (admin == null) return null;
                if (!ensureValidAccessTokenForAdmin(admin)) return null;

                HttpRequestInitializer initializer = request ->
                        request.getHeaders().setAuthorization("Bearer " + admin.getGoogleAccessToken());

                Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, initializer)
                                .setApplicationName("techprep")
                                .build();

                Event event = new Event()
                                .setSummary(interview.getType() != null ? interview.getType() : "Interview")
                                .setDescription("Interview between " + (interview.getCandidateName() != null ? interview.getCandidateName() : "candidate") + " and " + (interview.getInterviewerName() != null ? interview.getInterviewerName() : "interviewer"));

                EventDateTime start = new EventDateTime();
                EventDateTime end = new EventDateTime();
                try {
                        Instant s = Instant.parse(interview.getStartDateTime());
                        Instant e = Instant.parse(interview.getEndDateTime());
                        start.setDateTime(new com.google.api.client.util.DateTime(Date.from(s)));
                        end.setDateTime(new com.google.api.client.util.DateTime(Date.from(e)));
                } catch (Exception ex) {
                        Date now = new Date();
                        start.setDateTime(new com.google.api.client.util.DateTime(now));
                        end.setDateTime(new com.google.api.client.util.DateTime(new Date(now.getTime() + 3600 * 1000)));
                }
                event.setStart(start);
                event.setEnd(end);

                java.util.List<EventAttendee> attendees = new java.util.ArrayList<>();
                // admin as attendee
                EventAttendee adminAtt = new EventAttendee();
                adminAtt.setEmail(admin.getEmail());
                attendees.add(adminAtt);
                if (interview.getInterviewerEmail() != null && !interview.getInterviewerEmail().isEmpty()) {
                        EventAttendee it = new EventAttendee();
                        it.setEmail(interview.getInterviewerEmail());
                        attendees.add(it);
                }
                if (interview.getCandidateEmail() != null && !interview.getCandidateEmail().isEmpty()) {
                        EventAttendee ce = new EventAttendee();
                        ce.setEmail(interview.getCandidateEmail());
                        attendees.add(ce);
                }
                if (!attendees.isEmpty()) event.setAttendees(attendees);

                ConferenceData conferenceData = new ConferenceData();
                CreateConferenceRequest confReq = new CreateConferenceRequest();
                confReq.setRequestId(UUID.randomUUID().toString());
                confReq.setConferenceSolutionKey(
                        new ConferenceSolutionKey().setType("hangoutsMeet")
                );
                conferenceData.setCreateRequest(confReq);
                event.setConferenceData(conferenceData);

                Calendar.Events.Insert request = service.events().insert("primary", event).setConferenceDataVersion(1);
                Event created = request.execute();
                String meetLink = null;
                if (created.getConferenceData() != null && created.getConferenceData().getEntryPoints() != null) {
                        for (EntryPoint ep : created.getConferenceData().getEntryPoints()) {
                                if ("video".equalsIgnoreCase(ep.getEntryPointType())) {
                                        meetLink = ep.getUri();
                                        break;
                                }
                        }
                }
                if (meetLink == null) meetLink = created.getHangoutLink();
                interview.setMeetLink(meetLink);
                return meetLink;
        }

//        public void getGoogleToken() {
//            GoogleTokenResponse response = new GoogleAuthorizationCodeFlow.Builder(
//                    HTTP_TRANSPORT,
//                    JSON_FACTORY,
//                    clientId,
//                    clientSecret,
//                    List.of("https://www.googleapis.com/auth/gmail.readonly")
//            ).build()
//                    .newTokenRequest(code)
//                    .setRedirectUri(redirectUri)
//                    .execute();
//
//            String accessToken = response.getAccessToken();
//            String refreshToken = response.getRefreshToken();
//
//        }
}
