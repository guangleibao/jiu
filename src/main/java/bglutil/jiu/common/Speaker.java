package bglutil.jiu.common;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bglutil.jiu.Jiu;


/**
 * Output routines collection.
 * @author guanglei
 *
 */
@SuppressWarnings("serial")
public class Speaker implements Serializable {
	public enum RenderType {

		CONSOLE("console"), WEB("web");

		private String name;

		private RenderType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public static final Speaker CONSOLE = new Speaker(Speaker.RenderType.CONSOLE);
	public static final Speaker WEB = new Speaker(Speaker.RenderType.WEB);

	private RenderType renderType;
	private String renderTypeName;

	private static final String NEWLINE_WEB = "<br>";
	private static final String NEWLINE_CONSOLE = "\n";
	private static final String INDENT_WEB = "&nbsp;&nbsp;&nbsp;&nbsp;";
	private static final String INDENT_CONSOLE = "\t";
	private static final String INDENT_CONSOLE_TITLE = "    ";

	private String newLine;
	private String indent;
	private String symbol;

	/**
	 * Give me the name of renderer type: WEB, CONSOLE,...
	 * 
	 * @param typeName
	 * @return
	 */
	public static Speaker.RenderType makeRenderer(String typeName) {
		Speaker.RenderType rt = null;
		if (typeName.equalsIgnoreCase("CONSOLE")) {
			rt = Speaker.RenderType.CONSOLE;
		} else if (typeName.equalsIgnoreCase("WEB")) {
			rt = Speaker.RenderType.WEB;
		} else {
			rt = Speaker.RenderType.CONSOLE;
		}
		return rt;
	}

	public String getChild(int level) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		return new String(sb) + "[+] ";
	}

	private String makeTitlePrefix(String symbol, int symbolCount, int indent) {
		StringBuffer line = new StringBuffer();
		String prefix = null;
		if (this.renderType == RenderType.CONSOLE) {
			prefix = INDENT_CONSOLE_TITLE;
		} else {
			prefix = this.indent;
		}
		for (int i = 0; i < indent; i++) {
			line.append(prefix);
		} // end for
		for (int i = 0; i < symbolCount; i++) {
			line.append(symbol);
		} // end for
		return line.toString();
	}

	public Speaker(RenderType renderType, String symbol, String child) {
		this.setRenderType(renderType);
		this.symbol = symbol;
	}

	public Speaker(RenderType renderType) {
		this(renderType, "#", "[+] ");
	}

	public Speaker() {
		this(RenderType.CONSOLE);
	}

	public void setRenderType(RenderType renderType) {
		this.renderType = renderType;
		this.renderTypeName = this.renderType.getName();
		if (this.renderType.equals(RenderType.CONSOLE)) {
			this.newLine = NEWLINE_CONSOLE;
			this.indent = INDENT_CONSOLE;
		} else if (this.renderType.equals(RenderType.WEB)) {
			this.newLine = NEWLINE_WEB;
			this.indent = INDENT_WEB;
		} else {
			this.newLine = NEWLINE_CONSOLE;
			this.indent = INDENT_CONSOLE;
		}
	}

	/**
	 * Return the formtted title, and set the chrome symbol, and set the indent
	 * content.
	 * 
	 * @param content
	 *            The title content.
	 * @param symbol
	 *            The title delimiter symbol.
	 * @param indentCount
	 *            The indent count.
	 * @return
	 */
	public String makeTitle(int indentCount, String symbol, String content) {
		this.symbol = symbol;
		String ret = null;
		String CR = this.newLine;
		String space = null;
		if (this.renderType == RenderType.CONSOLE) {
			space = INDENT_CONSOLE_TITLE;
		} else {
			space = this.indent;
		}
		int contentLength = content.length();
		final int ceilingLength = 12 + contentLength;
		String ceil = makeTitlePrefix(this.symbol, ceilingLength, indentCount);
		String leading = makeTitlePrefix(this.symbol, 2, indentCount);
		String tail = makeTitlePrefix(this.symbol, 2, 0);
		String centerContent = leading + space + content + space + tail.trim();
		if (this.renderType.equals(RenderType.WEB)) {
			ret = centerContent + CR;
		} else {
			ret = ceil + CR + centerContent + CR + ceil + CR;
		}
		return this.newLine + ret;
	}

	public void printTitle(int indentCount, String symbol, String content) {
		System.out.println(this.makeTitle(indentCount, symbol, content));
	}

	/**
	 * Return the formatted title, and set the indent count.
	 * 
	 * @param content
	 *            The title content.
	 * @param indentCount
	 *            The indent count.
	 * @return
	 */
	public String makeTitle(int indentCount, String content) {
		return makeTitle(indentCount, this.symbol, content);
	}

	public void printTitle(int indentCount, String content) {
		System.out.println(this.makeTitle(indentCount, content));
	}

	/**
	 * Return a formatted result, set the indent count, and specify whether
	 * newline is appended.
	 * 
	 * @param <E>
	 * @param e
	 *            The result content.
	 * @param indentCount
	 *            The indent count.
	 * @param withNewLine
	 *            With or without NewLine.
	 * @return
	 */
	public <E> String makeResult(int indentCount, boolean withNewLine, E e) {
		String ret = null;
		String CR = null;
		if (withNewLine) {
			CR = this.newLine;
		} else {
			CR = "";
		}
		String prefix = this.indent;
		if (this.renderType.equals(RenderType.WEB)) {
			StringBuffer pre = new StringBuffer();
			StringBuffer suf = new StringBuffer();
			for (int i = 0; i < indentCount; i++) {
				pre.append(prefix);
			}
			ret = pre.toString() + this.getChild(1) + " " + e + suf.toString() + CR;
		} else {
			StringBuffer pre = new StringBuffer();
			for (int i = 0; i < indentCount; i++) {
				pre.append(prefix);
			}
			ret = pre.toString() + this.getChild(1) + " " + e + CR;
		}
		return ret;
	}

	public <E> void printResult(int indentCount, boolean withNewLine, E e) {
		System.out.print(this.makeResult(indentCount, withNewLine, e));
	}
	
	public <T,U> void printMap(int indent, Map<T,U> map){
		for(T t:map.keySet()){
			this.printResult(indent,true,t.toString()+": "+map.get(t).toString());
		}
	}

	public String getNewLine() {
		return this.newLine;
	}

	public void setNewLine(String newLine) {
		this.newLine = newLine;
	}

	public String getIndent() {
		return indent;
	}

	public void setIndent(String indent) {
		this.indent = indent;
	}

	public String getRenderTypeByName() {
		return renderTypeName;
	}

	public RenderType getRenderType() {
		return this.renderType;
	}

}
