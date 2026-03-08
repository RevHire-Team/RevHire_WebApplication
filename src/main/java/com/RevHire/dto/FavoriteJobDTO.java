package com.RevHire.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FavoriteJobDTO {

    private Long favId;
    private Long jobId;
    private String title;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String jobType;
    private String status;
    private String companyName;

    public FavoriteJobDTO(long favId, long jobId, String javaDeveloper, String abcCompany, BigDecimal salaryMin, BigDecimal salaryMax, String bangalore, String fullTime) {
    }
}