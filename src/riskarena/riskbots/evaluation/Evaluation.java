package riskarena.riskbots.evaluation;

import java.util.ArrayList;

import riskarena.GameInfo;
import riskarena.OutputFormat;
import riskarena.Risk;

public class Evaluation {
	private GameInfo game;
	private GameStats stats;
	
	// Internal list of evaluators. A game stat's score is a weighted combination of these.
	private ArrayList<AbstractEvaluator> evaluators;
	private final String FULL_DEBUG = "ALL";
	
	public Evaluation(GameInfo gi) {
		game = gi;
		stats = new GameStats(game);
		evaluators = new ArrayList<AbstractEvaluator>();
		registerEvaluators();
	}
	
	private void registerEvaluators() {
		evaluators.clear();
		evaluators.add( new OwnContinentsEvaluator("OwnContinents", 3.0, stats) );
	}
	
	/*
	 * Returns the score of a board state
	 */
	public double score() {
		return score(false, null);
	}
	
	public double debugScore() {
		return score(true, FULL_DEBUG);
	}
	
	public double debugScore(String nameOfEvalToDebug) {
		return score(true, nameOfEvalToDebug);
	}
	
	/*
	 * Calculate the weighted sum of scores returned by evaluators.
	 * If debug is true, intermediate calculations are also printed using Risk's static output methods.
	 * Only the final score and the score of an evaluator with the internal name nameOfEvalToDebug
	 * is printed, unless nameOfEvalToDebug has the value FULL_DEBUG, in which case they're all printed.
	 */
	private double score(boolean debug, String nameOfEvalToDebug) {
		if(debug && nameOfEvalToDebug == FULL_DEBUG)
			Risk.sayOutput("Scoring state for " + game.getMyName(), OutputFormat.BLUE, true);
		double result = 0.0;
		for(AbstractEvaluator e : evaluators) {
			double evalScore = e.getScore();
			if(debug) {
				if(nameOfEvalToDebug == FULL_DEBUG)
					Risk.sayOutput(e.getName() + ": " + evalScore, OutputFormat.TABBED, true);
				else if(nameOfEvalToDebug == e.getName())
					Risk.sayOutput(game.getMyName() + " " + e.getName() + ": " + evalScore, OutputFormat.BLUE, true);
			}
			result += e.getWeight() * evalScore;
		}
		if(debug && nameOfEvalToDebug == FULL_DEBUG)
			Risk.sayOutput("Final score for " + game.getMyName() + ": " + result, OutputFormat.BLUE, true);
		return result;
	}
	
	/*
	 * A signal that the game state has changed, and this should be sent on to the
	 * evaluators and GameStats.
	 */
	public void refresh() {
		stats.refresh();
		for(AbstractEvaluator e : evaluators) {
			e.refresh();
		}
	}
}
