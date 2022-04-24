package com.ling.test07.customEditor;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import java.util.Date;

/**
 * 定义属性编辑器
 */
public class DatePropertyEditorRegistrar implements PropertyEditorRegistrar {


	@Override
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(Date.class, new AddressPropertyEditor());
	}
}
