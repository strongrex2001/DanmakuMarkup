package cn.BHR.danmakurunner.Scripting.MarkupClassDefs;

import java.util.*;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;

import cn.BHR.danmakurunner.Scripting.MarkupIntepreter;
import cn.BHR.danmakurunner.Scripting.Enums.PositionMode;

public class GameObject {
	public ArrayList<Component> Components = new ArrayList<Component>();
	public ArrayList<String> ComponentNames = new ArrayList<String>();
	public Vector2 Speed = new Vector2();
	public Vector2 Acceleration = new Vector2();
	public Vector2 Position = new Vector2();
	public int existTime = 0;
	public float Direction = 0;
	public int LifeTime = -1;
	public boolean Died = true;
	public Texture DrawTexture = null;
	public Vector2 Zoom = new Vector2(1, 1);
	protected int parent = -1;
	public static MarkupIntepreter markupIntepreter = new MarkupIntepreter();
	public static final Rectangle FIGHTAREA= new Rectangle(0, 0, 540, 540);
	static boolean hasEmittedWarning = false;
	public PositionMode getPositionMode()
	{
		return parent == -1 ? PositionMode.BasedOnScreen : PositionMode.BasedOnParent;
	}
	public void Update()
	{
		if (Died) {
			return;
		}
		if (LifeTime > 0) {
			LifeTime--;
			if (LifeTime == 0) Died = true;
		}
		existTime++;
		Speed.add(Acceleration);
		for (Component component : Components) {
			component.Update(this);
		}
		Position.add(Speed);
	}
	public static void UpdateAll()
	{
		hasEmittedWarning = false;
		for (GameObject go : GameObject.markupIntepreter.FirstLayerObjects) {
			go.Update();
		}
		for (int i = 0; i < Bullet.Pool.length; i++) {
			Bullet.Pool[i].Update();
		}
	}
	public static void ResetAll()
	{
		for (int i = 0; i < Bullet.Pool.length; i++) {
			Bullet.Pool[i].Loot();
		}
	}
}
