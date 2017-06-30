package bglutil.jiu.common;

public class OracleCloudProduct {
	private String cat;
	private String name;
	private String alias;
	private String desc;
	private String doc;
	
	public OracleCloudProduct(String cat, String name, String alias, String desc, String doc){
		this.cat = cat;
		this.name = name;
		this.alias = alias;
		this.desc = desc;
		this.doc = doc;
	}
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String toString(){
		return "NAME: =[ "+this.name+" ]=\n"
				+"\tALIAS: "+this.alias+"\n"
				+"\tCATEGORY: < "+this.cat+" >\n"
				+"\tDOCUMENT: "+this.doc+"\n"
				+"DESCRIPTION: "+this.desc;
	}
}
