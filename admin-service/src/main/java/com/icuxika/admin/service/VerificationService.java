package com.icuxika.admin.service;

import com.icuxika.admin.vo.VerificationImageInfo;

public interface VerificationService {
    VerificationImageInfo refresh();

    boolean check(Double x, Double y, String token);
}
