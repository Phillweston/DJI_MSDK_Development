package com.ew.autofly.widgets.CloudPointView2.util.v3;

import com.ew.autofly.widgets.CloudPointView2.util.v3.model.OpenGLModelData;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.RawOpenGLModel;

import java.io.FileNotFoundException;

public class Execute_obj2openjlv3 {
	
	public static void main(String[] args) {
		OpenGLModelData openGLModelData;
		try {
			RawOpenGLModel openGLModel = new Obj2OpenJL().convert("src/org/obj2openjl/boxes");






			openGLModelData = openGLModel.normalize().center().getDataForGLDrawElements();
			
			printArray(openGLModelData.getVertices(), "Vertices");
			printArray(openGLModelData.getNormals(), "Normals");
			printArray(openGLModelData.getTextureCoordinates(), "Texture coordinates");
			printArray(openGLModelData.getIndices(), "Indices");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void printArray(float[] array, String label) {
		if(array == null) return;
		System.out.println(label + ": " + array.length);
		int c = 0;
		for(int i = 0; i < array.length; i++) {
			if(c != 0 && c % 10 == 0) {
				System.out.println();
			}
			System.out.print(array[i] + "f");
			if(i < array.length - 1) {
				System.out.print(", ");
			}
			c++;
		}
		System.out.println();
		System.out.println();
	}
	
	public static void printArray(int[] array, String label) {
		if(array == null) return;
		System.out.println(label + ": " + array.length);
		int c = 0;
		for(int i = 0; i < array.length; i++) {
			if(c != 0 && c % 3 == 0) {
				System.out.println();
			}
			System.out.print(array[i]);
			if(i < array.length - 1) {
				System.out.print(", ");
			}
			c++;
		}
		System.out.println();
		System.out.println();
	}
	
	public static void printArray(short[] array, String label) {
		if(array == null) return;
		System.out.println(label + ": " + array.length);
		int c = 0;
		for(int i = 0; i < array.length; i++) {
			if(c != 0 && c % 3 == 0) {
				System.out.println();
			}
			System.out.print(array[i]);
			if(i < array.length - 1) {
				System.out.print(", ");
			}
			c++;
		}
		System.out.println();
		System.out.println();
	}

}
