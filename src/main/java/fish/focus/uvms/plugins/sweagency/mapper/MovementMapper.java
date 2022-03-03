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
package fish.focus.uvms.plugins.sweagency.mapper;

import fish.focus.schema.exchange.movement.v1.MovementType;
import fish.focus.uvms.plugins.sweagency.dto.Movement;

public class MovementMapper {
    private MovementMapper() {}
    
    public static Movement mapToMovement(MovementType movementType) {
        Movement movement = new Movement();
        movement.setIrcs(movementType.getIrcs());
        movement.setLongitude(movementType.getPosition().getLongitude());
        movement.setLatitude(movementType.getPosition().getLatitude());
        movement.setReportDate(movementType.getPositionTime().toInstant());
        movement.setMeasuredSpeed(movementType.getReportedSpeed());
        movement.setCalculatedSpeed(movementType.getCalculatedSpeed());
        movement.setCourse(movementType.getReportedCourse());
        movement.setStatus(movementType.getStatus());
        movement.setOceanRegion(movementType.getSourceSatelliteId());
        return movement;
    }
}
