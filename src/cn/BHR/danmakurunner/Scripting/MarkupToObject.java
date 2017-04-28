package cn.BHR.danmakurunner.Scripting;

import cn.BHR.danmakurunner.Scripting.Enums.MarkupTokens;
import cn.BHR.danmakurunner.Scripting.MarkupClassDefs.*;

public class MarkupToObject {
	public static GameObject getObject(MarkupNode node)
	{
		GameObject obj = new GameObject();
		obj = putProperties(obj, node);
		return obj;
	}
	protected static GameObject putProperties(GameObject obj, MarkupNode node)
	{
		for (MarkupNode child : node.childs)
		{
			String[] kv = child.data.split(":|,");
			float[] values = new float[kv.length - 1];
			try {
				for (int i = 1; i < kv.length; i++) {
					values[i - 1] = Float.parseFloat(kv[i]);
				}
			} catch (Exception e) {
				if (!kv[0].equals("Components")) {
					
				}
			}
			switch (MarkupTokens.valueOf(kv[0]))
			{
			case Position:
				obj.Position.set(values[0], values[1]);
				break;
			case Speed:
				obj.Speed.set(values[0], 0);
				if (values.length > 1) obj.Speed.rotate(values[1]);
				break;
			case Acceleration:
				obj.Acceleration.set(values[0], 0);
				if (values.length > 1) obj.Acceleration.rotate(values[1]);
				break;
			case Zoom:
				obj.Zoom.x = values[0];
				if (values.length > 1) obj.Zoom.y = values[1];
				else values[1] = 1;
				break;
			case Components:
				for (int i = 1; i < kv.length; i++) {
					obj.ComponentNames.add(kv[i]);
				}
				break;
			default:
				break;
			}
		}
		return obj;
	}
	public static Bullet getBullet(MarkupNode node)
	{
		Bullet obj = new Bullet();
		putProperties(obj, node);
		for (MarkupNode child : node.childs)
		{
			String[] kv = child.data.split(":|,");
			switch (MarkupTokens.valueOf(kv[0]))
			{
			case Type:
				obj.Type = Integer.parseInt(kv[1]);
				break;
			default:
				break;
			}
		}
		return obj;
	}
	public static Emitter getEmitter(MarkupNode node)
	{
		Emitter emitter = new Emitter();
		for (MarkupNode child : node.childs)
		{
			String[] kv = child.data.split(":|,");
			switch (MarkupTokens.valueOf(kv[0])) {
			case Ways:
				emitter.Ways = Integer.parseInt(kv[1]);
				break;
			case Range:
				emitter.Range = Float.parseFloat(kv[1]);
				break;
			case Radius:
				emitter.Radius = Float.parseFloat(kv[1]);
				break;
			case Cycle:
				emitter.Cycle = Integer.parseInt(kv[1]);
				break;
			case Direction:
				emitter.Direction = Integer.parseInt(kv[1]);
				break;
			case BeginTick:
				emitter.BeginTick = Integer.parseInt(kv[1]);
				break;
			case Factory:
				emitter.factoryName = kv[1];
				String[] innerClass = kv[1].split("\\.");
				switch (MarkupTokens.valueOf(innerClass[0])) {
				case Bullet:
					GameObject.markupIntepreter.BulletTypes.put(innerClass[1], getBullet(child));
					break;

				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		return emitter;
	}
}
