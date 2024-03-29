package com.mygdx.game.model.characters.enemies;

import java.util.HashMap;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class ObservationBlock {
	HashMap <String, Observation> observations;
	CharacterModel sourceOfObservation;
	
	public ObservationBlock(CharacterModel sourceOfObservation) {
		observations = new HashMap <String, Observation> ();
		this.sourceOfObservation = sourceOfObservation; 
	}
	
	public void addObservation(Observation observation) {
		if (observation.observationTarget.getUuid().equals(this.sourceOfObservation.getUuid())) {
			if (observation instanceof PositionalObservation) {
				observations.put(PositionalObservation.classKey, observation);
			}
		}
	}
}
