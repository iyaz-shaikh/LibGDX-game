package com.mygdx.game.model.projectiles;

import java.util.UUID;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectDataRetriever;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.events.ActionListener;

public class Explosion extends EntityModel implements EffectDataRetriever {
	
	ExplosionSettings settings;
	float currentTime;
	int allegiance;
	ActionListener actionListener;
	EntityUIModel explosionUIModel;
	String uuid;
	Array <CharacterModel> alreadyHitCharacters;
	String state;
	 
	
	public Explosion(String name, ExplosionSettings explosionSettings, ActionListener actionListener, Projectile projectile) {
		this.settings = explosionSettings;
		this.actionListener = actionListener;
		this.currentTime = 0f;
		this.explosionUIModel = new EntityUIModel(name, EntityUIDataType.EXPLOSION);
		this.widthCoefficient = this.settings.getWidthCoefficient();
		this.heightCoefficient = this.settings.getHeightCoefficient();
		this.imageHitBox.x = projectile.getImageHitBox().x + (projectile.getImageHitBox().width / 2f);
		this.imageHitBox.y = projectile.getImageHitBox().y + (projectile.getImageHitBox().height / 2f);
		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
		this.allegiance = projectile.getAllegiance();
		this.state = EntityModel.windupState;
	}
	
	public void update(float delta) {
		currentTime += delta;
		this.changeStateCheck();
		float oldWidth = 0f;
		oldWidth = this.imageHitBox.width;
		explosionUIModel.setCurrentFrame(this, delta);
		this.moveWithoutCollisionDetection(delta);
		this.setGameplaySize(delta);
		this.expansionCheck(oldWidth);
		this.actionListener.processExplosion(this);
		this.deletionCheck();
	}

	@Override
	public boolean handleAdditionCollisionLogic(Rectangle tempGameplayBounds) {
		return false;
	}

	@Override
	public int getAllegiance() {
		return this.allegiance;
	}
	
	public void processExplosionHit(CharacterModel target) {
		if (target != null) {
			for (EffectSettings effectSettings : settings.getTargetEffects()) {
				Effect effect = EffectInitializer.initializeEffect(effectSettings, this);
				target.addEffect(effect);
			}
			target.setImmuneToInjury(true);
		}
	}
	
	
	private void changeStateCheck() {
		if (((this.currentTime <= this.settings.getTotalTime() && this.currentTime > this.settings.getWindUpPlusDuration()))) {
			this.setState(EntityModel.cooldownState);
		}
		else if (this.currentTime > this.settings.getWindUpTime() && this.currentTime <= this.settings.getWindUpPlusDuration()) {
			this.setState(EntityModel.activeState);
		}
		else if (this.currentTime <= this.settings.getWindUpTime()) {
			this.setState(EntityModel.windupState);
		}		
	}
	
	public void expansionCheck(float oldWidth) {
		//Origin needs to move backwards to account for leftward expansion.
		float widthDifference = this.imageHitBox.width - oldWidth;
		if (widthDifference > 0) {
			this.gameplayHitBox.x = this.gameplayHitBox.x - ((widthDifference * this.widthCoefficient) / 2);
			this.imageHitBox.x = this.imageHitBox.x - widthDifference / 2;
		}

	}
	
	private void deletionCheck() {
		if (this.currentTime > this.settings.getTotalTime()) {
			this.actionListener.deleteExplosion(this);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Explosion) {
			return ((Explosion) obj).uuid.equals(this.uuid); 
		}
		return super.equals(obj);
	}

	public ExplosionSettings getExplosionSettings() {
		return settings;
	}

	public Array<CharacterModel> getAlreadyHitCharacters() {
		return alreadyHitCharacters;
	}

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		MovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.settings.targetEffects) {
			if (settings instanceof MovementEffectSettings) {
				mSettings = (MovementEffectSettings) settings;
			}
		}
		return mSettings;
	}
	
	@Override
	public boolean isFacingLeft() {
		return true;
	}

	@Override
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public EntityUIModel getExplosionUIModel() {
		return explosionUIModel;
	}
}