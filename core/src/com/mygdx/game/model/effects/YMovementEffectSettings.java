package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.conditions.AerialConditionSettings;
import com.mygdx.game.model.conditions.InjuredStaggerConditionSettings;
import com.mygdx.game.model.conditions.PassiveConditionSettings;

public class YMovementEffectSettings extends EntityEffectSettings{

	@Override
	public YMovementEffectSettings deepCopy() {
		YMovementEffectSettings copy = new YMovementEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.velocity = this.velocity;
		copy.acceleration = this.acceleration;
		copy.maxVelocity = this.maxVelocity;
		copy.useGravity = this.useGravity;
		copy.applyOnlyIfInjuredStaggered = this.applyOnlyIfInjuredStaggered;
		return copy;
	}
	
	float maxVelocity;
	float velocity;
	float acceleration;
	boolean useGravity;
	boolean applyOnlyIfInjuredStaggered;
	
	public void fillInDefaults() {
		super.fillInDefaults();
		maxVelocity = Float.MAX_VALUE;
		velocity = 0f;
		acceleration = 0f;
		setType(YMovementEffect.type);
		applyOnlyIfInjuredStaggered = false;
		this.useGravity = true;
	}

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("velocity", velocity);
		json.writeValue("acceleration", acceleration);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		velocity = json.readValue("velocity", Float.class, jsonData);
		
		Float maxVelocity = json.readValue("maxVelocityX", Float.class, jsonData);
		if (maxVelocity != null) {
			this.maxVelocity = maxVelocity;
		}
		else {
			this.maxVelocity = Float.MAX_VALUE;
		}
		Float acceleration = json.readValue("acceleration", Float.class, jsonData);
		Boolean useGravity = json.readValue("useGravity", Boolean.class, jsonData);
		if (useGravity != null) {
			this.useGravity = useGravity.booleanValue();
			if (!this.useGravity)
				this.acceleration = acceleration.floatValue();
		}
		else if (acceleration != null) {
			this.useGravity = false;
			this.acceleration = acceleration.floatValue();
		}
		else {
			this.useGravity = true;
		}

		this.setType(YMovementEffect.type);
		
		Boolean applyOnlyIfInjuredStaggered = json.readValue("applyOnlyIfInjuredStaggered", Boolean.class, jsonData);
		if (applyOnlyIfInjuredStaggered != null) {
			this.applyOnlyIfInjuredStaggered = applyOnlyIfInjuredStaggered.booleanValue();
		}
		else {
			this.applyOnlyIfInjuredStaggered = true;
		}
		if (this.applyOnlyIfInjuredStaggered) {
			this.passiveConditions.add(new InjuredStaggerConditionSettings());
		}
	}
	
	public boolean onlyWhenInAir() {
		for (PassiveConditionSettings condition : this.passiveConditions) {
			if (condition instanceof AerialConditionSettings) {
				return true;
			}
		}
		return false;
	}
	
	public float getEstimatedDistance() {
		float xDistance = (velocity * getDuration()) + (0.5f * acceleration * getDuration() * getDuration());
		return xDistance;
	}

	public float getVelocity() {
		return velocity;
	}

	public float getAcceleration() {
		return acceleration;
	}
	
	
}
