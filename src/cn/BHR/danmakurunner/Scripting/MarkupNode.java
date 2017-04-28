package cn.BHR.danmakurunner.Scripting;
import java.util.*;

public class MarkupNode
{
	public String data;
	public String dataToParse;
	public ArrayList<MarkupNode> childs;
	public MarkupNode()
	{
		data = "";
		dataToParse = null;
		childs = new ArrayList<MarkupNode>();
	}
	public MarkupNode(String nodedata)
	{
		data = nodedata;
		dataToParse = null;
		childs = new ArrayList<MarkupNode>();
	}
}