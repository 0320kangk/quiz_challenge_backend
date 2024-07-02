package project.domain.chatGpt.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizTitle {
    SPRINGFRAMEWORK("Spring framework"),
    JAVA("Java");

    private final String value;

}