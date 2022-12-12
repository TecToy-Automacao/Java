import express from 'express'
import { CameraController } from '../controllers/camera.controller'

export const routes = express.Router()

const cameraController = new CameraController()

routes.post('/url', cameraController.getLink)
