package com.ew.autofly.widgets.CloudPointView2.util.v3.model.conversion;

import com.ew.autofly.widgets.CloudPointView2.util.v3.model.DirectionalVertex;

public class DrawArraysConverter extends OpenGLModelConverterImpl {

	
	@Override
	protected void handleDirectionalVertex(DirectionalVertex vertex) {
		this.addVertex(vertex.getVertex());
		this.addNormal(vertex.getNormal());
		this.addTextureCoordinates(vertex.getTexturePoint());
	}
	
}
