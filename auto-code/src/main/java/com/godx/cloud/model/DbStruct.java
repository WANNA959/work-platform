package com.godx.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbStruct {

    private String name;
    private String comment;

    private List<TableInfo> tableInfos;
}
