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

package gecv.alg.filter.convolve.impl;

import gecv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author Peter Abeles
 */
public class GenerateConvolveBox {

	PrintStream out;

	String className;
	String typeInput;
	String typeOutput;
	String dataInput;
	String dataOutput;
	String bitWise;
	String sumType;

	public void createAll() throws FileNotFoundException {
		createI8_I16();
		createI8_I32();
		createF32_F32();
	}

	public void createI8_I16() throws FileNotFoundException {
		className = "ConvolveBox_I8_I16";
		typeInput = "ImageInt8";
		typeOutput = "ImageInt16";
		dataInput = "byte";
		dataOutput = "short";
		bitWise = "& 0xFF";
		sumType = "int";

		createFile();
	}

	public void createI8_I32() throws FileNotFoundException {
		className = "ConvolveBox_I8_I32";
		typeInput = "ImageInt8";
		typeOutput = "ImageInt32";
		dataInput = "byte";
		dataOutput = "int";
		bitWise = "& 0xFF";
		sumType = "int";

		createFile();
	}

	public void createF32_F32() throws FileNotFoundException {
		className = "ConvolveBox_F32_F32";
		typeInput = "ImageFloat32";
		typeOutput = "ImageFloat32";
		dataInput = "float";
		dataOutput = "float";
		bitWise = "";
		sumType = "float";

		createFile();
	}

	public void createFile() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));

		printPreamble();
		printHorizontal();
		printVertical();

		out.println("}");
	}

	public void printPreamble() {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.filter.convolve.impl;\n" +
				"\n" +
				"import gecv.struct.image." + typeInput + ";\n" +
				"import gecv.struct.image." + typeOutput + ";\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Convolves a box filter across an image.  A box filter is equivalent to convolving a kernel with all 1's.\n" +
				" *\n" +
				" * <p>\n" +
				" * Do not modify.  Auto generated by GenerateConvolveBox.\n" +
				" * </p>\n" +
				" * \n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class " + className + " {\n");
	}

	public void printHorizontal() {

		String typeCast = dataOutput.compareTo(sumType) != 0 ? "(" + dataOutput + ")" : "";

		out.print("\tpublic static void horizontal( " + typeInput + " input , " + typeOutput + " output , int radius , boolean includeBorder) {\n" +
				"\t\tfinal int kernelWidth = radius*2 + 1;\n" +
				"\n" +
				"\t\tfinal int startY = includeBorder ? 0 : radius;\n" +
				"\t\tfinal int endY = includeBorder ? input.height : input.height - radius;\n" +
				"\n" +
				"\t\tfor( int y = startY; y < endY; y++ ) {\n" +
				"\t\t\tint indexIn = input.startIndex + input.stride*y;\n" +
				"\t\t\tint indexOut = output.startIndex + output.stride*y + radius;\n" +
				"\n" +
				"\t\t\t" + sumType + " total = 0;\n" +
				"\n" +
				"\t\t\tint indexEnd = indexIn + kernelWidth;\n" +
				"\t\t\t\n" +
				"\t\t\tfor( ; indexIn < indexEnd; indexIn++ ) {\n" +
				"\t\t\t\ttotal += input.data[indexIn] " + bitWise + ";\n" +
				"\t\t\t}\n" +
				"\t\t\toutput.data[indexOut++] = " + typeCast + "total;\n" +
				"\n" +
				"\t\t\tindexEnd = indexIn + input.width - kernelWidth;\n" +
				"\t\t\tfor( ; indexIn < indexEnd; indexIn++ ) {\n" +
				"\t\t\t\ttotal -= input.data[ indexIn - kernelWidth ] " + bitWise + ";\n" +
				"\t\t\t\ttotal += input.data[ indexIn ] " + bitWise + ";\n" +
				"\n" +
				"\t\t\t\toutput.data[indexOut++] = " + typeCast + "total;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n");
	}

	public void printVertical() {

		String typeCast = dataOutput.compareTo(sumType) != 0 ? "(" + dataOutput + ")" : "";

		out.print("\tpublic static void vertical( " + typeInput + " input , " + typeOutput + " output , int radius , boolean includeBorder ) {\n" +
				"\t\tfinal int kernelWidth = radius*2 + 1;\n" +
				"\n" +
				"\t\tfinal int startX = includeBorder ? 0 : radius;\n" +
				"\t\tfinal int endX = includeBorder ? input.width : input.width - radius;\n" +
				"\n" +
				"\t\tfinal int backStep = kernelWidth*input.stride;\n" +
				"\n" +
				"\t\tfor( int x = startX; x < endX; x++ ) {\n" +
				"\t\t\tint indexIn = input.startIndex + x;\n" +
				"\t\t\tint indexOut = output.startIndex + output.stride*radius + x;\n" +
				"\n" +
				"\t\t\t" + sumType + " total = 0;\n" +
				"\t\t\tint indexEnd = indexIn + input.stride*kernelWidth;\n" +
				"\t\t\tfor( ; indexIn < indexEnd; indexIn += input.stride) {\n" +
				"\t\t\t\ttotal += input.data[indexIn] " + bitWise + ";\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\toutput.data[indexOut] = " + typeCast + "total;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t// change the order it is processed in to reduce cache misses\n" +
				"\t\tfor( int y = radius+1; y < output.height-radius; y++ ) {\n" +
				"\t\t\tint indexIn = input.startIndex + (y+radius)*input.stride+startX;\n" +
				"\t\t\tint indexOut = output.startIndex + y*output.stride+startX;\n" +
				"\n" +
				"\t\t\tfor( int x = startX; x < endX; x++ ,indexIn++,indexOut++) {\n" +
				"\t\t\t\t" + sumType + " total = output.data[ indexOut - output.stride]  - (input.data[ indexIn - backStep ]" + bitWise + ");\n" +
				"\t\t\t\ttotal += input.data[ indexIn ]" + bitWise + ";\n" +
				"\n" +
				"\t\t\t\toutput.data[indexOut] = " + typeCast + "total;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n");
	}

	public static void main(String args[]) throws FileNotFoundException {
		GenerateConvolveBox generator = new GenerateConvolveBox();

		generator.createAll();
	}
}
