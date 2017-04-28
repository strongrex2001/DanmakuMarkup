package cn.BHR.danmakurunner.Scripting;
import java.io.FileInputStream;
import java.util.*;

import cn.BHR.danmakurunner.Scripting.Enums.MarkupTokens;
import cn.BHR.danmakurunner.Scripting.MarkupClassDefs.*;

//import android.util.Log;

public class MarkupIntepreter
{
	public ArrayList<GameObject> FirstLayerObjects = new ArrayList<GameObject>();
	public Hashtable<String, Emitter> Emitters = new Hashtable<String, Emitter>();
	public Hashtable<String, Bullet> BulletTypes = new Hashtable<String, Bullet>();
	public static String toParse = "Emitter.emitter1 {"+"Ways:5;"+"Direction:0;"+"Range:360;"+"Radius:0;"+"Cycle:90;"+"BeginTick:100;"+"Factory: Bullet.bullet1 {"+"         Speed:2;"+"         Type:1;"+"    Components:Emitter.emitter1;};"+"}"+"GameObject {"+"	Position: 270, 270;"+"	Speed: 0, 0;"+"  "+"	Components:Emitter.emitter1;"+"}";
	
	public static void main(String[] args)
	{
		//String toParse = "1";
		MarkupIntepreter intepreter = GameObject.markupIntepreter;
		StringBuffer testMarkup = new StringBuffer();
		try {
			FileInputStream fileInputStream = new FileInputStream("D:/1.dmm");
			while (fileInputStream.available() > 0) {
				testMarkup.append((char)fileInputStream.read());
			}
			fileInputStream.close();
			intepreter.Run(testMarkup.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (MarkupNode node : intepreter.root.childs) {
			System.out.print(node.data + " ");
		}
		System.out.println("");
		for (MarkupNode node : intepreter.root.childs) {
			for (MarkupNode child : node.childs) {
				System.out.print(child.data);
				System.out.print("{");
				for (MarkupNode child2 : child.childs) {
					System.out.print(child2.data + " ");
					System.out.print("{");
					for (MarkupNode child3 : child2.childs) {
						System.out.print(child3.data + " ");
						}
					System.out.print("} ");
				}
				System.out.print("} ");
			}
			System.out.print("###");
		}
		System.out.println("");System.out.println("");
	}
	protected MarkupNode root = new MarkupNode();
	protected String Prepare(String markup)
	{
		markup = markup.replace("\n", "");
		markup = markup.replace("\r", "");
		markup = markup.replace(" ", "");
		markup = markup.replace("\t", "");
		char[] markupChars = markup.toCharArray();
		boolean flagAppend = true;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < markupChars.length - 1; i++) {
			if (markupChars[i] == '/' && markupChars[i+1] == '*') {
				flagAppend = false;
				i++;
			}
			if (flagAppend) {
				builder.append(markupChars[i]);
			}
			if (markupChars[i] == '*' && markupChars[i+1] == '/') {
				flagAppend = true;
				i++;
			}
		}
		if (flagAppend) {
			builder.append(markupChars[markupChars.length-1]);
		}
		markup = builder.toString();
		System.out.println(markup);
		return markup;
	}
	public void Run(String markup)
	{
		root = new MarkupNode();
		Intepret(Prepare(markup));
	}
	protected void Intepret(String markup)
	{
		AnalyzeToTree(markup);
		TreeToObjects();
		ProcessObjects();
	}
	protected void ProcessObjects()
	{
		for (Emitter emitter : Emitters.values()) {
			String[] token = emitter.factoryName.split("\\.");
			Bullet toReturn = new Bullet();
			switch (MarkupTokens.valueOf(token[0])) {
			case Bullet:
				toReturn = BulletTypes.get(token[1]);
				break;

			default:
				break;
			}
			final Bullet finalObj = toReturn;
			emitter.Factory = new Factory() {
				@Override
				public GameObject Create() {
					return Bullet.Malloc(finalObj);
				}
			};
		}
		for (GameObject gameObject : FirstLayerObjects) {
			ProcessObject(gameObject);
		}
		for (Bullet bullet : BulletTypes.values()) {
			ProcessObject(bullet);
		}
	}
	protected void ProcessObject(GameObject object)
	{
		for (String string : object.ComponentNames) {
			String[] token = string.split("\\.");
			switch (MarkupTokens.valueOf(token[0])) {
			case Emitter:
				object.Components.add(Emitters.get(token[1]));
				break;

			default:
				break;
			}
		}
		object.Died = false;
	}
	protected void TreeToObjects()
	{
		//First Layer
		for (MarkupNode node : root.childs) {
			String[] objectToken = node.data.split("\\.");
			switch (MarkupTokens.valueOf(objectToken[0])) {
			case GameObject:
				FirstLayerObjects.add(MarkupToObject.getObject(node));
				break;
				
			case Emitter:
				Emitters.put(objectToken[1], MarkupToObject.getEmitter(node));
				break;
				
			case Bullet:
				BulletTypes.put(objectToken[1], MarkupToObject.getBullet(node));
				break;

			default:
				break;
			}
		}
	}
	protected void AnalyzeToTree(String markup)
	{
		//First Layer
		int nowpt = 0;
		while(nowpt < markup.length())
		{
			int nextBrace = markup.indexOf('{', nowpt);
			if (nextBrace == -1) break;
			String first = markup.substring(nowpt, nextBrace);
			nowpt += first.length();
			int closingBrace = findCloseBrace(markup, nowpt);
			String contained = markup.substring(nowpt + 1, closingBrace);
			nowpt = closingBrace + 1;
			MarkupNode firstLayerNode = new MarkupNode(first);
			firstLayerNode.dataToParse = contained;
			root.childs.add(firstLayerNode);
		}
		
		//Other Layers
		for (int i = 0; i < root.childs.size(); i++) {
			MarkupNode now = root.childs.get(i);
			ParseTreeNode(now);
		}
	}
	protected void ParseTreeNode(MarkupNode node)
	{
		if (node.dataToParse == null || node.dataToParse.isEmpty()) {
			return;
		}
		char[] dataToParse = node.dataToParse.toCharArray();
		ArrayList<String> splited = new ArrayList<String>();
		int lastPlace = 0;
		for (int i = 0; i < dataToParse.length; i++) {
			switch (dataToParse[i]) {
			case '{':
				i = findCloseBrace(node.dataToParse, i);
				break;
				
			case ';':
				splited.add(node.dataToParse.substring(lastPlace, i));
				lastPlace = i + 1;
				break;

			default:
				break;
			}
		}
		for (String properties : splited) {
			if (properties.contains("{")) {
				int nowpt = 0;
				String first = properties.substring(nowpt, properties.indexOf('{'));
				nowpt += first.length();
				int closingBrace = findCloseBrace(properties, nowpt);
				String contained = properties.substring(nowpt + 1, closingBrace);
				MarkupNode nNode = new MarkupNode(first);
				nNode.dataToParse = contained;
				ParseTreeNode(nNode);
				node.childs.add(nNode);
			}
			else
				node.childs.add(new MarkupNode(properties));
		}
	}
	protected int findCloseBrace(String m, int nowpt)
	{
		if (m.charAt(nowpt) != '{') {
			return -1;
		}
		int count = 0;
		for (int i = nowpt+1; i < m.length(); i++) {
			if (m.charAt(i) == '}') {
				if (count == 0) {
					return i;
				}
				else count--;
			}
			else if (m.charAt(i) == '{') {
				count++;
			}
		}
		return -2;
	}
}