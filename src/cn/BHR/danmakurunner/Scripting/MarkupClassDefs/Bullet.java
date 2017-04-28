package cn.BHR.danmakurunner.Scripting.MarkupClassDefs;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends GameObject {
	protected static Bullet[] Pool = new Bullet[1792];
	public static Bullet Malloc()
	{
		//synchronized(Pool) {
			for (int i = 0; i < Pool.length; i++) {
				if (Pool[i].Died) {
					return Pool[i];
				}
			}
		//}
		return null;
	}
	public static Bullet Malloc(Bullet bulletType)
	{
		if (bulletType == null) {
			return Malloc();
		}
		//synchronized (Pool) {
			for (int i = 0; i < Pool.length; i++) {
				if (Pool[i].Died) {
					Bullet now = Pool[i];
					now.existTime = 0;
					now.Type = bulletType.Type;
					now.Zoom.set(bulletType.Zoom);
					now.Acceleration.set(bulletType.Acceleration);
					now.Speed.set(bulletType.Speed);
					now.Direction = bulletType.Direction;
					now.DrawTexture = bulletType.DrawTexture;
					now.LifeTime = bulletType.LifeTime;
					now.Components.clear();
					for (Component c : bulletType.Components) {
						now.Components.add(c);
					}
					return now;
				}
			}
		//}
		return null;
	}
	static
	{
		for (int i = 0; i < Pool.length; i++) {
			Pool[i] = new Bullet();
		}
	}

	protected Rectangle judgeOutOfScreen = new Rectangle();
	protected Vector2 absPos = new Vector2();
	public int Type = 0;
	public void Update()
	{
		if (Died) return;
		existTime++;
		if (LifeTime > 0) {
			LifeTime--;
			if (LifeTime == 0) Loot();
		}
		judgeOutOfScreen.setSize(DrawTexture.getWidth(), DrawTexture.getHeight()).setCenter(Position);
		if (!judgeOutOfScreen.overlaps(FIGHTAREA)) {
			Loot();
		}
		
		for (Component component : Components) {
			if (component != null)
				component.Update(this);
		}
		
		Speed.add(Acceleration);
		Position.add(Speed);
		if (parent == -1) {
			absPos.set(Position);
		}
		else absPos.set(Position).add(Bullet.Pool[parent].absPos);
	}
	public void Loot()
	{
		Zoom.set(1, 1);
		Acceleration.set(0, 0);
		Speed.set(1, 0);
		Died = true;
	}
}
