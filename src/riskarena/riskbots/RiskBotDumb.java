/*******************************************************************************
 * Copyright (c) 2012-2014 Evan Radkoff.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available in LICENSE.txt or at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package riskarena.riskbots;
/*
 * A dumb RiskBot used for testing purposes. See HOWTO or RiskBot.java for more on what these methods do.
 * 
 * Evan Radkoff
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import riskarena.Bot;
import riskarena.CountryInfo;
import riskarena.GameInfo;
import riskarena.PlayerInfo;
import riskarena.RiskBot;
import riskarena.World;
import riskarena.Bot.RiskListener;
import riskarena.riskbots.evaluation.CardIndicator;
import riskarena.riskbots.evaluation.Evaluation;

public class RiskBotDumb implements RiskBot{
	private Bot.RiskListener to_game;
	private GameInfo risk_info;
	private World world = null;
	private PlayerInfo[] players = null;
	private CardIndicator card;

	Random gen;

	// Initialize the bot, locally store the given instance of GameInfo so that we can
	// get board info any time we want, as well as a RiskListener so we can communicate our answers.
	public void init(GameInfo gi, Bot.RiskListener rl) {
		risk_info = gi;
		to_game = rl;
		world = risk_info.getWorldInfo();
		players = risk_info.getPlayerInfo();
		gen = new Random((new Date()).getTime());
		//gen = new Random(5);
		card = new CardIndicator();
	}
	
	/*
	 * Turn-based initialization
	 * @see riskarena.RiskBot#initTurn()
	 */
	public void initTurn() {
		card.setVictory(false);
	}

	// Claim the country in the continent with the highest percentage of claimed friendly countries
	public void claimTerritory() {
		CountryInfo[] countries = risk_info.getCountryInfo();
		int num_conts = risk_info.getNumContinents();
		
		// Calculate the percentages of territory ownership for each continent of:
		// taken by me [contID][0], taken by some enemy [contID][1], and unclaimed [contID][2]
		int counts[][] = new int[num_conts][3];
		for(int i=0;i<num_conts;i++) {
			Arrays.fill(counts[i], 0);
		}
		for(int i=0;i<countries.length;i++) {
			int counts_index = !countries[i].isTaken() ? 2 : 0;
			if(counts_index == 0)
				counts_index = countries[i].getPlayer() == risk_info.me() ? 0 : 1;
			counts[countries[i].getCont()][counts_index] += 1;
		}
		double ratios[][] = new double[num_conts][3];
		for(int i=0;i<num_conts;i++) {
			for(int j=0;j<3;j++) {
				ratios[i][j] = ((double)counts[i][j])/(counts[i][0] + counts[i][1] + counts[i][2]);
			}
		}
		
		int targetCont = -1;	// Search for a target of interest
		double highest = -1.0;	
		for(int i=0;i<ratios.length;i++) {
			// Skip filled continents and ones already mostly claimed by others
			if(ratios[i][2] < .0000001 || ratios[i][1] > .2)
				continue;
			if(ratios[i][0] > highest) {
				highest = ratios[i][0];
				targetCont = i;
			}
		}
		// If no target continent was decided, choose the one with the lowest ratio of enemy territories
		if(targetCont == -1) {
			double lowest = 2.0;
			for(int i=0;i<ratios.length;i++) {
				if(ratios[i][2] < .0000001)
					continue;
				if(ratios[i][1] < lowest) {
					lowest = ratios[i][1];
					targetCont = i;
				}
			}
		}
		for(int i=0;i<countries.length;i++) {
			if(!countries[i].isTaken() && countries[i].getCont() == targetCont) {
				to_game.sendInt(i);
				return;
			}
		}
	}

	// Fortify a random territory with all armies that need placement
	public void fortifyTerritory(int num_to_place) {
		CountryInfo[] countries = risk_info.getCountryInfo();
		ArrayList<Integer> mine = new ArrayList<Integer>();

		for(int i=0;i<countries.length;i++) {
			if(countries[i].getPlayer() == risk_info.me()) {
				mine.add(new Integer(i));
			}
		}
		int choice = gen.nextInt(mine.size());

		to_game.sendInt(mine.get(choice).intValue());
		to_game.sendInt(num_to_place);
	}

	public void launchAttack() {
		CountryInfo[] countries = risk_info.getCountryInfo();
		for(int i=0;i<countries.length;i++) {
			if(countries[i].getPlayer() == risk_info.me() && countries[i].getArmies() > 4) {
				int[] adj = world.getAdjacencies(i);
				for(int j=0;j<adj.length;j++) {
					if(countries[adj[j]].getPlayer() != risk_info.me()) {
						//System.out.println("LOLOLOLOL ATTACKING FROM " + countries[i].getName() + "(" + countries[i].getArmies() + " armies) to " + countries[adj[j]].getName());
						to_game.sendInt(i);
						to_game.sendInt(adj[j]);
						to_game.sendInt(Math.min(countries[i].getArmies()-1, 3));
						return;
					}
				}
			}
		}
		to_game.sendInt(-1);
	}

	public void fortifyAfterVictory(int attacker, int defender, int min, int max) {
		card.setVictory(true);
		to_game.sendInt(max);
	}

	public void chooseToTurnInSet() {
		to_game.sendInt(1);
	}

	public void chooseCardSet(int[][] possible_sets) {
		to_game.sendInt(0);
	}

	public void fortifyPosition() {
		CountryInfo[] countries = risk_info.getCountryInfo();
		for(int i=0;i<countries.length;i++) {
			if(countries[i].getPlayer() == risk_info.me() && countries[i].getArmies() > 1) {
				int[] adj = world.getAdjacencies(i);
				/*System.out.println("Adjacencies of " + countries[i].getName() + ": ");
				for(int j=0;j<adj.length;j++)
					System.out.println("\t" + countries[adj[j]].getName());*/
				for(int j=0;j<adj.length;j++) {
					if(countries[adj[j]].getPlayer() == risk_info.me() && countries[adj[j]].getArmies() > countries[i].getArmies()) {
						to_game.sendInt(adj[j]);
						to_game.sendInt(i);
						to_game.sendInt(countries[adj[j]].getArmies()-1);
						return;
					}
				}
			}
		}
		to_game.sendInt(-1);
	}

	@Override
	public void endTurn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endGame(int place) {
		// TODO Auto-generated method stub
		
	}

}
