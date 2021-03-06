/*******************************************************************************
 * Copyright (c) 2012-2014 Evan Radkoff.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available in LICENSE.txt or at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package riskarena.riskbots.evaluation.evals;
/*
 * Uses a heuristic function to score each enemy player, and returns the largest such score
 * Makes it negative, since a higher enemy score is bad
 */

import java.util.ArrayList;

import riskarena.GameInfo;
import riskarena.PlayerInfo;
import riskarena.riskbots.evaluation.ArmyChange;
import riskarena.riskbots.evaluation.GameStats;
import riskarena.riskbots.evaluation.OccupationChange;

public class BestEnemyEvaluator extends AbstractEvaluator {
	private double score;
	private PlayerInfo[] players;
	
	public BestEnemyEvaluator(String name, GameStats stats, GameInfo game) {
		super(name, stats, game);
		refresh();
	}
	
	public double getScore() {
		return score;
	}
	
	public double getScore(OccupationChange change) {
		return recalculate();
	}
	
	public double getScore(ArrayList<ArmyChange> changes) {
		return recalculate();
	}
	
	public void refresh() {
		players = stats.getPlayers();
		score = recalculate();
	}
	
	private double recalculate() {
		double highestScore = -1*Double.MAX_VALUE;
		for(int i=0; i<players.length; i++) {
			if(players[i].getId() == game.me())
				continue;
			double enemyScore = rateEnemy(players[i].getId());
			if(enemyScore > highestScore)
				highestScore = enemyScore;
		}
		return -1 * highestScore;
	}
	
	private double rateEnemy(int player_id) {
		double armyRatio = 0.0, territoryRatio = 0.0;
		if(stats.getTotalArmies() != 0)
			armyRatio = stats.getArmiesPerPlayer()[player_id] / (double)stats.getTotalArmies();
		if(game.getNumContinents() != 0)
			territoryRatio = stats.getOccupationCounts()[player_id] / (double)game.getNumCountries();
		double enemyScore = (armyRatio + territoryRatio) / 2.0;
		return enemyScore;
	}

}
