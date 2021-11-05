package com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation;

import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.MatchHandler;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.primitive.FloatMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.Vertex;

import java.util.List;
import java.util.regex.Pattern;

public class VertexMatcher extends MatchHandler<Vertex> {
	
	private Pattern vertexLinePattern = Pattern.compile("^v .*$");

	@Override
	protected Pattern getPattern() {
		return this.vertexLinePattern;
	}
	
	
	@Override
	protected void handleMatch(String group) {
		FloatMatcher floatMatcher = new FloatMatcher();
		floatMatcher.matchString(group);
		List<Float> xyzwCoordinates = floatMatcher.getMatches();

		if(xyzwCoordinates.size() < 4) {
			xyzwCoordinates.add(null);
		}
		
		this.addMatch(new Vertex(
				xyzwCoordinates.get(0),
				xyzwCoordinates.get(1),
				xyzwCoordinates.get(2),
				xyzwCoordinates.get(3)));
	}
	
}
