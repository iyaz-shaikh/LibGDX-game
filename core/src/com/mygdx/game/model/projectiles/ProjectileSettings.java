package com.mygdx.game.model.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.effects.EffectSettings;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectileSettings extends AbilitySettings{
	
	private Boolean disappearOnImpact;
	private String explosionName;
	private ExplosionSettings explosionSettings;
	private Boolean bounces;
	private Boolean tracks; //shouldn't be used with smartAim/bounces/hasCollisionDetection.
	private Boolean useSmartAim;
	private Boolean hasCollisionDetection;
	private Array <EffectSettings> targetEffects;
	private String name;
	private Float activeSpeed;
	private Float windupSpeed;
	private Float cooldownSpeed;
	private Float gravity;
	private Vector2 size;
	private Array<Vector2> possibleOrigins;
	private Float angleOfVelocity; //use this to fire projectile in a direction rather than at a target.
	private Float widthCoefficient;
	private Float heightCoefficient;
	private Float hitRate;

	public ProjectileSettings deepCopy() {
		ProjectileSettings copy = new ProjectileSettings();
		copy.setFieldsWithAbilitySettings(super.deepCopy());
		copy.explosionSettings = this.explosionSettings.deepCopy();
		copy.hitRate = this.hitRate;
		copy.disappearOnImpact = this.disappearOnImpact;
		copy.bounces = this.bounces;
		copy.tracks = this.tracks;
		copy.useSmartAim = this.useSmartAim;
		copy.hasCollisionDetection = this.hasCollisionDetection;
		copy.name = this.name;
		copy.activeSpeed = this.activeSpeed;
		copy.windupSpeed = this.windupSpeed;
		copy.cooldownSpeed = this.cooldownSpeed;
		copy.gravity = this.gravity;
		copy.size = this.size;
		copy.possibleOrigins = this.possibleOrigins;
		copy.angleOfVelocity = this.angleOfVelocity;
		copy.explosionName = this.explosionName;
		copy.widthCoefficient = this.widthCoefficient;
		copy.heightCoefficient = this.heightCoefficient;
		
		Array <EffectSettings> newTargetEffects = new Array <EffectSettings>();
		for(EffectSettings eSettings : targetEffects) {
			newTargetEffects.add(eSettings.deepCopy());
		}
		copy.targetEffects = newTargetEffects;
		
		return copy;
	}
	
	public ProjectileSettings() {
		
	}

	public void write(Json json) {
		super.write(json);
		json.writeValue("explosionSettings", explosionSettings);
		json.writeValue("disappearOnImpact", disappearOnImpact);
		json.writeValue("bounces", bounces);
		json.writeValue("tracks", tracks);
		json.writeValue("hitRate", hitRate);
		json.writeValue("useSmartAim", useSmartAim);
		json.writeValue("hasCollisionDetection", hasCollisionDetection);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("name", name);
		json.writeValue("speed", activeSpeed);
		json.writeValue("gravity", gravity);
		json.writeValue("size", size);
		json.writeValue("possibleOrigins", possibleOrigins);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("angleOfVelocity", angleOfVelocity);
		json.writeValue("widthCoefficient", widthCoefficient);
		json.writeValue("heightCoefficient", heightCoefficient);
	}

	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		disappearOnImpact = json.readValue("disappearOnImpact", Boolean.class, jsonData);
		bounces = json.readValue("bounces", Boolean.class, jsonData);
		tracks = json.readValue("tracks", Boolean.class, jsonData);
		hasCollisionDetection = json.readValue("hasCollisionDetection", Boolean.class, jsonData);
		useSmartAim = json.readValue("useSmartAim", Boolean.class, jsonData);
		targetEffects = json.readValue("targetEffects", Array.class, jsonData);
		name = json.readValue("name", String.class, jsonData);
		activeSpeed = json.readValue("activeSpeed", Float.class, jsonData);
		angleOfVelocity = json.readValue("angleOfVelocity", Float.class, jsonData);
		size = json.readValue("size", Vector2.class, jsonData);
		
		Array <Vector2> possibleOrigins = json.readValue("possibleOrigins", Array.class, jsonData);
		if (possibleOrigins != null) {
			this.possibleOrigins = possibleOrigins;
		}
		else {
			this.possibleOrigins = new Array<Vector2>();
			this.possibleOrigins.add(new Vector2(0, 0));
		}
		
		Float gravity = json.readValue("gravity", Float.class, jsonData);
		if (gravity != null) {
			this.gravity = gravity;
		}
		else {
			this.gravity = 0f;
		}
		
		Float windupSpeed = json.readValue("windupSpeed", Float.class, jsonData);
		if (windupSpeed != null) {
			this.windupSpeed = windupSpeed;
		}
		else {
			this.windupSpeed = activeSpeed;
		}
		
		Float cooldownSpeed = json.readValue("cooldownSpeed", Float.class, jsonData);
		if (cooldownSpeed != null) {
			this.cooldownSpeed = cooldownSpeed;
		}
		else {
			this.cooldownSpeed = activeSpeed;
		}
		
		String explosionName = json.readValue("explosionName", String.class, jsonData);
		if (explosionName != null) {
			this.explosionName = explosionName;
			this.explosionSettings = JSONController.explosions.get(explosionName).deepCopy();
		}
		
		
		Float widthCoefficient = json.readValue("widthCoefficient", Float.class, jsonData);
		if (widthCoefficient != null) {
			this.widthCoefficient = widthCoefficient;
		}
		else {
			this.widthCoefficient = 1f;
		}
		
		Float heightCoefficient = json.readValue("heightCoefficient", Float.class, jsonData);
		if (heightCoefficient != null) {
			this.heightCoefficient = heightCoefficient;
		}
		else {
			this.heightCoefficient = 1f;
		}
		
		Float hitRate = json.readValue("hitRate", Float.class, jsonData);
		if (hitRate != null) {
			this.hitRate = hitRate;
		}
		else {
			this.hitRate = 5f;
		}
		
	}
	
	public void disableGravity() {
		this.gravity = 0f;
	}
	
	public boolean isDisappearOnImpact() {
		return disappearOnImpact;
	}

	public boolean isBounces() {
		return bounces;
	}

	public boolean isTracks() {
		return tracks;
	}

	public boolean isUseSmartAim() {
		return useSmartAim;
	}

	public boolean isHasCollisionDetection() {
		return hasCollisionDetection;
	}

	public Array<EffectSettings> getTargetEffects() {
		return targetEffects;
	}
	
	public String getName() {
		return name;
	}

	public Float getSpeed(String state) {
		if (state.equals(EntityModel.windupState)) {
			return this.windupSpeed;
		}
		else if (state.equals(EntityModel.activeState)) {
			return this.activeSpeed;
		}
		else if (state.equals(EntityModel.cooldownState)) {
			return this.cooldownSpeed;
		}
		return activeSpeed;
	}

	public Float getGravity() {
		return gravity;
	}

	public Vector2 getSize() {
		return size;
	}

	public Array<Vector2> getPossibleOrigins() {
		return possibleOrigins;
	}

	public Float getAngleOfVelocity() {
		return angleOfVelocity;
	}

	public ExplosionSettings getExplosionSettings() {
		return explosionSettings;
	}

	public String getExplosionName() {
		return explosionName;
	}

	public Float getWidthCoefficient() {
		return widthCoefficient;
	}

	public Float getHeightCoefficient() {
		return heightCoefficient;
	}
	
	
}
