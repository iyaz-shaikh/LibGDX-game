package com.mygdx.game.model.characters.player;


import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.constants.InputConverter;
import com.mygdx.game.constants.InputConverter.DirectionalInput;
import com.mygdx.game.constants.InputConverter.XDirectionalInput;
import com.mygdx.game.constants.InputConverter.YDirectionalInput;
import com.mygdx.game.constants.InputType;
import com.mygdx.game.constants.XBox360Pad;
import com.mygdx.game.model.actions.ActionSegment.ActionState;
import com.mygdx.game.model.actions.ActionSequence.StaggerType;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.CharacterConstants;
import com.mygdx.game.model.characters.EntityCollisionData;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.ModelListener;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.events.StatsInfoListener;
import com.mygdx.game.model.events.InteractableObject;
import com.mygdx.game.model.world.DialogueController;
import com.mygdx.game.model.world.SpawnPoint;
import com.mygdx.game.model.worldObjects.Item;

public class Player extends Character implements InputProcessor, ControllerListener {

	public static final String characterName = "Player";
	public static final String characterType = "Player";
	public static final int allegiance = 1;
	public static final String DialogueUUID = "Player";

	
	public Player() {
		super(characterName);
		setUpPlayer();
	}
	
	private void setUpPlayer() {
		loadSaveFile();
		setCharacterData(new PlayerModel(characterName, this.getCharacterUIData(), this));
		this.getCharacterData().setName(characterName);
	}
	
	private void loadSaveFile() {
		//Do stuff to load in spawn point + player info.
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return ((PlayerModel)getCharacterData()).handleKeyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return ((PlayerModel)getCharacterData()).handleKeyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		return ((PlayerModel)getCharacterData()).handleButtonDown(controller, buttonCode);

	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return ((PlayerModel)getCharacterData()).handleButtonUp(controller, buttonCode);
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return ((PlayerModel)getCharacterData()).handleAxisMoved(controller, axisCode, value);
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return ((PlayerModel)getCharacterData()).handlePovMoved(controller, povCode, value);
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class PlayerModel extends CharacterModel{
	
		final String blockState = "Block";		
		boolean dashing, canControl, isJumpPressed;
		XDirectionalInput currentlyHeldXDirection;
		YDirectionalInput currentlyHeldYDirection;
	    Array <String> processedCondtionalDialogueUUIDs;
	    SpawnPoint spawnPoint;
	    InteractableObject nearbyObject;
	    PlayerProperties playerProperties;
	    DialogueDatabase dialogues;
	    DialogueController dialogueController;
	    GameSave gameSave;
	    boolean isTalking;

		Queue <String> inputs;
		InputConverter inputConverter;
		StatsInfoListener infoListener;
		Item selectedItemType;

		public PlayerModel(String characterName, EntityUIModel uiModel, ModelListener modelListener) {
			super(characterName, uiModel, modelListener);
			Json json = new Json();
			GameSave gameSave;
			FileHandle fileHandle = Gdx.files.local("Saves/currentSave.json");
			if (fileHandle.exists()) {
				gameSave = json.fromJson(GameSave.class, fileHandle);
			}
			else {
				gameSave = GameSave.testSave();
			}
			this.gameSave = gameSave;
			playerProperties = json.fromJson(PlayerProperties.class, Gdx.files.internal("Json/Player/playerActions.json"));
			playerProperties.populateWith(gameSave);
			if (playerProperties.getQuickItems().size > 0) {
				selectedItemType = playerProperties.getQuickItems().get(0).item;
			}
			dialogues = json.fromJson(DialogueDatabase.class, Gdx.files.internal("Json/Player/dialogues.json"));
			dialogues.setSource(this);
			this.processedCondtionalDialogueUUIDs = new Array <String>();
			dashing = false;
			canControl = false;
			this.currentlyHeldXDirection = XDirectionalInput.NONE;
			this.currentlyHeldYDirection = YDirectionalInput.NONE;

			isJumpPressed = false;
//			this.widthCoefficient = 0.19f;
//			this.heightCoefficient = 0.6f;
			this.inputs = new Queue <String> ();
			this.inputConverter = new InputConverter(this.gameSave);
		}
		
		@Override
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			super.update(delta, collisionLayer);
			this.handleConditionalDialogues();
		}

		@Override
		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
			super.manageAutomaticStates(delta, collisionLayer);
			
			 if (dashing) {
				setState(CharacterConstants.idleState); //Dash
			}
		}
		
		private void handleConditionalDialogues() {
			if (dialogues != null) {
				for (ConditionalDialogueSettings dialogue : this.dialogues.conditionalDialogues)
				{
					if (!this.processedCondtionalDialogueUUIDs.contains(dialogue.getUUID(), true) && dialogue.conditionsMet()) {
						dialogueController.handleDialogue(dialogue);
						this.processedCondtionalDialogueUUIDs.add(dialogue.getUUID());
					}
				}
			}
		}
	    
		public boolean isUUIDInSave(Integer UUID) {
			return gameSave.isUUIDInSave(UUID);
		}

		public void addUUIDToSave(Integer UUID, UUIDType uuidType) {
			gameSave.addUUIDToSave(UUID, uuidType);
		}
		
		public void addItemToInventory(Item item) {
			this.getPlayerProperties().getInventory().add(item);
			//Eventually add HUD code to reflect receiving item.
		}
		
		public void removeFromInventory(Item item) {
			// TODO Auto-generated method stub
			this.getPlayerProperties().getInventory().removeValue(item, true);
			Iterator <ItemInfo> iterator = this.getPlayerProperties().getQuickItems().iterator();
			while (iterator.hasNext()) {
				ItemInfo itemInfo = iterator.next();
				if (itemInfo.item.equals(item) && item.expiresOnUse()) {
					itemInfo.numberInInventory -= 1;
					if (itemInfo.numberInInventory == 0) {
						iterator.remove();
						break;
					}
				}
			}
		}
		
		public void dialogueAction(String uuid) {
			ActionSequence sequence = ActionSequence.createSequenceWithDialog(this.dialogues.getDialogueForUUID(uuid), this, null, dialogueController, this.getActionListener());
			this.addActionSequence(sequence);
		}
	    
	    public void dash(boolean left) {
	    	if (!isInAir) {
	    		ActionSequence dashAction = this.getCharacterProperties().getActions().get("PlayerDash").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
	    		this.addActionSequence(dashAction);
	    	}
	    }
	    
	    public boolean isDodging() {
	        return dashing;
	    }
	    
	    private void checkToDisruptCurrentAct(String inputType) {
	    	if (this.getCurrentActiveActionSeq() != null) {
		    	boolean needsInterruptForWindup = this.getCurrentActiveActionSeq().getAction().doesNeedDisruptionDuringWindup() && this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.WINDUP);
		    	boolean needsInterruptForAction = this.getCurrentActiveActionSeq().getAction().doesNeedDisruptionDuringActive() && this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.ACTIVE);
		    	
		    	if (needsInterruptForWindup || needsInterruptForAction) {
		    		Queue <String> inputs = new Queue <String>();
			    	inputs.addFirst(inputType);
			    	if (this.getCurrentActiveActionSeq().doInputsMatch(inputs, this, true)) {
			    		if (needsInterruptForWindup) {
			    			this.forceActiveForActiveAction();
			    		}
			    		else if (needsInterruptForAction) {
			    			this.forceCooldownForActiveAction();
			    		}
			    	}
		    	}
	    	}
	    	
	    }
	    
	    private boolean queueUpActionFromInputs() {
	    	ActionSequence sequence = null;
	    	//Check char props first.
	    	sequence = this.getCharacterProperties().getSequenceGivenInputs(this.inputs, this);
	    	if (sequence != null) {
		    	this.stopHorizontalMovement(true);
	    		this.addActionSequence(sequence.cloneSequenceWithSourceAndTarget(this, null, getActionListener(), this.getCollisionChecker()));
	    		return true;
	    	}
	    	//Check weapon next.
	    	sequence = this.getCurrentWeapon().getSpecificWeaponAction(this.inputs, this);
	    	if (sequence != null) {
		    	this.stopHorizontalMovement(true);
	    		this.addActionSequence(sequence.cloneSequenceWithSourceAndTarget(this, null, getActionListener(), this.getCollisionChecker()));
	    		return true;
	    	}
	    	
	    	return false;
	    }
	    
	    private void useItem() {
	    	this.selectedItemType.use(this);
	    }
	      
		private void setDirectionFromDirectionalInput(DirectionalInput input) {
			switch (input) {
			case LEFT:
				this.currentlyHeldXDirection = XDirectionalInput.LEFT;
				break;
			case RIGHT:
				this.currentlyHeldXDirection = XDirectionalInput.RIGHT;
				break;
			case UP:
				this.currentlyHeldYDirection = YDirectionalInput.UP;
				break;
			case DOWN:
				this.currentlyHeldYDirection = YDirectionalInput.DOWN;
				break;
			case NONE:
				this.currentlyHeldXDirection = XDirectionalInput.NONE;
				this.currentlyHeldYDirection = YDirectionalInput.NONE;
				break;
			default:
				break;
			}
		}
		    
		public boolean handleKeyDown (int keyCode)
		{
			DirectionalInput potentialDirectionalInput = this.inputConverter.getDirectionFromKeyCodeForDown(keyCode);
			
			if (!potentialDirectionalInput.equals(DirectionalInput.NONE))
				setDirectionFromDirectionalInput(potentialDirectionalInput);
			
			DirectionalInput actualDirectionalInput = DirectionalInput.getDirectionFromXAndY(this.currentlyHeldXDirection, this.currentlyHeldYDirection);
			
			String inputType = this.inputConverter.convertKeyCodeToInputType(keyCode, actualDirectionalInput);
			if (!inputType.equals("")) {
				System.out.println("Key DOWN: " + inputType);
				this.inputs.addFirst(inputType);
				if (this.queueUpActionFromInputs())
				{
					return true;
				}
				switch (inputType) {
				case InputType.LEFT:
				case InputType.UPLEFT:
					horizontalMove(true);
					break;
				case InputType.RIGHT:
				case InputType.UPRIGHT:
					horizontalMove(false);
					break;
				case InputType.DOWNRIGHT:
				case InputType.DOWNLEFT:
				case InputType.DOWN:
					this.crouch();
					break;
				case InputType.JUMP:
				case InputType.JUMPRIGHT:
				case InputType.JUMPLEFT:
				case InputType.JUMPUP:
				case InputType.JUMPUPRIGHT:
				case InputType.JUMPUPLEFT:
					if (!actOnObject())
						jump();
					break;
				case InputType.JUMPDOWN: 
				case InputType.JUMPDOWNLEFT:
				case InputType.JUMPDOWNRIGHT: 
					downJump();
					break;
				case InputType.USEITEM:
					this.useItem();
					break;
				case InputType.DASH:
				case InputType.DASHLEFT:
				case InputType.DASHUPLEFT:
				case InputType.DASHUPRIGHT:
				case InputType.DASHUP:
				case InputType.DASHDOWNRIGHT:
				case InputType.DASHDOWNLEFT:
				case InputType.DASHDOWN:
				case InputType.DASHRIGHT:
					this.movementConditionActivated = true;
					break;
				default:
					break;
				}
			}

			
			return true;
		}
		
		public boolean handleKeyUp (int keyCode) {
			//This is used to see what direction the character should be facing after key up.
			//we need to reset currentlyHeldX and Y directions, right now x is being set.
			String inputType = this.inputConverter.convertKeyCodeToInputType(keyCode, this.getCurrentlyHeldDirection());
			DirectionalInput directionHeld = this.inputConverter.getDirectionFromKeyCodeForUp(keyCode, this.currentlyHeldXDirection, this.currentlyHeldYDirection);
			if (directionHeld != null) {
				this.currentlyHeldXDirection = directionHeld.getXDirectionalInputKey();
				this.currentlyHeldYDirection = directionHeld.getYDirectionalInput();
				checkIfNeedToStopWalk();
			}
//			String inputType = this.inputConverter.convertKeyCodeToInputType(keyCode, this.getCurrentlyHeldDirection());
			System.out.println("Key UP: " + inputType);
			System.out.println("Key UP direction: " + directionHeld);
			if (!inputType.equals("")) {
				this.checkToDisruptCurrentAct(inputType);
				switch (inputType) {
				case InputType.USEITEM:
//					unlockDirection();
					break;
				case InputType.DASH:
				case InputType.DASHLEFT:
				case InputType.DASHUPLEFT:
				case InputType.DASHUPRIGHT:
				case InputType.DASHUP:
				case InputType.DASHDOWNRIGHT:
				case InputType.DASHDOWNLEFT:
				case InputType.DASHDOWN:
				case InputType.DASHRIGHT:
					this.movementConditionActivated = false;
					break;
				case InputType.DOWNLEFT:
				case InputType.DOWNRIGHT:
				case InputType.DOWN:
					if (!this.currentlyHeldYDirection.equals(YDirectionalInput.DOWN))
						this.unCrouch(); //this doesn't work, keyUp right triggers DownRight
					break;
				default:
					break;
				}
			}


			return true;
		}
		
		public boolean handleButtonDown(Controller controller, int buttonCode) {
			String inputType = this.inputConverter.convertButtonCodeToInputType(buttonCode, this.getCurrentlyHeldDirection());
			if (!inputType.equals("")) {
				System.out.println(inputType);
				this.inputs.addFirst(inputType);
				if (this.queueUpActionFromInputs())
				{
					return true;
				}
				switch (inputType) {
				case InputType.JUMP:
				case InputType.JUMPRIGHT:
				case InputType.JUMPLEFT:
				case InputType.JUMPUP:
				case InputType.JUMPUPRIGHT:
				case InputType.JUMPUPLEFT:
					if (!actOnObject())
						jump();
					break;
				case InputType.JUMPDOWN: 
				case InputType.JUMPDOWNLEFT:
				case InputType.JUMPDOWNRIGHT: 
					downJump();
					break;
				case InputType.USEITEM:
					this.useItem();
					break;
				default:
					break;
				}
			}
			
			
			return true;
		}
		
		public boolean handleButtonUp(Controller controller, int buttonCode) {
			String inputType = this.inputConverter.convertButtonCodeToInputType(buttonCode, this.getCurrentlyHeldDirection());
			if (!inputType.equals("")) {
				this.checkToDisruptCurrentAct(inputType);
			}

			return true;
		}
		
		public boolean handleAxisMoved(Controller controller, int axisCode, float value) {
			if (axisCode == XBox360Pad.AXIS_LEFT_X || axisCode == XBox360Pad.AXIS_LEFT_Y) {
				DirectionalInput potentialDirectionalInput = this.inputConverter.getDirectionFromAxisCode(axisCode, value);
//				System.out.println(axisCode == XBox360Pad.AXIS_LEFT_X ? "X "  + potentialDirectionalInput.toString() : "Y " + potentialDirectionalInput.toString());
				this.setHeldDirectionForAxis(axisCode, potentialDirectionalInput);
				switch (this.getCurrentlyHeldDirection()) {
					case LEFT:
					case UPLEFT:
						if (axisCode == XBox360Pad.AXIS_LEFT_X)
							horizontalMove(true);
						break;
					case RIGHT:
					case UPRIGHT:
						if (axisCode == XBox360Pad.AXIS_LEFT_X)
							horizontalMove(false);
						break;
					case DOWNRIGHT:
					case DOWNLEFT:
					case DOWN:
						this.crouch();
						break;
					case NONE:
						if (axisCode == XBox360Pad.AXIS_LEFT_X)
							this.checkIfNeedToStopWalk();
					default:
						break;
					}
			}
			else if (axisCode == XBox360Pad.AXIS_LEFT_TRIGGER) {
				String inputType = this.inputConverter.convertAxisTriggerToInputType(axisCode, value, this.getCurrentlyHeldDirection());
				if (inputType != null) {
					System.out.println(inputType);
					this.inputs.addFirst(inputType);
					this.queueUpActionFromInputs();
					switch (inputType) {
					case InputType.DASH:
					case InputType.DASHLEFT:
					case InputType.DASHUPLEFT:
					case InputType.DASHUPRIGHT:
					case InputType.DASHUP:
					case InputType.DASHDOWNRIGHT:
					case InputType.DASHDOWNLEFT:
					case InputType.DASHDOWN:
					case InputType.DASHRIGHT:
						this.movementConditionActivated = true;
//						System.out.println("Movement pressed: " + this.movementConditionActivated);
						break;
					case InputType.DASHRELEASE:
						this.movementConditionActivated = false;
//						System.out.println("Movement pressed: " + this.movementConditionActivated);
						break;
					}
				}
			}
			return true;
		}
		
		private void setHeldDirectionForAxis(int axisCode, DirectionalInput potentialDirectionalInput) {
//			if ((axisCode == XBox360Pad.AXIS_LEFT_Y && !(this.getCurrentlyHeldDirection().equals(DirectionalInput.LEFT) || this.getCurrentlyHeldDirection().equals(DirectionalInput.RIGHT))) 
//			  || axisCode == XBox360Pad.AXIS_LEFT_X && !(this.getCurrentlyHeldDirection().equals(DirectionalInput.UP) || this.currentlyHeldDirection().equals(DirectionalInput.DOWN))) {
//				
//				if (this.currentlyHeldDirection.equals(DirectionalInput.LEFT)) {
//					
//				}
//				this.currentlyHeldDirection = potentialDirectionalInput;
//			}
			if (axisCode == XBox360Pad.AXIS_LEFT_X) {
				XDirectionalInput xInput = potentialDirectionalInput.getXDirectionalInputAxis();
				if (xInput != null) {
					this.currentlyHeldXDirection = xInput;
				}
			}
			else if (axisCode == XBox360Pad.AXIS_LEFT_Y) {
				YDirectionalInput yInput = potentialDirectionalInput.getYDirectionalInput();
				if (yInput != null) {
					this.currentlyHeldYDirection = yInput;
				}
			}
		}
		
		public boolean handlePovMoved(Controller controller, int povCode, PovDirection value) {
			//Handle item switching stuff here
			return true;
		}
		
		private void checkIfNeedToStopWalk() {
			if (!this.currentlyHeldXDirection.equals(XDirectionalInput.LEFT) && !this.currentlyHeldXDirection.equals(XDirectionalInput.RIGHT)) {
				this.stopHorizontalMovement(true);
			}
		}
		

		private boolean actOnObject() {
			if (nearbyObject != null && nearbyObject.canBeActedOn()) {
				this.nearbyObject.actOnThis(this);
				nearbyObject = null;
				return true;
			}
			return false;
		}
		


		@Override
		public int getAllegiance() {
			return Player.allegiance;
		}

		public void setNearbyObject(InteractableObject nearbyObject) {
			this.nearbyObject = nearbyObject;
		}
		


		public PlayerProperties getPlayerProperties() {
			return playerProperties;
		}

		public GameSave getGameSave() {
			return gameSave;
		}
		
		public boolean isTalking() {
			return isTalking;
		}


		public void setTalking(boolean isTalking) {
			this.isTalking = isTalking;
		}


		public DialogueDatabase getDialogues() {
			return dialogues;
		}


		@Override
		public EntityCollisionData handleEntityXCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
			if (alreadyCollided) {
				if (this.walking) {
					this.stopHorizontalMovement(false);
				}
				return null;
			}
			else if (this.isRespectingEntityCollision()){
				EntityCollisionData entityCollisionData = this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
				boolean entityCollision = entityCollisionData != null;
				if (entityCollision) {
					this.stopHorizontalMovement(false);
				}
//				if (!this.hasProcessedOverlapCorrection()) {
//					this.stopEntityOverlapIfNeeded(collidedEntity, tempGameplayBounds, tempImageBounds);
//				}
				return entityCollisionData;
			}
			
			return null;
		}
		
		@Override
		public EntityCollisionData handleEntityYCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
			if (alreadyCollided) {
				return null;
			}
			else if (this.isRespectingEntityCollision()){ 
				return this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
			}
			return null;
		}

		public void setDialogueController(DialogueController dialogueController) {
			this.dialogueController = dialogueController;
		}

		@Override
		public void setPatrolInfo(Array<Float> wayPoints, float patrolDuration, float breakDuration) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void patrolWalk(boolean left) {
			
		}

		@Override
		public Direction isTryingToMoveHorizontally() {
			if (this.currentlyHeldXDirection.equals(XDirectionalInput.LEFT)) {
				return Direction.LEFT;
			}
			else if (this.currentlyHeldXDirection.equals(XDirectionalInput.RIGHT)) {
				return Direction.RIGHT;
			}
			return Direction.NaN;
		}

		@Override
		public void tensionOverload() {
			ActionSequence staggerAction = ActionSequence.createStaggerSequence(this, this.getActionListener(), StaggerType.Tension);
    		this.addActionSequence(staggerAction);
    		this.setCurrentTension(0);
		}


		public void setInfoListener (StatsInfoListener infoListener) {
			this.infoListener = infoListener;
		}

		public Item getSelectedItemType() {
			return selectedItemType;
		}
		
		public int getNumberOfItemsForSelected() {
			int numberOfItems = 0;
			for (Item item : this.getPlayerProperties().inventory) {
				if (this.selectedItemType.equals(item)) {
					numberOfItems += 1;
				}
			}
			return numberOfItems;
		}
		
		public boolean hasItem(String itemName) {
			for (Item item : this.playerProperties.getInventory()) {
				if (item.getName().equals(itemName)) {
					return true;
				}
			}
			return false;
		}

		public DirectionalInput getCurrentlyHeldDirection() {
			return DirectionalInput.getDirectionFromXAndY(this.currentlyHeldXDirection, this.currentlyHeldYDirection);
		}
	}
}
