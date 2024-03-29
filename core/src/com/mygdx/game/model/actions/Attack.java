package com.mygdx.game.model.actions;

import java.util.Iterator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;

public class Attack extends ActionSegment {
	 
	Array<Rectangle> hitBoxes;
	int allegiance;
	AttackSettings attackSettings;
	Array <EntityEffect> activeSourceEffects;
	Array <EntityEffect> windupSourceEffects;
	Array <HitTracker> alreadyHitCharacters;
	CollisionChecker collisionChecker;
	boolean hasConfirmedHit;

	
	public Attack(CharacterModel source, AttackSettings settings, ActionListener listener, CollisionChecker collisionChecker) {
		super(listener);
		this.source = source;
		this.allegiance = source.getAllegiance();
		this.attackSettings = settings.deepCopy();
		this.activeSourceEffects = new Array <EntityEffect>();
		this.windupSourceEffects = new Array <EntityEffect>();
		this.alreadyHitCharacters = new Array<HitTracker>();
		this.collisionChecker = collisionChecker;
		this.hasConfirmedHit = false;
		this.hitBoxes = new Array <Rectangle>();
		for (int i = 0; i < this.attackSettings.hitBoxProperties.size; i++) {
			Rectangle hitBoxProperties = this.attackSettings.hitBoxProperties.get(i);
			hitBoxes.add(new Rectangle(0, 0, hitBoxProperties.width, hitBoxProperties.height));
		}
		this.setDurations(source);
	}
	
	public void processAttackOnEntity(EntityModel target) {
		for (HitTracker tracker : this.alreadyHitCharacters) {
			if (tracker.entityHit.equals(target)){
				return;
			}
		}
		this.hasConfirmedHit = true;
		boolean isIntercepted = target.checkIfIntercepted(this);
		Iterator <EffectSettings> iterator = this.attackSettings.targetEffectSettings.iterator();
		while(iterator.hasNext()) {
			EffectSettings effectSettings = iterator.next();
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			if (!isIntercepted || (isIntercepted && effect.shouldAddIfIntercepted())) {
				if (effect.shouldReciprocateToSource(target, getActionListener())) {
					effect.flipValues();
					source.addEffect(effect);
					iterator.remove();
				}
				else {
					effect.flipValuesIfNecessary(target, source);
					target.addEffect(effect);
				}
			}
		}


		target.actionStagger(false);
		if (!source.isActionStaggering()) {
			source.actionStagger(false);
		}
		this.shouldChain = true;
			
		this.alreadyHitCharacters.add(new HitTracker(target));
	} 
	
	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		Attack attack = new Attack(source, attackSettings, this.actionListener, this.collisionChecker);
		return attack;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		Iterator <HitTracker> iterator = this.alreadyHitCharacters.iterator();
		while(iterator.hasNext()) {
			HitTracker tracker = iterator.next();
			boolean isFinished = tracker.update(delta, this.attackSettings.hitRate);
			if (isFinished) {
				iterator.remove();
			}
		}
		this.updateHitBoxes();
		actionListener.processAttack(this);

		
	}
	
	public void sourceActiveProcessAdditionalBehavior(CharacterModel source) {
		if (this.attackSettings.tempWidthModifier != null) {
			source.setWidthCoefficient(this.attackSettings.tempWidthModifier.floatValue());
		}
		if (this.attackSettings.tempHeightModifier != null) {
			source.setHeightCoefficient(this.attackSettings.tempHeightModifier.floatValue());
		}
		if (this.attackSettings.xOffsetModifier != null) {
//			source.setxOffsetModifier(this.attackSettings.xOffsetModifier.floatValue());
		}
		if (this.attackSettings.yOffsetModifier != null) {
//			source.setyOffsetModifier(this.attackSettings.yOffsetModifier.floatValue());
		}
		if (!this.shouldRespectEntityCollisions()) {
			source.setRespectingEntityCollision(false);
			source.lockEntityCollisionBehavior();
		}
		
		for (EffectSettings effectSettings : attackSettings.sourceEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			effect.flipValuesIfNecessary(null, source);
			source.addEffect(effect);
			activeSourceEffects.add(effect);
		}
	}
	
	public void sourceWindupProcessAdditonalBehavior(CharacterModel source) {
		for (EffectSettings effectSettings : attackSettings.windupEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			effect.flipValuesIfNecessary(null, source);
			source.addEffect(effect);
			windupSourceEffects.add(effect);
		}
	}
	
	public void sourceCompletionAdditionalBehavior(CharacterModel source) {
		source.setWidthCoefficient(source.getCharacterProperties().getWidthCoefficient());
		source.setHeightCoefficient(source.getCharacterProperties().getHeightCoefficient());
		source.unlockEntityCollisionBehavior();

	}
	
	@Override
	public boolean willActionHitTarget(CharacterModel target) {
		for (Rectangle attackHitBox : this.attackSettings.hitBoxProperties) {
			Rectangle tempAttackHitBox = new Rectangle (attackHitBox);
			float xDistance = 0f;
			float yDistance = 0f;
			for (EffectSettings effectSettings : this.attackSettings.sourceEffectSettings) {
				if (effectSettings instanceof YMovementEffectSettings) {
					YMovementEffectSettings mEffectSettings = (YMovementEffectSettings) effectSettings;
					yDistance += mEffectSettings.getEstimatedDistance();
				}
				else if (effectSettings instanceof XMovementEffectSettings) {
					XMovementEffectSettings mEffectSettings = (XMovementEffectSettings) effectSettings;
					xDistance += mEffectSettings.getEstimatedDistance();
				}
			}
			tempAttackHitBox.x += source.gameplayCollisionBox.x + xDistance;
			tempAttackHitBox.y += source.gameplayCollisionBox.y + yDistance;
			for (Rectangle targetHurtBox : target.getGameplayHurtBoxes()) {
				if (attackHitBox.overlaps(targetHurtBox)) {
					return true;
				}
			}
		}
		return false;
	}
	
//	@Override
//	public float getEffectiveRange() {
//		float range = 0f;
//		float xDistance = 0f;
//		float yDistance = 0f;
//		for (EffectSettings effectSettings : this.attackSettings.sourceEffectSettings) {
//			if (effectSettings instanceof YMovementEffectSettings) {
//				YMovementEffectSettings mEffectSettings = (YMovementEffectSettings) effectSettings;
//				yDistance += mEffectSettings.getEstimatedDistance();
//			}
//			else if (effectSettings instanceof XMovementEffectSettings) {
//				XMovementEffectSettings mEffectSettings = (XMovementEffectSettings) effectSettings;
//				xDistance += mEffectSettings.getEstimatedDistance();
//			}
//		}
//		range = (float) Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
//		return range + hitBoxes.width;
//	}

//	@Override
//	public float getWindUpTime() {
//		return this.attackSettings.windupTime;
//	}
//	
//	@Override 
//	public float getWindUpPlusActionTime() {
//		return this.attackSettings.windupTime + this.attackSettings.duration;
//	}
//	
//	@Override
//	public float getTotalTime() {
//		if (this.forceCooldownState) {
//			return this.attackSettings.windupTime + this.activeTime + this.attackSettings.cooldownTime;
//		}
//		return this.attackSettings.windupTime + this.attackSettings.duration + this.attackSettings.cooldownTime;
//	}
	
	@Override
	public float getWindUpTime() {
		return this.forceActiveState ? this.processedWindupTime : this.windupTime;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return getWindUpTime() + (this.forceCooldownState ? this.processedActiveTime : this.activeTime);
	}
	
	@Override
	public float getTotalTime() {
//		if (this.forceCooldownState) {
//			return getWindUpTime() + this.activeTime + this.settings.cooldownTime;
//		}
		return getWindUpPlusActionTime() + this.cooldownTime;
	}

	
	@Override
	public void interruptionBlock() {
		for(EntityEffect effect : this.activeSourceEffects) {
			effect.setForceEnd(true);
		}
		for (EntityEffect effect : this.windupSourceEffects) {
			effect.setForceEnd(true);
		}
		
//		System.out.println("Interrupt");
	}
	
	public Array <Rectangle> getHitBoxes() {
		return hitBoxes;
	}

	public int getAllegiance() {
		return allegiance;
	}

	public AttackSettings getAttackSettings() {
		return attackSettings;
	}

	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public void setAllegiance(int allegiance) {
		this.allegiance = allegiance;
	}

	public Array<HitTracker> getAlreadyHitCharacters() {
		return alreadyHitCharacters;
	}
	
	public void updateHitBoxes() {
		for (int i = 0; i < hitBoxes.size; i++) {
			Rectangle hitBox = hitBoxes.get(i);
			if (source.isFacingLeft()) {
				hitBox.x = source.getGameplayCollisionBox().x - this.attackSettings.hitBoxProperties.get(i).x - this.attackSettings.hitBoxProperties.get(i).width;
			}
			else {
				hitBox.x = source.getGameplayCollisionBox().x + this.attackSettings.hitBoxProperties.get(i).x + source.getGameplayCollisionBox().width;
			}
			hitBox.y = source.getGameplayCollisionBox().y + this.attackSettings.hitBoxProperties.get(i).y ;
		}
	}

	@Override
	public XMovementEffectSettings getXReplacementMovementForStagger() {
		XMovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.attackSettings.targetEffectSettings) {
			if (settings instanceof XMovementEffectSettings) {
				mSettings = (XMovementEffectSettings) settings;
			}
		}
		return mSettings;
	}
	
	@Override
	public YMovementEffectSettings getYReplacementMovementForStagger() {
		YMovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.attackSettings.targetEffectSettings) {
			if (settings instanceof YMovementEffectSettings) {
				mSettings = (YMovementEffectSettings) settings;
			}
		}
		return mSettings;
	}

	public boolean shouldStagger() {
		return this.attackSettings.shouldStagger;
	}

	@Override
	public boolean shouldRespectEntityCollisions() {
		return this.attackSettings.sourceRespectEntityCollisions;
	}

	@Override
	public boolean doesNeedDisruptionDuringWindup() {
		return this.attackSettings.windupTillDisruption;
	}

	@Override
	public boolean doesNeedDisruptionDuringActive() {
		return this.attackSettings.activeTillDisruption;
	}
	
	@Override
	public void setDurations(CharacterModel source) {
		this.windupTime = this.attackSettings.windupTillDisruption ? Float.MAX_VALUE : source.getUiModel().getTimeForAnimation(this.attackSettings.name, ActionSegment.Windup);
		this.activeTime = this.attackSettings.activeTillDisruption ? Float.MAX_VALUE : source.getUiModel().getTimeForAnimation(this.attackSettings.name, ActionSegment.Active);
		this.cooldownTime = source.getUiModel().getTimeForAnimation(this.attackSettings.name, ActionSegment.Cooldown);
		//Debug purposes.
		if (windupTime == 0f)
		{
			System.out.println("WINDUPTIME = 0f");
		}
		if (activeTime == 0f)
		{
			System.out.println("ACTIVETIME = 0f");
		}
		if (cooldownTime == 0f)
		{
			System.out.println("COOLDOWNTIME = 0f");
		}
	}
	
	@Override
	public XMovementEffectSettings getSourceXMove() {
		for(EffectSettings effect : this.attackSettings.sourceEffectSettings) {
			if (effect instanceof XMovementEffectSettings) {
				return (XMovementEffectSettings) effect.deepCopy();
			}
		}
		return null;
	}

	@Override
	public YMovementEffectSettings getSourceYMove() {
		for(EffectSettings effect : this.attackSettings.sourceEffectSettings) {
			if (effect instanceof YMovementEffectSettings) {
				return (YMovementEffectSettings) effect.deepCopy();
			}
		}
		return null;
	}
	
	@Override
	public boolean chainsWithJump() {
		return attackSettings.chainsWithJump;
	}
	
	@Override
	public boolean isSuper() {
		return this.attackSettings.isSuper;
	}

	@Override
	public boolean metChainConditions() {
		return this.hasConfirmedHit;
	}

	@Override
	public void updateHurtBoxes() {
		if (this.actionState.equals(ActionState.WINDUP)) {
			source.updateHurtBoxProperties(this.attackSettings.windupHurtBoxProperties);
		}
		else if (this.actionState.equals(ActionState.ACTIVE)) {
			source.updateHurtBoxProperties(this.attackSettings.activeHurtBoxProperties);
		}
		else if (this.actionState.equals(ActionState.COOLDOWN)) {
			source.updateHurtBoxProperties(this.attackSettings.cooldownHurtBoxProperties);
		}
	}
	
	
}
