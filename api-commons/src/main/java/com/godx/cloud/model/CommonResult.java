package com.godx.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult <T>{

    private Integer code;
    private String message;
//    private Map<String,Object> data=new HashMap<>();
    private Object data;

    public CommonResult(Integer code,String message){
        this(code,message,null);
    }
}
