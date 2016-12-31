package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;

public abstract class EffectSettings implements Serializable {
	private Float duration;
	private Boolean isInstantaneous;
	private float delayToActivate;
	Boolean isPermanent;
	private String type;
	
	@Override
	public void write(Json json) {
		json.writeValue("isInstantaneous", isInstantaneous());
		json.writeValue("duration", getDuration());
		json.writeValue("delayToActivate", getDelayToActivate());
		json.writeValue("isPermanent", isPermanent);
		json.writeValue("type", type);

	}
	
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		setIsInstantaneous(json.readValue("isInstantaneous", Boolean.class, jsonData));
		if (isInstantaneous() == null || isInstantaneous().booleanValue() == true) {
			setIsInstantaneous(true);
			setDuration(0f);
			setDelayToActivate(0f);
			isPermanent = false;
		}
		else {
			setIsInstantaneous(false);
			Boolean isPermanent = json.readValue("isPermanent", Boolean.class, jsonData);
			Float duration = json.readValue("duration", Float.class, jsonData);
			if (isPermanent != null && isPermanent.booleanValue()) {
				this.setDuration(Float.MAX_VALUE);
				this.isPermanent = true;
			}
			else if (duration != null) {
				this.setDuration(duration);
			}
			else {
				this.setDuration(0.5f);
			}
			
			Float delayToActivate = json.readValue("delayToActivate", Float.class, jsonData);
			if (delayToActivate != null) {
				this.setDelayToActivate(delayToActivate);
			}
			else 
			{
				this.setDelayToActivate(0f);
			}
		}
	}
	
	public abstract EffectSettings deepCopy();	
	
	public void setBaseFieldsForSettings(EffectSettings settings) {
		settings.setDuration(this.getDuration());
		settings.setDelayToActivate(this.getDelayToActivate());
		settings.setIsInstantaneous(this.isInstantaneous());
		settings.isPermanent = this.isPermanent;
		settings.setType(this.getType());
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public float getDelayToActivate() {
		return delayToActivate;
	}


	public void setDelayToActivate(float delayToActivate) {
		this.delayToActivate = delayToActivate;
	}


	public Boolean isInstantaneous() {
		return isInstantaneous;
	}


	public void setIsInstantaneous(Boolean isInstantaneous) {
		this.isInstantaneous = isInstantaneous;
	}


	public Float getDuration() {
		return duration;
	}


	public void setDuration(Float duration) {
		this.duration = duration;
	}
}