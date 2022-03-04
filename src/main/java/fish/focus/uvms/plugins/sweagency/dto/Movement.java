/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package fish.focus.uvms.plugins.sweagency.dto;

import java.time.Instant;

public class Movement {
    
    private String ircs;
    private Double longitude;
    private Double latitude;
    private Instant reportDate;
    private Double measuredSpeed;
    private Double calculatedSpeed;
    private String status;
    private Double course;
    private Integer oceanRegion;

    public String getIrcs() {
        return ircs;
    }
    public void setIrcs(String ircs) {
        this.ircs = ircs;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Instant getReportDate() {
        return reportDate;
    }
    public void setReportDate(Instant reportDate) {
        this.reportDate = reportDate;
    }
    public Double getMeasuredSpeed() {
        return measuredSpeed;
    }
    public void setMeasuredSpeed(Double measuredSpeed) {
        this.measuredSpeed = measuredSpeed;
    }
    public Double getCalculatedSpeed() {
        return calculatedSpeed;
    }
    public void setCalculatedSpeed(Double calculatedSpeed) {
        this.calculatedSpeed = calculatedSpeed;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Double getCourse() {
        return course;
    }
    public void setCourse(Double course) {
        this.course = course;
    }
    public Integer getOceanRegion() {
        return oceanRegion;
    }
    public void setOceanRegion(Integer oceanRegion) {
        this.oceanRegion = oceanRegion;
    }
}
