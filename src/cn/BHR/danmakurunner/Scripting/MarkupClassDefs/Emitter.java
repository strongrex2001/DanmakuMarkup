package cn.BHR.danmakurunner.Scripting.MarkupClassDefs;

import com.badlogic.gdx.math.MathUtils;


public class Emitter extends Component {
	public int Ways;
	public float Range;
	public float Direction;
	public int Cycle;
	public int BeginTick;
	public float Radius;
	public Factory Factory;
	public String factoryName = null;
	
	private float _step, _angle;
	private int _count;
	@Override
	public void Update(GameObject parent) {
		if (parent.existTime < BeginTick) return;
		if ((parent.existTime - BeginTick) % Cycle == 0) {
			_step = Range / Ways;
			_count = 0;
			_angle = Direction - Range / 2 + parent.Direction;
			while (_count < Ways) {
				GameObject gameObject = Factory.Create();
				if (gameObject == null) {
					if (!GameObject.hasEmittedWarning)
					{
						//DanmakuInterface.Caller.NewText("TOO MANY BULLETS!");
						GameObject.hasEmittedWarning = true;
					}
					return;
				}
				gameObject.Position.set(
						parent.Position.x + 
						MathUtils.cosDeg(_angle) * Radius, 
						parent.Position.y + 
						MathUtils.sinDeg(_angle) * Radius);
				gameObject.Speed.rotate(_angle);
				gameObject.Died = false;
				_count++;
				_angle += _step;
			}
		}
	}
}
