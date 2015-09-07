package com.search.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class KeyWords implements Parcelable {

	String person;
	String area;
	String category;
	String modifier;
	String name;
	String year;

	public KeyWords() {
		person = null;
		area = null;
		category = null;
		modifier = null;
		name = null;
		year = null;
	}

	@Override
	public String toString() {
		String src = "";
		src = addText(src, person);
		src = addText(src, area);
		src = addText(src, category);
		src = addText(src, modifier);
		src = addText(src, name);
		src = addText(src, year);

		return src;
	}

	private String addText(String src, String text) {
		if (!TextUtils.isEmpty(text))
			src += text + " ";
		return src;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public KeyWords(String person, String area, String category,
			String modifier, String name, String year) {

		super();
		this.person = person;
		this.area = area;
		this.category = category;
		this.modifier = modifier;
		this.name = name;
		this.year = year;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(person);
		dest.writeString(area);
		dest.writeString(category);
		dest.writeString(modifier);
		dest.writeString(name);
		dest.writeString(year);

	}

	public static final Parcelable.Creator<KeyWords> CREATOR = new Parcelable.Creator<KeyWords>() {

		@Override
		public KeyWords[] newArray(int size) {
			// TODO Auto-generated method stub
			return new KeyWords[size];
		}

		@Override
		public KeyWords createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new KeyWords(source.readString(), source.readString(),
					source.readString(), source.readString(),
					source.readString(), source.readString());
		}
	};

}
