package com.mariuszilinskas.streamix.auth.identity.dto;

public record TokenIssueResponse(String accessToken, String refreshToken) {
}
