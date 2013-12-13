package riskarena.riskbots.evaluation;

import riskarena.GameInfo;

public abstract class AbstractEvaluator {
	private double weight;
	private String name;
	protected GameStats stats;
	protected GameInfo game;
	
	public AbstractEvaluator(String _name, double _weight, GameStats _stats, GameInfo _game) {
		name = _name;
		weight = _weight;
		stats = _stats;
		game = _game;
	}
	
	abstract public double getScore();
	
	abstract public void refresh();
	
	final public double getWeight() {
		return weight;
	}
	
	public String getName() {
		return name;
	}
}
