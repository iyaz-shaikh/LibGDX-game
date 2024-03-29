package com.mygdx.game.model.characters;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.ActionSequence.UseType;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.wrappers.StringWrapper;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;

public class CharacterProperties implements Serializable {
	float maxHealth;
	float maxWill;
	float attack;
	float maxStability;
	float maxTension;
	float staggerAllowanceTime;
	float collisionHeightCoefficient;
	float collisionWidthCoefficient;
	Float gravity;
	Float horizontalSpeed;
	Float horizontalAcceleration;
	Float sprintSpeed;
	Float jumpSpeed;
	Float injuryImmunityTime;
	Integer allegiance;
	CharacterModel source;
	HashMap<String, ActionSequence> actions;
	Array <Rectangle> defaultHurtboxProperties;
	Array <Rectangle> crouchingHurtboxProperties;
	Array <StringWrapper> weaponKeys;
	Array <ActionSequence> sequencesSortedByInputSize;
	boolean useDefaultWakeup;
	boolean useDefaultStagger;
	boolean useDefaultAerialStagger;
	boolean useDefaultTensionStagger;
	boolean shouldRespectEntityCollisions;
	boolean shouldRespectTileCollisions;
	boolean shouldRespectObjectCollisions;
	int maxJumpTokens;
	boolean boltedDown;
	float xRotationCoefficient;
	float yRotationCoefficient;
	
	Float xCollisionOffsetModifier;
	Float yCollisionOffsetModifier;
	
	public CharacterProperties() {
		
	}

	@Override
	public void write(Json json) {
		json.writeValue("maxHealth", maxHealth);
		json.writeValue("maxStability", maxStability);
		json.writeValue("maxWill", maxWill);
		json.writeValue("maxTension", maxTension);
		json.writeValue("attack", attack);
		json.writeValue("actions", actions);
		json.writeValue("horizontalSpeed", horizontalSpeed);
		json.writeValue("jumpSpeed", jumpSpeed);
		json.writeValue("gravity", gravity);
		json.writeValue("injuryImmunityTime", injuryImmunityTime);
		json.writeValue("allegiance", allegiance);
		json.writeValue("collisionWidthCoefficient", collisionWidthCoefficient);
		json.writeValue("collisionHeightCoefficient", collisionHeightCoefficient);
		json.writeValue("weaponKeys", weaponKeys);
		json.writeValue("shouldRespectEntityCollisions", shouldRespectEntityCollisions);
		json.writeValue("shouldRespectTileCollisions", shouldRespectTileCollisions);
		json.writeValue("shouldRespectObjectCollisions", shouldRespectObjectCollisions);
		json.writeValue("defaultHurtboxProperties", defaultHurtboxProperties);
		json.writeValue("crouchingHurtboxProperties", crouchingHurtboxProperties);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		maxHealth = json.readValue("maxHealth", Float.class, jsonData);
		maxTension = json.readValue("maxTension", Float.class, jsonData);
		maxWill = json.readValue("maxWill", Float.class, jsonData);
		attack = json.readValue("attack", Float.class, jsonData);
		maxStability = json.readValue("maxStability", Float.class, jsonData);
		actions = json.readValue("actions", HashMap.class, jsonData);

		Array <StringWrapper> weaponKeys = json.readValue("weaponKeys", Array.class, jsonData);
		if (weaponKeys == null) {
			this.weaponKeys = new Array <StringWrapper>();
		}
		else {
			this.weaponKeys = weaponKeys;
		}

		Boolean shouldRespectEntityCollisions = json.readValue("shouldRespectEntityCollisions", Boolean.class, jsonData);
		if (shouldRespectEntityCollisions != null) {
			this.shouldRespectEntityCollisions = shouldRespectEntityCollisions;
		}
		else {
			this.shouldRespectEntityCollisions = true;
		}
		
		Boolean shouldRespectObjectCollisions = json.readValue("shouldRespectObjectCollisions", Boolean.class, jsonData);
		if (shouldRespectObjectCollisions != null) {
			this.shouldRespectObjectCollisions = shouldRespectObjectCollisions;
		}
		else {
			this.shouldRespectObjectCollisions = true;
		}
		
		Boolean shouldRespectTileCollisions = json.readValue("shouldRespectTileCollisions", Boolean.class, jsonData);
		if (shouldRespectTileCollisions != null) {
			this.shouldRespectTileCollisions = shouldRespectTileCollisions;
		}
		else {
			this.shouldRespectTileCollisions = true;
		}
		
		Float horizontalSpeed = json.readValue("horizontalSpeed", Float.class, jsonData);
		if (horizontalSpeed != null) {
			this.horizontalSpeed = horizontalSpeed;
		}
		else {
			this.horizontalSpeed = 150f;
		}
		
		Float sprintSpeed = json.readValue("sprintSpeed", Float.class, jsonData);
		if (horizontalSpeed != null) {
			this.sprintSpeed = sprintSpeed;
		}
		else {
			this.sprintSpeed = 1200f;
		}
		
		Float horizontalAcceleration = json.readValue("horizontalAcceleration", Float.class, jsonData);
		if (horizontalAcceleration != null) {
			this.horizontalAcceleration = horizontalAcceleration;
		}
		else {
			this.horizontalAcceleration = 800f;
		}
		
		Float jumpSpeed = json.readValue("jumpSpeed", Float.class, jsonData);
		if (jumpSpeed != null) {
			this.jumpSpeed = jumpSpeed;
		}
		else {
			this.jumpSpeed = 1300f;
		}
		
		Float gravity = json.readValue("gravity", Float.class, jsonData);
		if (gravity != null) {
			this.gravity = gravity;
		}
		else {
			this.gravity = 3500f;
		}
		
		Float injuryImmunityTime = json.readValue("injuryImmunityTime", Float.class, jsonData);
		if (injuryImmunityTime != null) {
			this.injuryImmunityTime = injuryImmunityTime;
		}
		else {
			this.injuryImmunityTime = 2f;
		}
		
		Float staggerAllowanceTime = json.readValue("staggerAllowanceTime", Float.class, jsonData);
		if (staggerAllowanceTime != null) {
			this.staggerAllowanceTime = staggerAllowanceTime;
		}
		else {
			this.staggerAllowanceTime = 5f;
		}
		
		Integer allegiance = json.readValue("allegiance", Integer.class, jsonData); 
		if (allegiance != null) {
			this.allegiance = allegiance;
		}
		
		Float widthCoefficient = json.readValue("collisionWidthCoefficient", Float.class, jsonData);
		if (widthCoefficient != null) {
			this.collisionWidthCoefficient = widthCoefficient;
		}
		else {
			this.collisionWidthCoefficient = 1f;
		}
		
		Float heightCoefficient = json.readValue("collisionHeightCoefficient", Float.class, jsonData);
		if (heightCoefficient != null) {
			this.collisionHeightCoefficient = heightCoefficient;
		}
		else {
			this.collisionHeightCoefficient = 1f;
		}
		
		Boolean useDefaultStagger = json.readValue("useDefaultStagger", Boolean.class, jsonData);
		if (useDefaultStagger != null) {
			this.useDefaultStagger = useDefaultStagger;
		}
		else {
			this.useDefaultStagger = true;
		}
		
		Boolean useDefaultAerialStagger = json.readValue("useDefaultAerialStagger", Boolean.class, jsonData);
		if (useDefaultAerialStagger != null) {
			this.useDefaultAerialStagger = useDefaultAerialStagger;
		}
		else {
			this.useDefaultAerialStagger = true;
		}
		
		Boolean useDefaultTensionStagger = json.readValue("useDefaultTensionStagger", Boolean.class, jsonData);
		if (useDefaultTensionStagger != null) {
			this.useDefaultTensionStagger = useDefaultStagger;
		}
		else {
			this.useDefaultTensionStagger = true;
		}
		
		Float xOffsetModifier = json.readValue("xCollisionOffsetModifier", Float.class, jsonData);
		if (xOffsetModifier != null) {
			this.xCollisionOffsetModifier = xOffsetModifier; 
		}
		else {
			this.xCollisionOffsetModifier = 0f;
		}
		
		Float yOffsetModifier = json.readValue("yCollisionOffsetModifier", Float.class, jsonData);
		if (yOffsetModifier != null) {
			this.yCollisionOffsetModifier = yOffsetModifier; 
		}
		else {
			this.yCollisionOffsetModifier = 0f;
		}
		
		this.sequencesSortedByInputSize = new Array <ActionSequence> ();
		for (ActionSequence sequence : this.actions.values()) {
			ActionSequence.addSequenceToSortedArray(sequencesSortedByInputSize, sequence);
		}
		
		Boolean useDefaultWakeup = json.readValue("useDefaultWakeup", Boolean.class, jsonData);
		if (useDefaultWakeup != null) {
			this.useDefaultWakeup = useDefaultWakeup;
		}
		else {
			this.useDefaultWakeup = true;
		}	
		
		Integer maxJumpTokens = json.readValue("maxJumpTokens", Integer.class, jsonData); 
		if (maxJumpTokens != null) {
			this.maxJumpTokens = maxJumpTokens;
		}
		else {
			this.maxJumpTokens = 2;
		}
			
		Boolean boltedDown = json.readValue("boltedDown", Boolean.class, jsonData);
		if (boltedDown != null) {
			this.boltedDown = boltedDown;
		}
		else {
			this.boltedDown = false;
		}

		Float xRotationCoefficient = json.readValue("xRotationCoefficient", Float.class, jsonData);
		if (xRotationCoefficient != null) {
			this.xRotationCoefficient = xRotationCoefficient; 
		}
		else {
			this.xRotationCoefficient = 0.5f;
		}
		
		Float yRotationCoefficient = json.readValue("yRotationCoefficient", Float.class, jsonData);
		if (yRotationCoefficient != null) {
			this.yRotationCoefficient = yRotationCoefficient; 
		}
		else {
			this.yRotationCoefficient = 0.5f;
		}
		
		defaultHurtboxProperties = json.readValue("defaultHurtboxProperties", Array.class, jsonData);
		if (defaultHurtboxProperties == null) {
			defaultHurtboxProperties = new Array <Rectangle>();
		}
		
		crouchingHurtboxProperties = json.readValue("crouchingHurtBoxProperties", Array.class, jsonData);
		if (crouchingHurtboxProperties == null) {
			crouchingHurtboxProperties = defaultHurtboxProperties;
		}
	}

	public CharacterProperties cloneProperties() {
		CharacterProperties properties = new CharacterProperties();
		properties.maxHealth = this.maxHealth;
		properties.maxWill = this.maxWill;
		properties.maxStability = this.maxStability;
		properties.attack = this.attack;
		properties.horizontalSpeed = this.horizontalSpeed;
		properties.jumpSpeed = this.jumpSpeed;
		properties.allegiance = this.allegiance;
		properties.gravity = this.gravity;
		properties.injuryImmunityTime = this.injuryImmunityTime;
		properties.collisionWidthCoefficient = this.collisionWidthCoefficient;
		properties.collisionHeightCoefficient = this.collisionHeightCoefficient;
		properties.horizontalAcceleration = this.horizontalAcceleration;
		properties.weaponKeys = this.weaponKeys;
		properties.maxTension = this.maxTension;
		properties.useDefaultStagger = this.useDefaultStagger;
		properties.useDefaultTensionStagger = this.useDefaultTensionStagger;
		properties.sprintSpeed = this.sprintSpeed;
		properties.shouldRespectEntityCollisions = this.shouldRespectEntityCollisions;
		properties.shouldRespectObjectCollisions = this.shouldRespectObjectCollisions;
		properties.shouldRespectTileCollisions = this.shouldRespectTileCollisions;
		properties.xCollisionOffsetModifier = this.xCollisionOffsetModifier;
		properties.yCollisionOffsetModifier = this.yCollisionOffsetModifier;
		properties.useDefaultAerialStagger = this.useDefaultAerialStagger;
		properties.useDefaultWakeup = this.useDefaultWakeup;
		properties.maxJumpTokens = this.maxJumpTokens;
		properties.boltedDown = this.boltedDown;
		properties.xRotationCoefficient = this.xRotationCoefficient;
		properties.yRotationCoefficient = this.yRotationCoefficient;
		properties.defaultHurtboxProperties = this.defaultHurtboxProperties;
		properties.crouchingHurtboxProperties = this.crouchingHurtboxProperties;
		//iterate through actions.
		HashMap <String, ActionSequence> clonedActions = new HashMap<String, ActionSequence> ();
		for (Map.Entry<String, ActionSequence> entry : actions.entrySet()) {
			clonedActions.put(entry.getKey(), entry.getValue().cloneBareSequence());
		}
		properties.setActions(clonedActions);
		properties.sequencesSortedByInputSize = new Array <ActionSequence>();
		for (ActionSequence sequence : clonedActions.values()) {
			ActionSequence.addSequenceToSortedArray(properties.sequencesSortedByInputSize, sequence);
		}
		properties.setSource(this.source);
//		Iterator<Map.Entry<String, ActionSequence>> iterator = actions.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Map.Entry<String, ActionSequence> pair = (Map.Entry<String, ActionSequence>) iterator.next();
//			clonedActions.put(pair.getKey(), pair.getValue().cloneSequence());
//		}
		return properties;
	}
	
	public void setSource (CharacterModel source) {
		this.source = source;
		for (ActionSequence action : actions.values()) {
			action.setSource(source);
		}
	}
	
	public ActionSequence getSequenceGivenInputs(Queue<String> inputs, CharacterModel source) {
		ActionSequence result = null;
		boolean isInAir = source.isInAir;
		for (ActionSequence sequence : this.sequencesSortedByInputSize) {
			if (sequence.doInputsMatch(inputs, source, false) && 
					(sequence.getUseType().equals(UseType.Either)
					|| (isInAir && sequence.getUseType().equals(UseType.Aerial))
					|| (!isInAir && sequence.getUseType().equals(UseType.Ground)))) {
				result = sequence;
				break;
			}
		}
		
		return result;
	}
	
	public float getHeightCoefficient() {
		return collisionHeightCoefficient;
	}

	public float getWidthCoefficient() {
		return collisionWidthCoefficient;
	}

	public Float getInjuryImmunityTime() {
		return injuryImmunityTime;
	}

	public Float getGravity() {
		return gravity;
	}

	public Float getHorizontalSpeed() {
		return horizontalSpeed;
	}

	public Float getSprintSpeed() {
		return sprintSpeed;
	}

	public Float getHorizontalAcceleration() {
		return horizontalAcceleration;
	}

	public Float getJumpSpeed() {
		return jumpSpeed;
	}

	public float getMaxStability() {
		return maxStability;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getMaxWill() {
		return maxWill;
	}

	public float getAttack() {
		return attack;
	}

	public HashMap<String, ActionSequence> getActions() {
		return actions;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setMaxWill(int maxWill) {
		this.maxWill = maxWill;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setActions(HashMap<String, ActionSequence> actions) {
		this.actions = actions;
	}

	public Integer getAllegiance() {
		return allegiance;
	}

	public Array <StringWrapper> getWeaponKeys() {
		return weaponKeys;
	}

	public float getMaxTension() {
		return maxTension;
	}

	public boolean useDefaultStagger() {
		return useDefaultStagger;
	}

	public boolean shouldRespectEntityCollisions() {
		return shouldRespectEntityCollisions;
	}

	public boolean shouldRespectTileCollisions() {
		return shouldRespectTileCollisions;
	}

	public boolean shouldRespectObjectCollisions() {
		return shouldRespectObjectCollisions;
	}

	public int getMaxJumpTokens() {
		return maxJumpTokens;
	}
	
	
}
