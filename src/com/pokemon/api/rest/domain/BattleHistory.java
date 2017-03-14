package com.pokemon.api.rest.domain;

import java.util.List;

public class BattleHistory {
	
	private Pokemon winner;
	private Pokemon loser;
	private List<Attack> attacks;
	
	public Pokemon getWinner() {
		return winner;
	}
	public void setWinner(Pokemon winner) {
		this.winner = winner;
	}
	public Pokemon getLoser() {
		return loser;
	}
	public void setLoser(Pokemon loser) {
		this.loser = loser;
	}
	public List<Attack> getAttacks() {
		return attacks;
	}
	public void setAttacks(List<Attack> attacks) {
		this.attacks = attacks;
	}

}
