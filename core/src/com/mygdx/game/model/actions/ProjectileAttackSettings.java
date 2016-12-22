package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.wrappers.StringWrapper;
import com.mygdx.game.model.projectiles.ProjectileSettings;

public class ProjectileAttackSettings implements Serializable{
	String abilitySettingKey;
	Array <StringWrapper> projectileSettingKeys;
	AbilitySettings abilitySettings;
	float projectilesOverTime;
	
	public ProjectileAttackSettings() {
		
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("abilitySettingKey", abilitySettingKey);
		json.writeValue("projectileSettingKeys", projectileSettingKeys);		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		abilitySettingKey = json.readValue("abilitySettingKey", String.class, jsonData);
		projectileSettingKeys = json.readValue("projectileSettingKeys", Array.class, jsonData);
		abilitySettings = JSONController.abilities.get(abilitySettingKey).deepCopy();
		
		Float projectilesOverTime = json.readValue("projectilesOverTime", Float.class, jsonData);
		if (projectilesOverTime != null) {
			this.projectilesOverTime = projectilesOverTime.floatValue(); 
		}
		else {
			this.projectilesOverTime = Float.MAX_VALUE;
		}
	}

	public AbilitySettings getAbilitySettings() {
		return abilitySettings;
	}

	public ProjectileAttackSettings deepCopy() {
		ProjectileAttackSettings copy = new ProjectileAttackSettings();
		copy.abilitySettings = this.abilitySettings.deepCopy();
		copy.abilitySettingKey = this.abilitySettingKey;
		copy.projectileSettingKeys = this.projectileSettingKeys;
		copy.projectilesOverTime = this.projectilesOverTime;
		return copy;
	}
}
