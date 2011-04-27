/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.convolve;

import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.image.ImageBase;
import gecv.struct.image.ImageFloat32;

/**
 * @author Peter Abeles
 */
public class ConvolutionTestHelper {
	/**
	 * Find the type of kernel based on the input image type.
	 */
	public static Class<?> kernelTypeByInputType( Class<?> imageType ) {
		if( imageType == ImageFloat32.class ) {
			return Kernel1D_F32.class;
		} else {
			return Kernel1D_I32.class;
		}
	}

	/**
	 * Creates an image of the specified type
	 */
	public static ImageBase createImage(Class<?> imageType, int width, int height) {
		try {
			ImageBase img = (ImageBase) imageType.newInstance();
			return img._createNew(width, height);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Searches for images and creates copies.  The same instance of all other variables is returned
	 */
	public static Object[] copyImgs(Object... input ) {
		Object[] output = new Object[input.length];
		for (int i = 0; i < input.length; i++) {
			Object o = input[i];
			if (o instanceof ImageBase) {
				ImageBase b = (ImageBase)o;
				ImageBase img = b._createNew(b.width, b.height);
				img.setTo((ImageBase) o);
				output[i] = img;
			} else {
				output[i] = o;
			}
		}

		return output;
	}
}
