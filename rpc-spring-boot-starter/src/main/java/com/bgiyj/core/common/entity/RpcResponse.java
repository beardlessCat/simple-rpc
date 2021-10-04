package com.bgiyj.core.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcResponse {
    private String requestId;
    private int code;
    private String errorMsg;
    private Object result;
}
