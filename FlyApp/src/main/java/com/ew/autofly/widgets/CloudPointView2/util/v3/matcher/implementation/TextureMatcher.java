package com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.implementation;

import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.MatchHandler;
import com.ew.autofly.widgets.CloudPointView2.util.v3.matcher.primitive.FloatMatcher;
import com.ew.autofly.widgets.CloudPointView2.util.v3.model.TexturePoint;

import java.util.List;
import java.util.regex.Pattern;

public class TextureMatcher extends MatchHandler<TexturePoint> {
	
	private Pattern textureLinePattern = Pattern.compile("^vt.*$");

	@Override
	protected Pattern getPattern() {
		return this.textureLinePattern;
	}
	
	@Override
	protected void handleMatch(String group) {
		FloatMatcher floatMatcher = new FloatMatcher();
		floatMatcher.matchString(group);
		
		List<Float> coordinates = floatMatcher.getMatches();
		if(coordinates.size() < 3) {
			coordinates.add(null);
		}
		
		this.addMatch(new TexturePoint(coordinates));
	}
	
}
