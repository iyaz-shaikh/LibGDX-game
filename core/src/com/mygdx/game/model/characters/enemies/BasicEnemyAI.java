package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class BasicEnemyAI extends EnemyAI {

	public static final String name = "Basic";
	
	public BasicEnemyAI(EnemyModel source, WorldModel world) {
		super(source, world);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ObservationBlock createObservationBlockFromCharacter(CharacterModel characterModel) {
		ObservationBlock observationBlock = super.createObservationBlockFromCharacter(characterModel);
		
		return observationBlock;
	}

	@Override
	public void handleObservation(Observation data) {
		// TODO Auto-generated method stub
		// From PlayerObserver
	}

	@Override
	public void setNextActionSequences(ArrayList<ActionSequence> possibleActionSequences) {
		// TODO Auto-generated method stub
		if (!this.source.isActionLock() && !isDocile()) {
			ArrayList <ActionSequence> actualActionSequences = new ArrayList <ActionSequence> ();
			for (ActionSequence sequence : possibleActionSequences) {
				float randomFloat = rand.nextFloat();
				if (randomFloat < 0.3) {
					actualActionSequences.add(sequence);
				}
			}
			if (actualActionSequences.size() > 0 && !this.source.isProcessingActiveSequences()) {
				this.source.stopHorizontalMovement();
				ActionSequence actionSequence = actualActionSequences.get(rand.nextInt(actualActionSequences.size()));
				this.nextActionSequences.add(actionSequence);
			}
			else if (!this.source.isProcessingActiveSequences()){ 
				//walk towards or away nearest enemy
				source.horizontalMove(rand.nextBoolean());
			}
		}

	}
}
