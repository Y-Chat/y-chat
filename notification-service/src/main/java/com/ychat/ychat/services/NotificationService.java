package com.ychat.ychat.services;

import com.asyncapi.gen.notification.model.Notification;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.*;
import com.openapi.gen.social.dto.ChatMemberDTO;
import com.ychat.ychat.enums.NotificationTypeEnum;
import com.ychat.ychat.models.UserFirebaseTokenMapping;
import com.ychat.ychat.repositories.UserFirebaseTokenMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final UserFirebaseTokenMappingRepository userFirebaseTokenMappingRepository;

    private final SocialServiceConnector socialServiceConnector;

    public NotificationService(
            @Autowired UserFirebaseTokenMappingRepository userFirebaseTokenMappingRepository,
            @Autowired SocialServiceConnector socialServiceConnector) {
        this.userFirebaseTokenMappingRepository = userFirebaseTokenMappingRepository;
        this.socialServiceConnector = socialServiceConnector;
    }

    public void updateToken(UUID userId, String userFirebaseUid, String token) {
        var mapping = userFirebaseTokenMappingRepository.findById(userId).orElseGet(() -> new UserFirebaseTokenMapping(userId, new HashSet<>()));
        mapping.setFirebaseUid(userFirebaseUid);
        if(mapping.getFcmTokens().stream().noneMatch(x -> x.getToken().equals(token))) {
            var fcmTokens = mapping.getFcmTokens();
            fcmTokens.add(new UserFirebaseTokenMapping.FirebaseToken(token, LocalDateTime.now()));
            mapping.setFcmTokens(fcmTokens);
        }
        userFirebaseTokenMappingRepository.save(mapping);
    }

    private void sendInAppNotification(UUID userUUID, Map<String, String> data) {
        Firestore db = FirestoreClient.getFirestore();
        var usersCollection = db.collection("users");
        var userFirebaseTokenMapping = userFirebaseTokenMappingRepository.findById(userUUID);
        if(userFirebaseTokenMapping.isEmpty()) {
            logger.warn("In App Notification should be sent to user with UUID " + userUUID + " but they don't have a fcm uid registered");
            return;
        }

        usersCollection.document(userFirebaseTokenMapping.get().getFirebaseUid()).collection("notifications").document(""+LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset())).set(data);
        logger.info("Sent in app notification to UUID: " + userUUID + " with uid: " + userFirebaseTokenMapping.get().getFirebaseUid());
    }

    private void sendOfflineNotificationToUser(UUID userUUID, Message.Builder messageBuilder, Map<UUID, List<String>> staleTokens) {
        var firebaseMessaging = FirebaseMessaging.getInstance();

        var userFirebaseTokenMapping = userFirebaseTokenMappingRepository.findById(userUUID);
        if(userFirebaseTokenMapping.isEmpty()) {
            logger.warn("Offline Notification should be sent to user with UUID " + userUUID + " but they don't have a fcm token registered");
            return;
        }
        for (UserFirebaseTokenMapping.FirebaseToken token : userFirebaseTokenMapping.get().getFcmTokens()) {
            try {
                firebaseMessaging.send(messageBuilder
                        .setToken(token.getToken())
                        .build()
                );
                logger.info("Sent offline notification to UUID: " + userUUID + " with token: " + token.getToken());
            } catch (FirebaseMessagingException e) {
                switch (e.getMessagingErrorCode()){
                    case QUOTA_EXCEEDED -> {
                        // TODO Handle too many requests, Exponential delay, see firebase docu https://firebase.google.com/docs/cloud-messaging/manage-tokens
                    }
                    case UNREGISTERED, INVALID_ARGUMENT -> {
                        var userStaleTokens = staleTokens.getOrDefault(userUUID, new ArrayList<>());
                        userStaleTokens.add(token.getToken());
                        staleTokens.put(userUUID, userStaleTokens);
                    }
                    default -> {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void createNotification(Notification notification) {
        var firebaseMessaging = FirebaseMessaging.getInstance();
        Map<UUID, List<String>> staleTokens = new HashMap<>();

        var notificationType = getNotificationTypeEnum(notification);
        Map<String, String> data = new HashMap<>();
        data.put("type", notificationType.name());

        Message.Builder messageBuilder = Message.builder();

        switch (notificationType) {
            case NEW_MESSAGE -> {
                /*messageBuilder.setNotification(notification.getNewMessage() != null ? com.google.firebase.messaging.Notification.builder()
                        .setTitle("Y-Chat - New Message")
                        .setImage("https://y-chat.net/logo192.png")
                        .setBody("You received a new message!")
                        .build() : null
                );*/
                var chatId = notification.getNewMessage().getChatId();
                data.put("chatId", chatId);
                messageBuilder.putAllData(data);
                /*messageBuilder.setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTag("YChat - New Message")
                                .setTitle("New Message")
                                .setBody("You received a new message!")
                                .setIcon("https://y-chat.net/logo192.png")
                                .setRenotify(true)
                                .build()
                        )
                        .setFcmOptions(WebpushFcmOptions.builder()
                                .setLink("https://y-chat.net/chat/"+chatId)
                                .build())
                        .build()
                );*/

                var pageSize = 10;
                var currentPageNumber = 0;
                var currentPage = socialServiceConnector.getChatMembersInternal(
                        UUID.fromString(notification.getNewMessage().getChatId()),
                        new com.openapi.gen.social.dto.Pageable().page(currentPageNumber).size(pageSize)
                );

                while(currentPageNumber == 0 || currentPage.getChatMembers().size() >= pageSize) {
                    for(ChatMemberDTO user: currentPage.getChatMembers()) {
                        if(user.getUserId().toString().equals(notification.getNewMessage().getSenderId())) continue;
                        sendInAppNotification(user.getUserId(), data);
                        sendOfflineNotificationToUser(user.getUserId(), messageBuilder, staleTokens);
                    }
                    removeStaleTokens(staleTokens);
                    staleTokens.clear();
                    currentPageNumber += 1;
                    if(currentPage.getChatMembers().size() >= pageSize) {
                        currentPage = socialServiceConnector.getChatMembersInternal(
                                UUID.fromString(notification.getNewMessage().getChatId()),
                                new com.openapi.gen.social.dto.Pageable().page(currentPageNumber).size(pageSize)
                        );
                    }
                }
            }
            case MEDIA_UPLOADED -> {
                // Will not be implemented for now
            }
            case TRANSACTION_COMPLETE -> {
                // Will not be implemented for now
            }
            case UPDATED_MESSAGE -> {
                // Will not be implemented for now
            }
            case SIGNALING_NEW_OFFER -> {
                data.put("offerSdp", notification.getSignalingNewOffer().getOffer().getSdp());
                data.put("offerType", notification.getSignalingNewOffer().getOffer().getType());
                data.put("callId", notification.getSignalingNewOffer().getCallId());
                data.put("callerId", notification.getSignalingNewOffer().getCallerId());

                sendInAppNotification(UUID.fromString(notification.getSignalingNewOffer().getCalleeId()), data);
            }
            case SIGNALING_NEW_ANSWER -> {
                data.put("answerSdp", notification.getSignalingNewAnswer().getAnswer().getSdp());
                data.put("answerType", notification.getSignalingNewAnswer().getAnswer().getType());
                data.put("callId", notification.getSignalingNewAnswer().getCallId());
                data.put("calleeId", notification.getSignalingNewAnswer().getCalleeId());

                sendInAppNotification(UUID.fromString(notification.getSignalingNewOffer().getCallerId()), data);
            }
            case SIGNALING_NEW_CANDIDATE -> {
                data.put("callId", notification.getSignalingNewCandidate().getCallId());
                data.put("candidateCandidate", notification.getSignalingNewCandidate().getCandidate().getCandidate());
                data.put("candidateSdpMid", notification.getSignalingNewCandidate().getCandidate().getSdpMid());
                data.put("candidateUsernameFragment", notification.getSignalingNewCandidate().getCandidate().getUsernameFragment());
                data.put("candidateSdpMLineIndex", ""+notification.getSignalingNewCandidate().getCandidate().getSdpMLineIndex());

                sendInAppNotification(UUID.fromString(notification.getSignalingNewCandidate().getReceiverId()), data);
            }
            case CALL_ENDED -> {
                data.put("callId", notification.getCallEnded().getCallId());

                sendInAppNotification(UUID.fromString(notification.getCallEnded().getReceiverId()), data);
            }
        }
    }

    public void cleanupStaleTokens() {
        final int pageSize = 50;
        Pageable pageable = Pageable.ofSize(pageSize).first();
        Integer lastPageSize = null;

        while(lastPageSize == null || lastPageSize == pageSize) {
            var mappings = userFirebaseTokenMappingRepository.findAll(pageable);

            Map<UUID, List<String>> staleTokens = new HashMap<>();

            var now = LocalDateTime.now();

            for (var userFirebaseTokenMapping : mappings) {
                for(var token: userFirebaseTokenMapping.getFcmTokens()) {
                    if(ChronoUnit.DAYS.between(token.getTimestamp(), now) > 30) {
                        var userStaleTokens = staleTokens.getOrDefault(userFirebaseTokenMapping.getUserId(), new ArrayList<>());
                        userStaleTokens.add(token.getToken());
                        staleTokens.put(userFirebaseTokenMapping.getUserId(), userStaleTokens);
                    }
                }
            }

            removeStaleTokens(staleTokens);
            staleTokens.clear();

            lastPageSize = mappings.getContent().size();
            pageable = pageable.next();
        }
    }

    private void removeStaleTokens(Map<UUID, List<String>> staleTokens) {
        for(UserFirebaseTokenMapping uftm : userFirebaseTokenMappingRepository.findAllById(staleTokens.keySet())) {
            var userStaleTokens = staleTokens.get(uftm.getUserId());
            uftm.setFcmTokens(uftm.getFcmTokens().stream().filter(x -> !userStaleTokens.contains(x.getToken())).collect(Collectors.toSet()));
            userFirebaseTokenMappingRepository.save(uftm);
            userStaleTokens.forEach(x -> logger.warn("Deleted fcm token: " + x +" of UUID " + uftm.getUserId() + " because it was reported as stale by firebase"));
        }
    }

    private NotificationTypeEnum getNotificationTypeEnum(Notification notification) {
        if(notification.getNewMessage() != null) {
            return NotificationTypeEnum.NEW_MESSAGE;
        } else if(notification.getMediaUploaded() != null) {
            return NotificationTypeEnum.MEDIA_UPLOADED;
        } else if(notification.getUpdatedMessage() != null) {
            return NotificationTypeEnum.UPDATED_MESSAGE;
        } else if(notification.getTransactionComplete() != null) {
            return NotificationTypeEnum.TRANSACTION_COMPLETE;
        } else if (notification.getSignalingNewOffer() != null) {
            return NotificationTypeEnum.SIGNALING_NEW_OFFER;
        } else if (notification.getSignalingNewAnswer() != null) {
            return NotificationTypeEnum.SIGNALING_NEW_ANSWER;
        } else if (notification.getSignalingNewCandidate() != null) {
            return NotificationTypeEnum.SIGNALING_NEW_CANDIDATE;
        } else if (notification.getCallEnded() != null) {
            return NotificationTypeEnum.CALL_ENDED;
        }
        throw new RuntimeException("Unknown or empty notification type");
    }
}
