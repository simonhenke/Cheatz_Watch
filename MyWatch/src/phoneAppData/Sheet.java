package phoneAppData;

import java.util.ArrayList;
import java.util.List;

public class Sheet {
	private List<Chapter> chapterList;
	private String name;

	public String getName() {
		return this.name;
	}

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sheet(String name) {
		this.name = name;
		this.chapterList = new ArrayList<Chapter>();
	}
	
	public void addChapter(Chapter c) {
		chapterList.add(c);
	}

}
