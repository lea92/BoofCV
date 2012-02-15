/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.distort;

import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.core.image.border.ImageBorder;
import boofcv.core.image.border.ImageBorder_F32;
import boofcv.struct.distort.ImageDistort;
import boofcv.struct.distort.PixelTransform_F32;
import boofcv.struct.image.ImageFloat32;
import georegression.struct.point.Point2D_F32;

/**
 * @author Peter Abeles
 */
public class ImageDistortMap implements ImageDistort<ImageFloat32> {

	int width,height;
	Point2D_F32 map[];
	// sub pixel interpolation
	private InterpolatePixel<ImageFloat32> interp;
	// handle the image border
	private ImageBorder_F32 border;

	// crop boundary
	int x0,y0,x1,y1;

	public ImageDistortMap(int width, int height, 
						   InterpolatePixel<ImageFloat32> interp , 
						   ImageBorder<ImageFloat32> border) {
		this.width = width;
		this.height = height;

		this.interp = interp;
		this.border = (ImageBorder_F32)border;

		map = new Point2D_F32[width*height];
		for( int i = 0; i < map.length; i++ ) {
			map[i] = new Point2D_F32();
		}
	}

	@Override
	public void setModel(PixelTransform_F32 dstToSrc) {
		int index = 0;
		for( int y = 0; y < height; y++ ) {
			for( int x = 0; x < width; x++ ) {
				dstToSrc.compute(x,y);
				map[index++].set(dstToSrc.distX,dstToSrc.distY);
			}
		}
	}

	@Override
	public void apply(ImageFloat32 srcImg, ImageFloat32 dstImg) {
		if( dstImg.width != width || dstImg.height != height )
			throw new IllegalArgumentException("Unexpected dstImg dimension");

		interp.setImage(srcImg);

		x0 = 0;y0 = 0;x1 = dstImg.width;y1 = dstImg.height;

		if( border != null )
			applyBorder(srcImg, dstImg);
		else
			applyNoBorder(srcImg, dstImg);
	}

	@Override
	public void apply(ImageFloat32 srcImg, ImageFloat32 dstImg, int dstX0, int dstY0, int dstX1, int dstY1) {
		if( dstImg.width != width || dstImg.height != height )
			throw new IllegalArgumentException("Unexpected dstImg dimension");

		interp.setImage(srcImg);

		x0 = dstX0;y0 = dstY0;x1 = dstX1;y1 = dstY1;

		if( border != null )
			applyBorder(srcImg, dstImg);
		else
			applyNoBorder(srcImg, dstImg);
	}

	public void applyBorder( ImageFloat32 srcImg , ImageFloat32 dstImg ) {

		border.setImage(srcImg);

		final float minInterpX = interp.getUnsafeBorderX();
		final float minInterpY = interp.getUnsafeBorderY();
		final float maxInterpX = srcImg.getWidth()-interp.getUnsafeBorderX();
		final float maxInterpY = srcImg.getHeight()-interp.getUnsafeBorderY();

		final float widthF = srcImg.getWidth();
		final float heightF = srcImg.getHeight();

		for( int y = y0; y < y1; y++ ) {
			int indexDst = dstImg.startIndex + dstImg.stride*y + x0;
			for( int x = x0; x < x1; x++ , indexDst++ ) {
				Point2D_F32 s = map[indexDst];

				if( s.x < minInterpX || s.x >= maxInterpX || s.y < minInterpY || s.y >= maxInterpY ) {
					if( s.x >= 0f && s.x < widthF && s.y >= 0f && s.y < heightF )
						dstImg.data[indexDst] = interp.get(s.x,s.y);
					else
						dstImg.data[indexDst] = border.getOutside((int)s.x,(int)s.y);
				} else {
					dstImg.data[indexDst] = interp.get(s.x,s.y);
				}
			}
		}
	}

	public void applyNoBorder( ImageFloat32 srcImg , ImageFloat32 dstImg ) {

		final float minInterpX = interp.getUnsafeBorderX();
		final float minInterpY = interp.getUnsafeBorderY();
		final float maxInterpX = srcImg.getWidth()-interp.getUnsafeBorderX();
		final float maxInterpY = srcImg.getHeight()-interp.getUnsafeBorderY();

		final float widthF = srcImg.getWidth();
		final float heightF = srcImg.getHeight();

		for( int y = y0; y < y1; y++ ) {
			int indexDst = dstImg.startIndex + dstImg.stride*y + x0;
			for( int x = x0; x < x1; x++ , indexDst++ ) {
				Point2D_F32 s = map[indexDst];

				if( s.x < minInterpX || s.x >= maxInterpX || s.y < minInterpY || s.y >= maxInterpY ) {
					if( s.x >= 0f && s.x < widthF && s.y >= 0f && s.y < heightF )
						dstImg.data[indexDst] = interp.get(s.x,s.y);
				} else {
					dstImg.data[indexDst] = interp.get_unsafe(s.x, s.y);
				}
			}
		}
	}
}
