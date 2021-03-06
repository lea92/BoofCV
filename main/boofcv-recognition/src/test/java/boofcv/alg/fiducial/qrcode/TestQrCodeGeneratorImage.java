/*
 * Copyright (c) 2011-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.fiducial.qrcode;

import boofcv.gui.image.ShowImages;
import boofcv.misc.BoofMiscOps;
import org.junit.Test;

/**
 * @author Peter Abeles
 */
public class TestQrCodeGeneratorImage {
	boolean showImage = false; // use a boolean to make it easier to turn on and off. Don't need to import

	@Test
	public void showImage() {
		if(!showImage)
			return;

		QrCode qr = new QrCode();
		qr.version = 40;

		QrCodeGeneratorImage generator = new QrCodeGeneratorImage(4);

		generator.generate(qr);

		ShowImages.showWindow(generator.gray,"QR Code");
		BoofMiscOps.sleep(10000);
	}
}