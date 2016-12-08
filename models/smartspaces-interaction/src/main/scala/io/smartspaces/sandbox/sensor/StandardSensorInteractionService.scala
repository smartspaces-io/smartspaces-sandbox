package io.smartspaces.sandbox.sensor

import io.smartspaces.service.BaseSupportedService
import io.smartspaces.sandbox.sensor.integrator.SensorIntegrator

class StandardSensorInteractionService extends BaseSupportedService with SensorInteractionService {

  override def getName(): String = {
    SensorInteractionService.SERVICE_NAME
  }
  
  override def newSensorIntegrator(): SensorIntegrator = {
    null
  }
}