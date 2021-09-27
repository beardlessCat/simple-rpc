package com.rpc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserDto implements Serializable {
    private String name ;
    private int age ;
    private String sex ;
    private String address ;
}
