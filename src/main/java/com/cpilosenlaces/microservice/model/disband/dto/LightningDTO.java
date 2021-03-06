package com.cpilosenlaces.microservice.model.disband.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LightningDTO {
    private int lightning;
    private int redLightning;
    private int greenLightning;
    private int blueLightning;
    private long date;
    private String disbandMac;
}
