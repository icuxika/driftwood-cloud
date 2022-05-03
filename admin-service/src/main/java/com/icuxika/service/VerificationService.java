package com.icuxika.service;

import com.icuxika.vo.VerificationImageInfo;

public interface VerificationService {
    VerificationImageInfo refresh();

    boolean check(Double x, Double y, String token);
}
