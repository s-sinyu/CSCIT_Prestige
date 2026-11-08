package com.prestige.renovation.dto.designer;

import java.math.BigDecimal;

import com.prestige.renovation.dto.company.CompanyResponse;

/** Public information about a designer shown on IDP detail pages (FR-CUS-3). */
public record DesignerInfo(Integer id, String fullName, String displayName, DesignerProfileResponse profile,
                           CompanyResponse company, BigDecimal averageRating, long ratingCount) {
}
