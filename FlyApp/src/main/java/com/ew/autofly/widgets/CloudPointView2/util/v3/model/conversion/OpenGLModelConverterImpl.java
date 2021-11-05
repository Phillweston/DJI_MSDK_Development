package com.ew.autofly.widgets.CloudPointView2.util.v3.model.conversion;

import com.ew.autofly.widgets.CloudPointView2.util.v3.model.DirectionalVertex;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Face;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Normal;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.OpenGLModelData;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.TexturePoint;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Vertex;
import com.ew.autofly.widgets.CloudPointView2.util.v3.util.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class OpenGLModelConverterImpl implements OpenGLModelConverter {
	
	List<Face> faces;
	private List<Vertex> vertices;
	private List<Normal> normals;
	private List<TexturePoint> textureCoordinates;
	private List<Short> indices;
	
	public OpenGLModelConverterImpl() {
		this.vertices = new ArrayList<>();
		this.normals = new ArrayList<Normal>();
		this.textureCoordinates = new ArrayList<TexturePoint>();
		this.indices = new ArrayList<Short>();
	}
	
	@Override
	public OpenGLModelData convert(List<Face> faces) {
		Iterator<Face> faceIterator = faces.iterator();
		while(faceIterator.hasNext()) {
			this.handleFace(faceIterator.next());
		}
		
		return new OpenGLModelData(ListUtils.toFloat(this.vertices),
				ListUtils.toFloat(this.normals),
				ListUtils.toFloat(this.textureCoordinates),
				ListUtils.toPrimitiveShort(this.indices));
	}
	
	private void handleFace(Face face) {
		Iterator<DirectionalVertex> vertexIterator = face.getVertices().iterator();
		while(vertexIterator.hasNext()) {
			this.handleDirectionalVertex(vertexIterator.next());
		}
	}
	
	protected abstract void handleDirectionalVertex(DirectionalVertex vertex);
	
	protected void addVertex(Vertex vertex) {
		this.vertices.add(vertex);
	}
	
	protected void addNormal(Normal normal) {
		this.normals.add(normal);
	}
	
	protected void addTextureCoordinates(TexturePoint textureCoordinates) {
		this.textureCoordinates.add(textureCoordinates);
	}
	
	protected void addIndex(Short index) {
		this.indices.add(index);
	}

}
