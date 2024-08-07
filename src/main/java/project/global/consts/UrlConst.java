package project.global.consts;

public class UrlConst  {
    public static final String[] publicAPI = {
            "/api/join",
            "/api/auth/login",
            "/api/chatGpt/completion",
            "/api/chatGpt/chat/completion",
            "/api/chatGpt/chat/completion/content",
            "/error",
//            "/api/gameRoom/create",
            "/api/gameRoom/enter/{roomId}",
            "/api/gameRoom/all",
            "/api/chat/**",
            "/quiz/**",
            "/api/characterImg/all",
            "/api/characterImg/{characterImg}",
            "/api/quizTheme/all"

    };
}
