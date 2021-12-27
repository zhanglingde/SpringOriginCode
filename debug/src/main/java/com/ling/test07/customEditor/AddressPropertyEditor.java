package com.ling.test07.customEditor;

import java.beans.PropertyEditorSupport;

/**
 * @author zhangling  2021/12/27 21:19
 */
public class AddressPropertyEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		// 手动写的城市与下划线分隔,  浙江省_杭州市_钱塘区
		String[] s = text.split("_");
		Address address = new Address();
		address.setProvince(s[0]);
		address.setCity(s[1]);
		address.setTown(s[2]);

		this.setValue(address);
	}
}
