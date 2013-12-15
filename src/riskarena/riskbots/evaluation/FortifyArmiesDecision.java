package riskarena.riskbots.evaluation;

import java.util.ArrayList;

import riskarena.CountryInfo;
import riskarena.GameInfo;
import riskarena.OutputFormat;
import riskarena.Risk;

public class FortifyArmiesDecision {
	private GameInfo game;
	private Evaluation eval;
	
	public FortifyArmiesDecision(GameInfo _game, Evaluation _eval) {
		game = _game;
		eval = _eval;
	}
	
	public ArrayList<ArmyChange> decide(int numToPlace) {
		eval.refresh();
		ArrayList<ArmyChange> winner = null;
		double highest = -1*Double.MAX_VALUE;
		CountryInfo countries[] = game.getCountryInfo();
		for(int i=0; i<countries.length; i++) {
			if(countries[i].getPlayer() != game.me())
				continue;
			ArmyChange change = new ArmyChange(i, numToPlace);
			ArrayList<ArmyChange> changes = new ArrayList<ArmyChange>();
			changes.add(change);
			double score = eval.score(changes);
			//Risk.sayOutput(countries[i].getName() + ": " + score, OutputFormat.BLUE);
			if(score > highest) {
				highest = score;
				winner = new ArrayList<ArmyChange>(changes.size());
				winner.add(new ArmyChange(changes.get(0)));
			}
		}
		return winner;
	}
}