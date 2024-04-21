package com.icuxika.framework.service.websocket.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMessage {

    private Long userId;

    private Integer clientType;

    private String message;
}
