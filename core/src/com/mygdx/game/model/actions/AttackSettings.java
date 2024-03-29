package com.mygdx.game.model.actions;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.assets.HitSparkUtils;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.hitSpark.HitSparkData;

public class AttackSettings extends AbilitySettings{
	Array <Rectangle> hitBoxProperties;
	float hitRate;
	Array<EffectSettings> targetEffectSettings;
	boolean shouldStagger;
	boolean targetRespectEntityCollisions; //if this is false, the action ignores all entity collisions.
	HitSparkData hitSparkData;
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		super.write(json);
		json.writeValue("hitRate", hitRate);
		json.writeValue("targetEffectSettings", targetEffectSettings);
		json.writeValue("shouldStagger", shouldStagger);
		json.writeValue("targetRespectEntityCollisions", targetRespectEntityCollisions);
		json.writeValue("hitBoxProperties", hitBoxProperties);
		json.writeValue("hitSparkData", hitSparkData);

	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		super.read(json, jsonData);
//		width = json.readValue("width", Float.class, jsonData);
//		height = json.readValue("height", Float.class, jsonData);
//		originX = json.readValue("originX", Float.class, jsonData);
//		originY = json.readValue("originY", Float.class, jsonData);
		targetEffectSettings = json.readValue("targetEffectSettings", Array.class, jsonData);
		
		
		Float tickRate = json.readValue("hitRate", Float.class, jsonData);
		if (tickRate != null) {
			hitRate = tickRate.floatValue();
		}
		else {
			hitRate = Float.MAX_VALUE;
		}
		
		Boolean shouldStagger = json.readValue("shouldStagger", Boolean.class, jsonData);
		if (shouldStagger != null) {
			this.shouldStagger = shouldStagger;
		}
		else {
			this.shouldStagger = true;
		}
		
		Boolean targetRespectEntityCollisions = json.readValue("targetRespectEntityCollisions", Boolean.class, jsonData);
		if (targetRespectEntityCollisions != null) {
			this.targetRespectEntityCollisions = targetRespectEntityCollisions;
		}
		else {
			this.targetRespectEntityCollisions = true;
		}

		this.hitBoxProperties = json.readValue("hitBoxProperties", Array.class, jsonData);
		if (hitBoxProperties == null) {
			this.hitBoxProperties = new Array <Rectangle>();
		}
		
		this.hitSparkData = json.readValue("hitSparkData", HitSparkData.class, jsonData);
		if (this.hitSparkData == null) {
			this.hitSparkData = HitSparkUtils.defaultData();
		}
	}
	
	@Override
	public AttackSettings deepCopy() {
		AttackSettings copy = new AttackSettings();
		copy.setFieldsWithAbilitySettings(super.deepCopy());
		copy.hitRate = this.hitRate;
		copy.shouldStagger = this.shouldStagger;
		Array<EffectSettings> newTargetSettings = new Array <EffectSettings> ();
		for (EffectSettings eSettings : this.targetEffectSettings) {
			newTargetSettings.add(eSettings.deepCopy());
		}
		copy.targetEffectSettings = newTargetSettings;
		copy.hitBoxProperties = this.hitBoxProperties;
		copy.hitSparkData = this.hitSparkData;
		return copy;
	}

	public Array<EffectSettings> getTargetEffectSettings() {
		return targetEffectSettings;
	}

	public boolean isShouldStagger() {
		return shouldStagger;
	}

	public HitSparkData getHitSparkData() {
		return hitSparkData;
	}
}
