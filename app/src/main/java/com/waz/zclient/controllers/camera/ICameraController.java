/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.controllers.camera;

import com.waz.annotations.Controller;
import com.waz.api.ImageAsset;
import com.waz.zclient.pages.main.profile.camera.CameraContext;
import com.waz.zclient.pages.main.profile.camera.CameraType;

@Controller
public interface ICameraController {
    CameraContext getCameraContext();

    void addCameraActionObserver(CameraActionObserver cameraActionObserver);

    void removeCameraActionObserver(CameraActionObserver cameraActionObserver);

    void openCamera(CameraContext cameraContext);

    void closeCamera(CameraContext cameraContext);

    void onBitmapSelected(ImageAsset imageAsset, boolean imageFromCamera, CameraContext cameraContext);

    void onDeleteImage(CameraContext cameraContext);

    void onCameraTypeChanged(CameraType cameraType, CameraContext cameraContext);

    void onCameraNotAvailable(CameraContext cameraContext);

    void tearDown();
}
