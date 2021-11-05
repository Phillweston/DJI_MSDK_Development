package com.ew.autofly.widgets.CloudPointView2.util.v3.matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MatchHandler<T> {
	
	private List<T> matches = new ArrayList<T>();
	
	public void matchString(String group) {
		Matcher matcher = this.getPattern().matcher(group);
		while(matcher.find()) {
			this.handleMatch(matcher.group());
		}
	}
	
	protected abstract Pattern getPattern();
	protected abstract void handleMatch(String group);
	
	protected void addMatch(T match) {
		this.matches.add(match);
	}
	
	protected void addMatches(Collection<? extends T> matches) {
		this.matches.addAll(matches);
	}
	
	public List<T> getMatches() {
		return this.matches;
	}

}
