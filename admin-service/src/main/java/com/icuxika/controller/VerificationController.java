package com.icuxika.controller;

import com.icuxika.annotations.Anonymous;
import com.icuxika.common.ApiData;
import com.icuxika.service.VerificationService;
import com.icuxika.vo.VerificationImageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @Anonymous
    @GetMapping("/refresh")
    public ApiData<VerificationImageInfo> refresh() {
        VerificationImageInfo verificationImageInfo = verificationService.refresh();
        return ApiData.ok(verificationImageInfo);
    }

    @Anonymous
    @GetMapping("/check")
    public ApiData<Boolean> check(Double x, Double y, String token) {
        boolean result = verificationService.check(x, y, token);
        System.out.println(x + " " + y + " " + token);
        return ApiData.ok(result);
    }

}
