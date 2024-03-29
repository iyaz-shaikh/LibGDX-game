package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ProjectileCreateEffectSettings extends WorldEffectSettings{

	String projectileSettingKey;
	Vector2 origin;
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("origin", origin);
		json.writeValue("projectileSettingKey", projectileSettingKey);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		projectileSettingKey = json.readValue("projectileSettingKey", String.class, jsonData);
		Vector2 origin = json.readValue("origin", Vector2.class, jsonData);
		if (origin != null) {
			this.origin = origin; 
		}
		else {
			this.origin = new Vector2(0, 0);
		}
		setType(ProjectileCreateEffect.type);
	}

	
	@Override
	public WorldEffectSettings deepCopy() {
		ProjectileCreateEffectSettings copy = new ProjectileCreateEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.projectileSettingKey = this.projectileSettingKey;
		copy.origin = this.origin;	
		return copy;
	}
	
	public Vector2 getOrigin(boolean left) {
		if (left) {
			return new Vector2(-this.origin.x, this.origin.y);
		}
		return this.origin;
	}

}
