package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.assets.HitSparkUtils;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.InteractableObject;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.hitSpark.HitSpark;
import com.mygdx.game.model.hitSpark.HitSparkData;
import com.mygdx.game.model.hitSpark.HitSparkListener;
import com.mygdx.game.model.world.WorldModel;

public abstract class WorldObject extends EntityModel implements InteractableObject{
	
	final String deactivatedState = "Deactivated";
	final String activatingState = "Activating";
	final String activatedState = "Activated";
	public static final String WorldObjUUIDKey = "UUID";
	private final String WorldObjUIKey = "UIKey";
	final String isStrikeableKey = "IsStrikeable";
	final String maxHealthKey = "maxHealth";
	static float defaultGravity = 300f;
	public static int allegiance = 0;
	boolean isStrikeable;
	float maxHealth, currentHealth;
	
	EntityUIModel itemUIModel;
	Integer uuid;
	boolean isActivated, isFacingLeft, didChangeState;
	String state;
	SaveListener saveListener;
	ObjectListener objListener;
	
	public WorldObject(String typeOfObject, MapProperties properties, ObjectListener objListener, SaveListener saveListener) {
		super();
		isFacingLeft = false;
		didChangeState = false;
		itemUIModel = new EntityUIModel((String) properties.get(WorldObjUIKey), EntityUIDataType.WORLDOBJECT);
		if (properties.get(WorldObjUUIDKey) != null) {
			this.uuid = (Integer) properties.get(WorldObjUUIDKey);
		}
		if (properties.get(this.maxHealthKey)!= null) {
			this.isStrikeable = true;
			this.maxHealth = (Float) properties.get(this.maxHealthKey);
			this.currentHealth = maxHealth;
		}
		else {
			this.isStrikeable = false;
			this.maxHealth = 0f;
			this.currentHealth = 0f;
		}
		this.setObjListener(objListener);
		this.saveListener = saveListener;
		this.acceleration.y = -defaultGravity;
		this.gameplayCollisionBox = this.imageBounds;
		state = deactivatedState;
	}
	
	public static boolean shouldAddIfActivated(String typeOfObject) {
		switch (typeOfObject) {
		case "WorldItem":
			return false;
		case "WorldLever":
		case "WorldGate":
			return true;
		}
		return false;
	}
	public abstract boolean shouldDeleteIfActivated();
	public abstract boolean shouldMove();
	public abstract boolean shouldCollideWithEntity();
	public abstract UUIDType getUUIDType();
	
	public void shouldAttackHit(Attack attack, HitSparkListener listener, Rectangle collidingHitBox) {
		if (this.isStrikeable && !this.isActivated) {
			attack.processAttackOnEntity(this);
			HitSparkData hitSparkData = HitSparkUtils.blockData(attack.getAttackSettings().getHitSparkData().getSize());
			HitSpark hitSpark = new HitSpark(hitSparkData, 
					collidingHitBox.x,
					collidingHitBox.y,
					listener);
			if (hitSpark != null) {
				listener.addHitSpark(hitSpark);
			}
		}
	}
	
	public boolean isStrikeable() {
		return this.isStrikeable;
	}
	
	public void activateObjectOnWorld(WorldModel world) {
		this.setState(this.activatingState);
		if (shouldDeleteIfActivated()) {
			this.getObjListener().deleteObjectFromWorld(this);
		}
		if (this.uuid != null) {
			saveListener.addUUIDToSave(this.uuid, this.getUUIDType());
			saveListener.triggerSave();
		}
	}
	
	public void activateAlreadyActivatedObject(WorldModel world) {
		this.setState(this.activatedState);
		this.isActivated = true;
		if (shouldDeleteIfActivated()) {
			this.getObjListener().deleteObjectFromWorld(this);
		}
	}
	
	public void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		if (this.shouldMove()) {
			CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, this.acceleration.x, true);
			if (collisionX.doesCollide()) {
				this.getVelocity().x = 0;
				this.getAcceleration().x = 0;
			}
			CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true, true);
			if (collisionY.doesCollide()) {
				this.getVelocity().y = 0;
			}
		}
	}
	
	public void update(float delta, TiledMapTileLayer collisionLayer) {
		this.handleCollisionRespectChecks();
		this.setGameplayCollisionSize(delta);
		this.movementWithCollisionDetection(delta, collisionLayer);
		this.handleEffects(delta);
		if (this.didChangeState) {
			this.itemUIModel.setAnimationTime(0f);
			this.didChangeState = false; 
		}
		boolean isFinishedWithAnimation = this.itemUIModel.setCurrentFrame(this, delta);
		if (this.state.equals(this.activatingState) && isFinishedWithAnimation) {
			this.setState(this.activatedState);
			this.isActivated = true;
		}
		if (this.currentHealth == 0 && this.isStrikeable && !this.isActivated) {
			this.objListener.objectToActOn(this);
		}
	}
	
	public void addToCurrentHealth(float healing) {
		this.setCurrentHealth(this.currentHealth + healing);

	}

	public void removeFromCurrentHealth(float removal) {
		this.setCurrentHealth(this.currentHealth - removal);
	}
	
	public void setCurrentHealth (float value) {
		this.currentHealth = Math.min(Math.max(0, value), this.maxHealth);
	}
	
	
//	@Override
//	public boolean equals(Object other){ 
//		if (other instanceof WorldObject) {
//			if ((((WorldObject) other).getUuid() == null && this.getUuid() != null) 
//					|| (((WorldObject) other).getUuid() != null && this.getUuid() == null)
//					|| ((WorldObject) other).getUuid() == null && this.getUuid() == null) {
//				return super.equals(other);
//			}
//			else {
//				return ((WorldObject) other).getUuid().equals(this.uuid);
//			}
//		}
//		return super.equals(other);
//	}
	
	public Integer getUuid() {
		return uuid;
	}

	public EntityUIModel getItemUIModel() {
		return itemUIModel;
	}

	public String getState() {
		return state;
	}

	public boolean isFacingLeft() {
		return isFacingLeft;
	}
	
	public boolean isActivated() {
		return isActivated;
	}

	public void setState(String state) {
		if (!this.state.equals(state)) {
			this.state = state;
			this.didChangeState = true;
		}
	}

	public ObjectListener getObjListener() {
		return objListener;
	}

	public void setObjListener(ObjectListener objListener) {
		this.objListener = objListener;
	}
	
	@Override
	public int getAllegiance() {
		return WorldObject.allegiance;
	}
	
	@Override 
	public void actOnThis(PlayerModel player) {
		if (canBeActedOn()) {
			this.objListener.objectToActOn(this);
		}
	}
	
	public boolean canBeActedOn() {
		return !this.isStrikeable && !this.isActivated;
	}

}
