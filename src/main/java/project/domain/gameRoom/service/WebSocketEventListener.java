package project.domain.gameRoom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import project.domain.gameRoom.model.domain.GameRoom;
import project.domain.gameRoom.model.dto.GameRoomHostNameDto;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final GameRoomService gameRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Authentication authentication = (Authentication) headerAccessor.getUser();
        UserDetails userDetails;
        if (authentication != null) {
            userDetails = (UserDetails) authentication.getPrincipal();
            log.info("authentication userDetails: {}", userDetails.getUsername());
        } else {
            throw new NoSuchElementException("user 정보가 없습니다.");
        }

        GenericMessage generic = (GenericMessage) headerAccessor.getHeader("simpConnectMessage");
        Map<String, List<String>> nativeHeaders = (Map) generic.getHeaders().get("nativeHeaders");
        log.info("nativeHeaders data {}", nativeHeaders);
        String roomId;
        if(!nativeHeaders.get("roomId").isEmpty()){
            roomId = nativeHeaders.get("roomId").get(0);
        } else {
            throw new NoSuchElementException("방 번호를 찾지 못 했습니다.");
        }
        gameRoomService.enterGameRoom(roomId, userDetails.getUsername());
    }
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String id = event.getUser().getName();
        log.info("userEmail {}", id);
        String roomId = gameRoomService.getIdToRoomId(id);
        GameRoom gameRoom = gameRoomService.leaveGameRoom(roomId, id);
        GameRoomHostNameDto gameRoomHostNameDto = new GameRoomHostNameDto(gameRoom.getHostName());
        log.info("[Disconnected] websocket host name : {}", gameRoomHostNameDto.getHostName());
        messagingTemplate.convertAndSend("/subscribe/notification/room/"+ roomId ,gameRoomHostNameDto.getHostName()); //방장이 누군지 알려야 함
    }
}
