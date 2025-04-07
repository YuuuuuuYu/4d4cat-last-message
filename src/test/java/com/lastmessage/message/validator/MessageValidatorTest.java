package com.lastmessage.message.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MessageValidatorTest {

    private final MessageValidator validator = new MessageValidator();

    @Nested
    @DisplayName("기본 유효성 체크")
    class IsValidTests {

        @Test
        @DisplayName("Null인 경우 False 반환")
        void isValid_NullContent_ReturnsFalse() {
            // When
            boolean result = validator.isValid(null);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("빈 문자열인 경우 False 반환")
        void isValid_EmptyContent_ReturnsFalse() {
            // When
            boolean result = validator.isValid("");

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("공백 문자열인 경우 False 반환")
        void isValid_BlankContent_ReturnsFalse() {
            // When
            boolean result = validator.isValid("   ");

            // Then
            assertFalse(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Hello",
                "Hello123",
                "안녕하세요",
                "Hello 안녕 123",
                "ㄱㄴㄷㅏㅑㅓ가나다",
                "    spaces    "
        })
        @DisplayName("유효한 문자열인 경우 True 반환")
        void isValid_ValidContent_ReturnsTrue(String content) {
            // When
            boolean result = validator.isValid(content);

            // Then
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Invalid@Content",
                "Special$Characters",
                "Not!Allowed",
                "안녕하세요!",
                "Contains&Symbols",
                "12345+67890"
        })
        @DisplayName("유효하지 않은 문자열인 경우 False 반환")
        void isValid_InvalidCharacters_ReturnsFalse(String content) {
            // When
            boolean result = validator.isValid(content);

            // Then
            assertFalse(result);
        }

        @Test
        @DisplayName("글자 수 제한 체크")
        void isValid_ContentExceedsMaxLength_ReturnsFalse() {
            // Given
            // 영어로 31자
            String longEnglishContent = "abcdefghijklmnopqrstuvwxyz12345";

            // 한글 16글자
            String longKoreanContent = "가나다라마바사아자차카타파하갸냐";

            // 혼합 30자 초과
            String mixedLongContent = "안녕하세요 Hello 12345 안녕 반가워요";

            // When
            boolean resultEnglish = validator.isValid(longEnglishContent);
            boolean resultKorean = validator.isValid(longKoreanContent);
            boolean resultMixed = validator.isValid(mixedLongContent);

            // Then
            assertFalse(resultEnglish);
            assertFalse(resultKorean);
            assertFalse(resultMixed);
        }
    }


    @Nested
    @DisplayName("calculateCharCount 메서드 테스트")
    class CharCountTests {

        @Test
        @DisplayName("영어는 글자 수가 1로 계산됨")
        void calculateCharCount_EnglishOnly_CountsAsOne() {
            // Given
            String content = "abcde";

            // When
            int count = validator.calculateCharCount(content);

            // Then
            assertEquals(5, count);
        }

        @Test
        @DisplayName("한글은 글자 수가 2로 계산됨")
        void calculateCharCount_KoreanOnly_CountsAsTwo() {
            // Given
            String content = "가나다";

            // When
            int count = validator.calculateCharCount(content);

            // Then
            assertEquals(6, count);
        }

        @Test
        @DisplayName("한글, 영어 혼합 계산")
        void calculateCharCount_MixedContent_CountsCorrectly() {
            // Given
            String content = "안녕 Hello";  // 안녕(4) + space(1) + Hello(5) = 10

            // When
            int count = validator.calculateCharCount(content);

            // Then
            assertEquals(10, count);
        }
    }
}