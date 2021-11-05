package com.ew.autofly.widgets.CloudPointView2.util.v3.model.transform.array;

import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Vertex;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.transform.Transformation;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.transform.matrix.TransformationMatrix;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.transform.matrix.TranslationMatrix;

import java.util.ArrayList;
import java.util.List;

public class TranslationTransformation extends ArrayTransformation {
	
	private float distanceX;
	private float distanceY;
	private float distanceZ;
	
	public TranslationTransformation(int numberOfInstances, float distanceX, float distanceY, float distanceZ) {
		super(numberOfInstances > 0 ? numberOfInstances - 1 : 0);
		this.distanceX = distanceX;
		this.distanceY = distanceY;
		this.distanceZ = distanceZ;
	}

	@Override
	public List<Vertex> transformVertices(List<Vertex> vertices) {
		if(this.getNumberOfInstances() == 0) return new ArrayList<Vertex>();
		
		float stepX = this.distanceX / (this.getNumberOfInstances());
		float stepY = this.distanceY / (this.getNumberOfInstances());
		float stepZ = this.distanceZ / (this.getNumberOfInstances());
		

		List<Vertex> result = new ArrayList<Vertex>();
		for(int i = 1; i <= this.getNumberOfInstances(); i++) {
				TransformationMatrix translationMatrix = new TranslationMatrix(i*stepX, i*stepY, i*stepZ);
				Transformation translation = new Transformation(translationMatrix);
				
				result.addAll(translation.copyAndApplyToVertices(vertices));
		}
		return result;
	}

}
