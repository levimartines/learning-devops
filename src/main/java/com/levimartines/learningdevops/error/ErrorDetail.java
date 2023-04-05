package com.levimartines.learningdevops.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetail {

    private Long timestamp;
    private Integer status;
    private String message;
}
