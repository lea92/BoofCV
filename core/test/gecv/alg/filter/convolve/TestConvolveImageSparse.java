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

import gecv.alg.filter.convolve.impl.ConvolveImageStandardSparse;
import gecv.core.image.GeneralizedImageOps;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.image.ImageBase;
import gecv.testing.CompareIdenticalFunctions;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public class TestConvolveImageSparse {
	Random rand = new Random(0xFF);

	int width = 10;
	int height = 15;
	int kernelRadius = 1;

	int testX = 5;
	int testY = 6;

	@Test
	public void compareToStandard() {
		CompareToStandard a = new CompareToStandard();
		a.performTests(5);
	}

	public class CompareToStandard extends CompareIdenticalFunctions
	{
		protected CompareToStandard() {
			super(ConvolveImageSparse.class, ConvolveImageStandardSparse.class);
		}

		@Override
		protected void compareResults(Object targetResult, Object[] targetParam, Object validationResult, Object[] validationParam) {
			Number a = (Number)targetResult;
			Number b = (Number)validationResult;

			assertEquals(a.doubleValue(),b.doubleValue(),1e-4);
		}

		@Override
		protected Object[][] createInputParam(Method m) {
			Class<?> paramTypes[] = m.getParameterTypes();

			Object storage;
			Object kernel;
			if (Kernel1D_F32.class == paramTypes[0]) {
				kernel = KernelFactory.random1D_F32(kernelRadius, -1, 1, rand);
				storage = new float[ kernelRadius*2+1];
			} else if (Kernel1D_I32.class == paramTypes[0]) {
				kernel = KernelFactory.random1D_I32(kernelRadius, 0, 5, rand);
				storage = new int[ kernelRadius*2+1];
			} else {
				throw new RuntimeException("Unknown kernel type");
			}

			ImageBase src = ConvolutionTestHelper.createImage(paramTypes[2], width, height);
			GeneralizedImageOps.randomize(src, 0, 5, rand);


			Object[][] ret = new Object[1][paramTypes.length];

			ret[0][0] = kernel;
			ret[0][1] = kernel;
			ret[0][2] = src;
			ret[0][3] = testX;
			ret[0][4] = testY;
			ret[0][5] = storage;
			if( paramTypes.length > 6 ) {
				ret[0][6] = 1;
				ret[0][7] = 2;
			}

			return ret;
		}
	}
}
