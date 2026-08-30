package com.mariuszilinskas.streamix.auth.identity.client;

import com.mariuszilinskas.streamix.auth.identity.dto.AuthDetails;
import com.mariuszilinskas.streamix.auth.identity.dto.TokenIssueResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("auth-session")
public interface SessionFeignClient {

    @PostMapping(value = "/token/issue", consumes = "application/json")
    TokenIssueResponse issueTokens(@RequestBody AuthDetails authDetails);

}
